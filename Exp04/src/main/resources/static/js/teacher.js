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
    }

    function updateNav(activeItem) {
        navItems.forEach(i => i.classList.remove('active'));
        activeItem.classList.add('active');
    }

    // Load Data
    loadClasses();

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
                        <span class="course-title">${c.courseName}</span>
                        <span class="tag">${c.term}</span>
                    </div>
                    <div class="course-code">${c.classNo}</div>
                </div>
                <div class="course-body">
                    <p>选课人数: ${c.enrollCount || 0}</p>
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
        document.getElementById('currentClassName').textContent = `${cls.courseName} (${cls.classNo})`;
        showView('class-detail');

        // Load students and stats parallel
        showLoading(true);
        await Promise.all([loadClassScores(cls.id), loadClassStats(cls.id)]);
        showLoading(false);
    }

    async function loadClassScores(classId) {
        try {
            const res = await API.getClassScores(classId);
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

    function renderScoreTable(scores) {
        const tbody = document.querySelector('#scoreInputTable tbody');
        tbody.innerHTML = '';

        scores.forEach(s => {
            const tr = document.createElement('tr');
            tr.dataset.studentId = s.studentId;
            tr.innerHTML = `
                <td>${s.studentNo}</td>
                <td>${s.studentName}</td>
                <td><input type="number" class="input-score" name="usual" value="${s.usualScore || ''}" min="0" max="100"></td>
                <td><input type="number" class="input-score" name="midterm" value="${s.midtermScore || ''}" min="0" max="100"></td>
                <td><input type="number" class="input-score" name="experiment" value="${s.experimentScore || ''}" min="0" max="100"></td>
                <td><input type="number" class="input-score" name="final" value="${s.finalScore || ''}" min="0" max="100"></td>
                <td><span class="score-badge ${Utils.getScoreClass(s.totalScore)}">${s.totalScore || '-'}</span></td>
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
            opt.textContent = `${c.courseName} (${c.classNo})`;
            select.appendChild(opt);
        });
    }

    const analysisInput = document.getElementById('analysisInput');
    const sendAnalysisBtn = document.getElementById('sendAnalysisBtn');
    const analysisMessages = document.getElementById('analysisMessages');

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

        const loadId = 'loading-' + Date.now();
        addMsg('分析中...', 'ai', loadId);

        try {
            const res = await API.teacherAiConsult(parseInt(classId), text);
            document.getElementById(loadId).remove();
            if (res.code === 200) {
                addMsg(res.data, 'ai');
            } else {
                addMsg('AI服务异常: ' + res.message, 'ai');
            }
        } catch (e) {
            document.getElementById(loadId).remove();
            addMsg('网络错误', 'ai');
        }
    }

    function addMsg(text, type, id) {
        const div = document.createElement('div');
        div.className = `message ${type}`;
        if (id) div.id = id;
        div.textContent = text;
        analysisMessages.appendChild(div);
        analysisMessages.scrollTop = analysisMessages.scrollHeight;
    }

    sendAnalysisBtn.addEventListener('click', handleAnalysis);
    analysisInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') handleAnalysis();
    });

    function showLoading(show) {
        document.getElementById('loading').style.display = show ? 'block' : 'none';
    }
});
