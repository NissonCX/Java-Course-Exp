document.addEventListener('DOMContentLoaded', () => {
    // Auth Check
    const user = API.getUser();
    if (!user || user.role !== 'STUDENT') {
        window.location.href = 'index.html';
        return;
    }

    // Set UI User Info
    document.getElementById('userName').textContent = user.realName;

    // Logout Handler
    document.getElementById('logoutBtn').addEventListener('click', () => {
        API.logout();
    });

    // Navigation Handler
    const navItems = document.querySelectorAll('.nav-item');
    const sections = document.querySelectorAll('.view-section');

    navItems.forEach(item => {
        item.addEventListener('click', (e) => {
            e.preventDefault();
            const viewId = item.dataset.view;

            // Update Nav State
            navItems.forEach(nav => nav.classList.remove('active'));
            item.classList.add('active');

            // Update View State
            sections.forEach(section => {
                section.style.display = section.id === `view-${viewId}` ? 'block' : 'none';
            });

            // Load Data based on View
            if (viewId === 'scores') loadScores();
            if (viewId === 'course-manage') loadCourseManage();
            if (viewId === 'profile') loadProfile();
        });
    });

    // Tab Handler for Course Management
    const tabs = document.querySelectorAll('.tab-item');
    const tabContents = document.querySelectorAll('.tab-content');

    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            // Update Tabs
            tabs.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');

            // Update Content
            const target = tab.dataset.tab;
            tabContents.forEach(c => {
                c.classList.remove('active');
                if (c.id === `tab-${target}`) {
                    c.classList.add('active');
                }
            });

            // Load Data
            if (target === 'enrollments') loadEnrollments();
            if (target === 'selection') loadAvailableCourses();
        });
    });

    // Initial Load
    loadScores();

    function loadCourseManage() {
        // Default to enrollments tab
        const activeTab = document.querySelector('.tab-item.active');
        if (activeTab && activeTab.dataset.tab === 'selection') {
            loadAvailableCourses();
        } else {
            loadEnrollments();
        }
    }

    // --- Feature: Profile ---
    async function loadProfile() {
        showLoading(true);
        try {
            const res = await API.getStudentProfile();
            if (res.code === 200) {
                const s = res.data;
                document.getElementById('profileStudentNo').value = s.studentNo || '';
                document.getElementById('profileName').value = s.name || '';
                document.getElementById('profileClass').value = s.className || '';
                document.getElementById('profileEmail').value = s.email || '';
                document.getElementById('profilePhone').value = s.phone || '';
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
            const res = await API.updateStudentProfile(data);
            if (res.code === 200) {
                Utils.showToast('更新成功', 'success');
            } else {
                Utils.showToast(res.message, 'error');
            }
        } catch (error) {
            Utils.showToast('更新失败', 'error');
        }
    });

    // --- Feature: Course Selection ---
    document.getElementById('searchCoursesBtn').addEventListener('click', () => loadAvailableCourses());

    async function loadAvailableCourses() {
        showLoading(true);
        const semester = document.getElementById('semesterInput').value.trim();
        try {
            const res = await API.getAvailableClasses(semester);
            if (res.code === 200) {
                renderAvailableCourses(res.data);
            }
        } catch (error) {
            Utils.showToast('加载可选课程失败', 'error');
        } finally {
            showLoading(false);
        }
    }

    function renderAvailableCourses(list) {
        const tbody = document.querySelector('#availableCoursesTable tbody');
        tbody.innerHTML = '';

        if (!list || list.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" style="text-align:center">暂无可选课程</td></tr>';
            return;
        }

        list.forEach(item => {
            const tr = document.createElement('tr');
            const teachingClassId = item.teachingClassId ?? item.id;
            tr.innerHTML = `
                <td>${item.classNo || '-'}</td>
                <td>${item.courseName || '-'}</td>
                <td>${item.teacherName || '-'}</td>
                <td>${item.semester || '-'}</td>
                <td>${item.currentStudents || 0} / ${item.maxStudents || 0}</td>
                <td>
                    <button class="btn btn-primary btn-sm" onclick="handleEnroll(${teachingClassId})" ${teachingClassId ? '' : 'disabled'}>选课</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    }

    window.handleEnroll = async (teachingClassId) => {
        if (!teachingClassId) {
            Utils.showToast('选课失败：teachingClassId 为空（前端字段未对齐）', 'error');
            return;
        }
        if (!confirm('确定要选择这门课程吗？')) return;
        try {
            const res = await API.enrollCourse(teachingClassId);
            if (res.code === 200) {
                Utils.showToast('选课成功', 'success');
                // 刷新两个tab的数据
                loadAvailableCourses();
                loadEnrollments();
            } else {
                Utils.showToast(res.message, 'error');
            }
        } catch (error) {
            Utils.showToast('选课失败', 'error');
        }
    };

    // --- Feature: My Scores ---
    async function loadScores() {
        showLoading(true);
        try {
            const res = await API.getStudentScores();
            if (res.code === 200) {
                renderStats(res.data);
                renderScoresTable(res.data.scores);
            } else {
                Utils.showToast(res.message, 'error');
            }
        } catch (error) {
            Utils.showToast('加载成绩失败', 'error');
        } finally {
            showLoading(false);
        }
    }

    function renderStats(data) {
        document.getElementById('avgScore').textContent = data.averageScore?.toFixed(1) || '0.0';
        document.getElementById('avgGPA').textContent = data.gpa?.toFixed(2) || '0.00';
        document.getElementById('totalCredits').textContent = data.completedCourses || '0';
        document.getElementById('totalCourses').textContent = data.totalCourses || '0';
    }

    function renderScoresTable(scores) {
        const tbody = document.querySelector('#scoresTable tbody');
        tbody.innerHTML = '';

        if (!scores || scores.length === 0) {
            tbody.innerHTML = '<tr><td colspan="9" style="text-align:center">暂无成绩记录</td></tr>';
            return;
        }

        scores.forEach(s => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${s.courseName || '-'}</td>
                <td>${s.credit || '-'}</td>
                <td>${s.semester || '-'}</td>
                <td>${Utils.formatScore(s.usualScore)}</td>
                <td>${Utils.formatScore(s.midtermScore)}</td>
                <td>${Utils.formatScore(s.experimentScore)}</td>
                <td>${Utils.formatScore(s.finalScore)}</td>
                <td><span class="score-badge ${Utils.getScoreClass(s.totalScore)}">${Utils.formatScore(s.totalScore)}</span></td>
                <td>${s.gradePoint !== null ? s.gradePoint.toFixed(1) : '-'}</td>
            `;
            tbody.appendChild(tr);
        });
    }

    // --- Feature: Enrollments ---
    async function loadEnrollments() {
        showLoading(true);
        try {
            const res = await API.getStudentEnrollments();
            if (res.code === 200) {
                renderEnrollmentsTable(res.data);
            } else {
                Utils.showToast(res.message, 'error');
            }
        } catch (error) {
            Utils.showToast('加载选课列表失败', 'error');
        } finally {
            showLoading(false);
        }
    }

    function renderEnrollmentsTable(list) {
        const tbody = document.querySelector('#enrollmentsTable tbody');
        tbody.innerHTML = '';

        if (!list || list.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" style="text-align:center">暂无选课记录</td></tr>';
            return;
        }

        list.forEach(item => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${item.classNo || '-'}</td>
                <td>${item.courseName || '-'}</td>
                <td>${item.teacherName || '-'}</td>
                <td>${item.semester || '-'}</td>
                <td><span class="score-badge score-mid">已选</span></td>
                <td>
                    <button class="btn btn-danger btn-sm" onclick="handleDropCourse(${item.enrollmentId})">退课</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    }

    // Expose to window for onclick
    window.handleDropCourse = async (enrollmentId) => {
        if (!confirm('确定要退掉这门课吗？')) return;

        try {
            const res = await API.dropCourse(enrollmentId);
            if (res.code === 200) {
                Utils.showToast('退课成功', 'success');
                loadEnrollments();
            } else {
                Utils.showToast(res.message, 'error');
            }
        } catch (error) {
            Utils.showToast('操作失败', 'error');
        }
    };

    // --- Feature: AI Advisor ---
    const chatInput = document.getElementById('chatInput');
    const sendBtn = document.getElementById('sendMessageBtn');
    const chatMessages = document.getElementById('chatMessages');

    let currentEventSource = null; // Track current SSE connection

    function addMessage(text, type) {
        const div = document.createElement('div');
        div.className = `message ${type}`;
        div.innerHTML = formatAIMessage(text);
        chatMessages.appendChild(div);
        chatMessages.scrollTop = chatMessages.scrollHeight;
        return div;
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

    async function handleSendMessage() {
        const text = chatInput.value.trim();
        if (!text) return;

        addMessage(text, 'user');
        chatInput.value = '';

        // Close existing connection if any
        if (currentEventSource) {
            currentEventSource.close();
        }

        // Create AI message div that will be updated
        const aiMessageDiv = document.createElement('div');
        aiMessageDiv.className = 'message ai';
        aiMessageDiv.innerHTML = '';
        chatMessages.appendChild(aiMessageDiv);
        chatMessages.scrollTop = chatMessages.scrollHeight;

        const token = API.getToken();
        const encodedMessage = encodeURIComponent(text);

        try {
            const response = await fetch(
                `${API.BASE_URL}/student/ai/consult/stream?message=${encodedMessage}`,
                {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                }
            );

            if (!response.ok) {
                const errorText = await response.text();
                console.error('HTTP Error:', response.status, errorText);
                aiMessageDiv.textContent = `请求失败 (${response.status}): ${errorText.substring(0, 100)}`;
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
                            chatMessages.scrollTop = chatMessages.scrollHeight;
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
                        chatMessages.scrollTop = chatMessages.scrollHeight;
                    }
                }
            }

            // 如果没有收到任何内容，显示提示
            if (!aiMessageDiv.innerHTML.trim()) {
                aiMessageDiv.innerHTML = formatAIMessage('未收到回复，请重试');
            }

        } catch (error) {
            console.error('Streaming error:', error);
            aiMessageDiv.textContent = `连接错误: ${error.message || '网络请求失败，请稍后重试'}`;
        }
    }

    sendBtn.addEventListener('click', handleSendMessage);
    chatInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') handleSendMessage();
    });

    // Helper
    function showLoading(show) {
        document.getElementById('loading').style.display = show ? 'block' : 'none';
    }
});
