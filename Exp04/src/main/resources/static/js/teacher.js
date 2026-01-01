document.addEventListener('DOMContentLoaded', () => {
    // Auth Check
    const user = API.getUser();
    if (!user || user.role !== 'TEACHER') {
        window.location.href = 'index.html';
        return;
    }

    // State
    let currentClassId = null;
    let myClasses = [];

    // Init UI
    document.getElementById('userName').textContent = user.realName;

    // Handlers
    document.getElementById('logoutBtn').addEventListener('click', () => API.logout());
    document.getElementById('backToClassesBtn').addEventListener('click', () => showView('classes'));

    // Navigation
    const navItems = document.querySelectorAll('.nav-item');
    navItems.forEach(item => {
        item.addEventListener('click', (e) => {
            e.preventDefault();
            const view = item.dataset.view;
            showView(view);
            updateNav(item);
        });
    });

    function showView(viewId) {
        document.querySelectorAll('.view-section').forEach(el => el.style.display = 'none');
        document.getElementById(`view-${viewId}`).style.display = 'block';

        if (viewId === 'classes') loadClasses();
        if (viewId === 'analysis') setupAnalysisDropdown();
        if (viewId === 'profile') loadProfile();
    }

    function updateNav(activeItem) {
        navItems.forEach(i => i.classList.remove('active'));
        activeItem.classList.add('active');
    }

    // Load Data
    loadClasses();

    // --- Feature: Profile ---
    async function loadProfile() {
        showLoading(true);
        try {
            const res = await API.getTeacherProfile();
            if (res.code === 200) {
                const t = res.data;
                document.getElementById('profileTeacherNo').value = t.teacherNo || '';
                document.getElementById('profileName').value = t.name || '';
                document.getElementById('profileEmail').value = t.email || '';
                document.getElementById('profilePhone').value = t.phone || '';
            }
        } catch (error) {
            Utils.showToast('加载个人信息失败', 'error');
        } finally {
            showLoading(false);
        }
    }

    document.getElementById('profileForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const data = {
            email: document.getElementById('profileEmail').value,
            phone: document.getElementById('profilePhone').value
        };
        try {
            const res = await API.updateTeacherProfile(data);
            if (res.code === 200) {
                Utils.showToast('更新成功', 'success');
            } else {
                Utils.showToast(res.message, 'error');
            }
        } catch (error) {
            Utils.showToast('更新失败', 'error');
        }
    });

    // --- Feature: Class List ---
    async function loadClasses() {
        showLoading(true);
        try {
            const res = await API.getTeacherClasses();
            if (res.code === 200) {
                myClasses = res.data;
                renderClassesGrid(res.data);
            }
        } catch (error) {
            Utils.showToast('加载班级失败', 'error');
        } finally {
            showLoading(false);
        }
    }

    function renderClassesGrid(classes) {
        const grid = document.getElementById('classesGrid');
        grid.innerHTML = '';

        if (!classes || classes.length === 0) {
            grid.innerHTML = '<p>暂无教学班级</p>';
            return;
        }

        classes.forEach(c => {
            const card = document.createElement('div');
            card.className = 'course-card';
            card.style.cursor = 'pointer';
            card.innerHTML = `
                <div class="course-header">
                    <div class="class-card-header">
                        <span class="course-title">${c.course?.courseName || '未知课程'}</span>
                        <span class="tag">${c.semester || '-'}</span>
                    </div>
                    <div class="course-code">${c.classNo || '-'}</div>
                </div>
                <div class="course-body">
                    <p>选课人数: ${c.currentStudents || 0}/${c.maxStudents || 0}</p>
                    <button class="btn btn-primary" style="margin-top: 1rem; width: 100%;">管理成绩</button>
                </div>
            `;
            card.addEventListener('click', () => openClassDetail(c));
            grid.appendChild(card);
        });
    }

    // --- Feature: Class Detail & Score Input ---
    async function openClassDetail(cls) {
        currentClassId = cls.id;
        const courseName = cls.course?.courseName || '未知课程';
        document.getElementById('currentClassName').textContent = `${courseName} (${cls.classNo || '-'})`;
        showView('class-detail');

        // Load students and stats parallel
        showLoading(true);
        await Promise.all([loadClassScores(cls.id), loadClassStats(cls.id)]);
        showLoading(false);
    }

    // Batch Save Handler
    document.getElementById('saveAllScoresBtn').addEventListener('click', async () => {
        if (!currentClassId) return;

        const rows = document.querySelectorAll('#scoreInputTable tbody tr');
        const scores = [];

        rows.forEach(tr => {
            const studentId = tr.dataset.studentId;
            if (studentId) {
                scores.push({
                    studentId: parseInt(studentId),
                    usualScore: val(tr, 'usual'),
                    midtermScore: val(tr, 'midterm'),
                    experimentScore: val(tr, 'experiment'),
                    finalScore: val(tr, 'final')
                });
            }
        });

        if (scores.length === 0) {
            Utils.showToast('没有可保存的数据', 'info');
            return;
        }

        showLoading(true);
        try {
            const res = await API.batchInputScores(currentClassId, scores);
            if (res.code === 200) {
                Utils.showToast('批量保存成功', 'success');
                loadClassScores(currentClassId);
                loadClassStats(currentClassId);
            } else {
                Utils.showToast(res.message, 'error');
            }
        } catch (e) {
            Utils.showToast('批量保存失败', 'error');
        } finally {
            showLoading(false);
        }
    });

    async function loadClassScores(classId) {
        try {
            const res = await API.getClassStudents(classId);
            if (res.code === 200) {
                renderScoreTable(res.data);
            }
        } catch (e) {
            console.error(e);
        }
    }

    async function loadClassStats(classId) {
        try {
            const res = await API.getClassStatistics(classId);
            if (res.code === 200) {
                const s = res.data;
                const grid = document.getElementById('classStatsGrid');
                grid.innerHTML = `
                    <div class="stat-card"><div class="stat-title">平均分</div><div class="stat-value">${s.averageScore || '-'}</div></div>
                    <div class="stat-card"><div class="stat-title">及格率</div><div class="stat-value">${s.passRate}%</div></div>
                    <div class="stat-card"><div class="stat-title">最高分</div><div class="stat-value">${s.highestScore || '-'}</div></div>
                    <div class="stat-card"><div class="stat-title">优秀率</div><div class="stat-value">${s.excellentRate}%</div></div>
                `;
            }
        } catch (e) {
            console.error(e);
        }
    }

    function renderScoreTable(students) {
        const tbody = document.querySelector('#scoreInputTable tbody');
        tbody.innerHTML = '';

        if(!students || students.length === 0) {
            tbody.innerHTML = '<tr><td colspan="8" style="text-align:center">暂无学生数据</td></tr>';
            return;
        }

        students.forEach(s => {
            const tr = document.createElement('tr');
            // getClassStudents 返回的数据结构中 studentId 字段直接在顶层
            tr.dataset.studentId = s.studentId;
            // 使用 studentNo 和 studentName 字段，而不是 student 对象
            const studentNo = s.studentNo || '-';
            const studentName = s.studentName || '-';
            tr.innerHTML = `
                <td>${studentNo}</td>
                <td>${studentName}</td>
                <td><input type="number" class="input-score" name="usual" value="${s.usualScore || ''}" min="0" max="100"></td>
                <td><input type="number" class="input-score" name="midterm" value="${s.midtermScore || ''}" min="0" max="100"></td>
                <td><input type="number" class="input-score" name="experiment" value="${s.experimentScore || ''}" min="0" max="100"></td>
                <td><input type="number" class="input-score" name="final" value="${s.finalScore || ''}" min="0" max="100"></td>
                <td><span class="score-badge ${Utils.getScoreClass(s.totalScore)}">${s.totalScore ? s.totalScore.toFixed(1) : '-'}</span></td>
                <td><button class="btn btn-primary btn-sm save-row-btn">保存</button></td>
            `;

            // Single Row Save
            tr.querySelector('.save-row-btn').addEventListener('click', () => saveRowScore(tr));
            tbody.appendChild(tr);
        });
    }

    async function saveRowScore(tr) {
        const studentId = tr.dataset.studentId;
        const data = {
            teachingClassId: currentClassId,
            studentId: parseInt(studentId),
            usualScore: val(tr, 'usual'),
            midtermScore: val(tr, 'midterm'),
            experimentScore: val(tr, 'experiment'),
            finalScore: val(tr, 'final')
        };

        try {
            const res = await API.inputScore(data);
            if (res.code === 200) {
                Utils.showToast('保存成功', 'success');
                // Refresh to calculate total
                loadClassScores(currentClassId);
                loadClassStats(currentClassId);
            } else {
                Utils.showToast(res.message, 'error');
            }
        } catch (e) {
            Utils.showToast('保存失败', 'error');
        }
    }

    function val(tr, name) {
        const v = tr.querySelector(`input[name="${name}"]`).value;
        return v === '' ? null : parseFloat(v);
    }

    // --- Feature: Analysis ---
    function setupAnalysisDropdown() {
        const select = document.getElementById('analysisClassSelect');
        select.innerHTML = '<option value="">请选择班级...</option>';
        myClasses.forEach(c => {
            const opt = document.createElement('option');
            opt.value = c.id;
            const courseName = c.course?.courseName || '未知课程';
            opt.textContent = `${courseName} (${c.classNo || '-'})`;
            select.appendChild(opt);
        });
    }

    const analysisInput = document.getElementById('analysisInput');
    const sendAnalysisBtn = document.getElementById('sendAnalysisBtn');
    const analysisMessages = document.getElementById('analysisMessages');

    let currentEventSource = null; // Track current SSE connection

    async function handleAnalysis() {
        const classId = document.getElementById('analysisClassSelect').value;
        const text = analysisInput.value.trim();

        if (!classId) {
            Utils.showToast('请先选择教学班', 'info');
            return;
        }
        if (!text) return;

        addMsg(text, 'user');
        analysisInput.value = '';

        // Close existing connection if any
        if (currentEventSource) {
            currentEventSource.close();
        }

        // Create AI message div that will be updated
        const aiMessageDiv = document.createElement('div');
        aiMessageDiv.className = 'message ai';
        aiMessageDiv.innerHTML = '';
        analysisMessages.appendChild(aiMessageDiv);
        analysisMessages.scrollTop = analysisMessages.scrollHeight;

        const token = API.getToken();
        const encodedMessage = encodeURIComponent(text);

        try {
            const response = await fetch(
                `${API.BASE_URL}/teacher/ai/consult/stream?teachingClassId=${classId}&message=${encodedMessage}`,
                {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                }
            );

            if (!response.ok) {
                const errorText = await response.text();
                console.error('HTTP Error:', response.status, errorText);
                aiMessageDiv.textContent = `请求失败 (${response.status})`;
                return;
            }

            const reader = response.body.getReader();
            const decoder = new TextDecoder();
            let buffer = ''; // 用于处理不完整的数据块

            while (true) {
                const { done, value } = await reader.read();
                if (done) {
                    console.log('Stream completed');
                    break;
                }

                // 解码并添加到缓冲区
                buffer += decoder.decode(value, { stream: true });

                // 按行分割
                const lines = buffer.split('\n');

                // 保留最后一个可能不完整的行
                buffer = lines.pop() || '';

                for (const line of lines) {
                    if (line.startsWith('data:')) {
                        let data = line.substring(5);
                        // 处理SSE协议中的空格（如果存在）
                        if (data.startsWith(' ')) {
                            data = data.substring(1);
                        }
                        // 移除可能的行尾回车符
                        if (data.endsWith('\r')) {
                            data = data.substring(0, data.length - 1);
                        }

                        if (data && data !== '[DONE]') {
                            // 累积原始文本
                            aiMessageDiv.dataset.rawText = (aiMessageDiv.dataset.rawText || '') + data;
                            // 格式化显示
                            aiMessageDiv.innerHTML = formatAIMessage(aiMessageDiv.dataset.rawText);
                            analysisMessages.scrollTop = analysisMessages.scrollHeight;
                        }
                    } else if (line.trim() === '') {
                        // SSE事件之间的空行
                        continue;
                    }
                }
            }

            // 处理缓冲区中剩余的数据
            if (buffer.trim()) {
                if (buffer.startsWith('data:')) {
                    let data = buffer.substring(5);
                    if (data.startsWith(' ')) {
                        data = data.substring(1);
                    }
                    if (data.endsWith('\r')) {
                        data = data.substring(0, data.length - 1);
                    }
                    
                    if (data && data !== '[DONE]') {
                        // 累积原始文本
                        aiMessageDiv.dataset.rawText = (aiMessageDiv.dataset.rawText || '') + data;
                        // 格式化显示
                        aiMessageDiv.innerHTML = formatAIMessage(aiMessageDiv.dataset.rawText);
                        analysisMessages.scrollTop = analysisMessages.scrollHeight;
                    }
                }
            }

            // 如果没有收到任何内容，显示提示
            if (!aiMessageDiv.innerHTML.trim()) {
                aiMessageDiv.innerHTML = formatAIMessage('未收到回复，请重试');
            }

        } catch (e) {
            console.error('Streaming error:', e);
            aiMessageDiv.textContent = `连接错误: ${e.message || '网络错误'}`;
        }
    }

    function addMsg(text, type, id) {
        const div = document.createElement('div');
        div.className = `message ${type}`;
        if (id) div.id = id;
        div.innerHTML = formatAIMessage(text);
        analysisMessages.appendChild(div);
        analysisMessages.scrollTop = analysisMessages.scrollHeight;
    }

    // 格式化AI消息，使用marked库处理Markdown
    function formatAIMessage(text) {
        if (!text) return '';
        try {
            // 预处理：修复常见的格式问题以优化 Markdown 解析
            let processed = text
                // 1. 确保标题标记前有换行，且 # 后有空格
                .replace(/([^\n])\s*(#{1,6})([^\s#])/g, '$1\n\n$2 $3')
                .replace(/([^\n])\s*(#{1,6})\s+/g, '$1\n\n$2 ')
                // 2. 确保列表项前有换行
                .replace(/([^\n])\s*(\-\s)/g, '$1\n\n$2')
                // 3. 确保数字列表项前有换行 (1. xxx)
                .replace(/([^\n])\s*(\d+\.\s)/g, '$1\n\n$2');

            // 启用换行符转 <br>，确保单换行也能显示
            return marked.parse(processed, { breaks: true });
        } catch (e) {
            console.error('Markdown parsing error:', e);
            return text;
        }
    }

    sendAnalysisBtn.addEventListener('click', handleAnalysis);
    analysisInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') handleAnalysis();
    });

    function showLoading(show) {
        document.getElementById('loading').style.display = show ? 'block' : 'none';
    }
});
