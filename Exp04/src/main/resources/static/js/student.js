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
            if (viewId === 'enrollments') loadEnrollments();
        });
    });

    // Initial Load
    loadScores();

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
        document.getElementById('avgScore').textContent = data.averageScore?.toFixed(1) || '-';
        document.getElementById('avgGPA').textContent = data.averageGPA?.toFixed(2) || '-';
        document.getElementById('totalCredits').textContent = data.totalCredits || '0';
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
                <td>${s.courseName}</td>
                <td>${s.credit}</td>
                <td>${s.term}</td>
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
                <td>${item.courseName}</td>
                <td>${item.teacherName}</td>
                <td>${item.term}</td>
                <td><span class="score-badge score-mid">已选</span></td>
                <td>
                    <button class="btn btn-danger btn-sm" onclick="handleDropCourse(${item.id})">退课</button>
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

    function addMessage(text, type) {
        const div = document.createElement('div');
        div.className = `message ${type}`;
        div.textContent = text;
        chatMessages.appendChild(div);
        chatMessages.scrollTop = chatMessages.scrollHeight;
    }

    async function handleSendMessage() {
        const text = chatInput.value.trim();
        if (!text) return;

        addMessage(text, 'user');
        chatInput.value = '';

        // Add loading placeholder
        const loadingId = 'loading-' + Date.now();
        const loadingDiv = document.createElement('div');
        loadingDiv.className = 'message ai';
        loadingDiv.id = loadingId;
        loadingDiv.textContent = '思考中...';
        chatMessages.appendChild(loadingDiv);
        chatMessages.scrollTop = chatMessages.scrollHeight;

        try {
            const res = await API.studentAiConsult(text);
            document.getElementById(loadingId).remove();

            if (res.code === 200) {
                addMessage(res.data, 'ai');
            } else {
                addMessage('抱歉，AI服务暂时不可用：' + res.message, 'ai');
            }
        } catch (error) {
            document.getElementById(loadingId).remove();
            addMessage('网络请求失败，请稍后重试', 'ai');
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
