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

    // Initial Load
    loadScores();

    // Chart instances
    let scoresChart = null;
    let subjectDistributionChart = null;
    let gradeDistributionChart = null;

    function loadCourseManage() {
        // Default to enrollments tab
        const activeTab = document.querySelector('.tab-item.active');
        if (activeTab && activeTab.dataset.tab === 'selection') {
            loadAvailableCourses();
        } else {
            loadEnrollments();
        }
    }

    // Tabs inside "选课管理" view
    const courseManageTabItems = document.querySelectorAll('#view-course-manage .tab-item');
    const courseManageTabContents = document.querySelectorAll('#view-course-manage .tab-content');

    courseManageTabItems.forEach(tab => {
        tab.addEventListener('click', () => {
            const tabKey = tab.dataset.tab;

            // Switch active tab header
            courseManageTabItems.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');

            // Switch active tab content
            courseManageTabContents.forEach(content => {
                content.classList.toggle('active', content.id === `tab-${tabKey}`);
            });

            // Load data for the selected tab
            if (tabKey === 'selection') {
                loadAvailableCourses();
            } else {
                loadEnrollments();
            }
        });
    });

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
            tbody.innerHTML = '<tr><td colspan="7" style="text-align:center">暂无可选课程</td></tr>';
            return;
        }

        list.forEach(item => {
            const tr = document.createElement('tr');
            const teachingClassId = item.teachingClassId ?? item.id;

            // 后端现在会返回所有教学班：以前端展示为主，能否选课以 canEnroll 为准
            const status = item.status ?? 1;
            const statusText = item.statusText || (status === 1 ? '未开课' : status === 2 ? '已开课' : '已结课');
            const canEnroll = Boolean(item.canEnroll) && Boolean(teachingClassId);

            tr.innerHTML = `
                <td>${item.classNo || '-'}</td>
                <td>${item.courseName || '-'}</td>
                <td>${item.teacherName || '-'}</td>
                <td>${item.semester || '-'}</td>
                <td><span class="score-badge score-mid">${statusText}</span></td>
                <td>${item.currentStudents || 0} / ${item.maxStudents || 0}</td>
                <td>
                    <button class="btn btn-primary btn-sm" onclick="handleEnroll(${teachingClassId})" ${canEnroll ? '' : 'disabled'}>${canEnroll ? '选课' : '不可选'}</button>
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
                // Generate charts after data is loaded
                generateCharts(res.data.scores);
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

    // --- Feature: Charts ---
    function generateCharts(scores) {
        // Filter scores with total score
        const validScores = scores.filter(s => s.totalScore !== null && s.totalScore !== undefined);
        
        if (validScores.length === 0) {
            // Clear charts if no data
            if (scoresChart) {
                scoresChart.destroy();
                scoresChart = null;
            }
            if (subjectDistributionChart) {
                subjectDistributionChart.destroy();
                subjectDistributionChart = null;
            }
            if (gradeDistributionChart) {
                gradeDistributionChart.destroy();
                gradeDistributionChart = null;
            }
            return;
        }

        // Common chart options
        Chart.defaults.font.family = "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif";
        Chart.defaults.color = '#64748b';

        // 1. 柱状图 - 各科目总评成绩
        const ctx1 = document.getElementById('scoresChart').getContext('2d');
        if (scoresChart) {
            scoresChart.destroy();
        }
        
        scoresChart = new Chart(ctx1, {
            type: 'bar',
            data: {
                labels: validScores.map(s => s.courseName),
                datasets: [{
                    label: '总评成绩',
                    data: validScores.map(s => s.totalScore),
                    backgroundColor: validScores.map(s => {
                        if (s.totalScore >= 90) return 'rgba(34, 197, 94, 0.8)'; // Green-500
                        if (s.totalScore >= 80) return 'rgba(59, 130, 246, 0.8)'; // Blue-500
                        if (s.totalScore >= 60) return 'rgba(245, 158, 11, 0.8)'; // Amber-500
                        return 'rgba(239, 68, 68, 0.8)'; // Red-500
                    }),
                    borderRadius: 6,
                    borderSkipped: false,
                    barPercentage: 0.6,
                    categoryPercentage: 0.8
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false },
                    title: {
                        display: true,
                        text: '课程成绩概览',
                        font: { size: 16, weight: '600' },
                        color: '#1e293b',
                        padding: { bottom: 20 }
                    },
                    tooltip: {
                        backgroundColor: 'rgba(15, 23, 42, 0.9)',
                        padding: 12,
                        cornerRadius: 8,
                        callbacks: {
                            label: (ctx) => `成绩: ${ctx.parsed.y}分`
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        max: 100,
                        grid: {
                            color: '#f1f5f9',
                            drawBorder: false
                        },
                        ticks: { font: { size: 11 } }
                    },
                    x: {
                        grid: { display: false },
                        ticks: { font: { size: 11 } }
                    }
                },
                animation: {
                    duration: 1000,
                    easing: 'easeOutQuart'
                }
            }
        });

        // 2. 雷达图 - 学科能力分析 (替代原来的饼图)
        const ctx2 = document.getElementById('subjectDistributionChart').getContext('2d');
        if (subjectDistributionChart) {
            subjectDistributionChart.destroy();
        }

        subjectDistributionChart = new Chart(ctx2, {
            type: 'radar',
            data: {
                labels: validScores.map(s => s.courseName),
                datasets: [{
                    label: '成绩',
                    data: validScores.map(s => s.totalScore),
                    backgroundColor: 'rgba(99, 102, 241, 0.2)', // Indigo-500 with opacity
                    borderColor: '#6366f1', // Indigo-500
                    pointBackgroundColor: '#6366f1',
                    pointBorderColor: '#fff',
                    pointHoverBackgroundColor: '#fff',
                    pointHoverBorderColor: '#6366f1',
                    borderWidth: 2,
                    pointRadius: 3
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    r: {
                        angleLines: { color: '#e2e8f0' },
                        grid: { color: '#e2e8f0' },
                        pointLabels: {
                            font: { size: 11 },
                            color: '#475569'
                        },
                        suggestedMin: 0,
                        suggestedMax: 100,
                        ticks: { display: false, stepSize: 20 }
                    }
                },
                plugins: {
                    legend: { display: false },
                    title: {
                        display: true,
                        text: '学科能力雷达图',
                        font: { size: 16, weight: '600' },
                        color: '#1e293b',
                        padding: { bottom: 10 }
                    },
                    tooltip: {
                        backgroundColor: 'rgba(15, 23, 42, 0.9)',
                        padding: 10,
                        cornerRadius: 6
                    }
                }
            }
        });

        // 3. 环形图 - 成绩等级分布
        const excellentCount = validScores.filter(s => s.totalScore >= 90).length;
        const goodCount = validScores.filter(s => s.totalScore >= 80 && s.totalScore < 90).length;
        const passCount = validScores.filter(s => s.totalScore >= 60 && s.totalScore < 80).length;
        const failCount = validScores.filter(s => s.totalScore < 60).length;

        const ctx3 = document.getElementById('gradeDistributionChart').getContext('2d');
        if (gradeDistributionChart) {
            gradeDistributionChart.destroy();
        }

        gradeDistributionChart = new Chart(ctx3, {
            type: 'doughnut',
            data: {
                labels: ['优秀 (90+)', '良好 (80-89)', '及格 (60-79)', '不及格 (<60)'],
                datasets: [{
                    data: [excellentCount, goodCount, passCount, failCount],
                    backgroundColor: [
                        '#22c55e', // Green-500
                        '#3b82f6', // Blue-500
                        '#f59e0b', // Amber-500
                        '#ef4444'  // Red-500
                    ],
                    borderWidth: 0,
                    hoverOffset: 4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                cutout: '70%',
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {
                            usePointStyle: true,
                            padding: 15,
                            font: { size: 11 },
                            boxWidth: 8
                        }
                    },
                    title: {
                        display: true,
                        text: '成绩等级分布',
                        font: { size: 16, weight: '600' },
                        color: '#1e293b',
                        padding: { bottom: 10 }
                    },
                    tooltip: {
                        backgroundColor: 'rgba(15, 23, 42, 0.9)',
                        padding: 10,
                        cornerRadius: 6,
                        callbacks: {
                            label: function(context) {
                                const value = context.parsed;
                                const total = context.dataset.data.reduce((a, b) => a + b, 0);
                                const percentage = total ? ((value / total) * 100).toFixed(1) + '%' : '0%';
                                return ` ${context.label}: ${value}门 (${percentage})`;
                            }
                        }
                    }
                },
                animation: {
                    duration: 1000,
                    easing: 'easeOutQuart'
                }
            }
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

            const status = item.teachingClassStatus ?? 1;
            const statusText = item.teachingClassStatusText || (status === 1 ? '未开课' : status === 2 ? '已开课' : '已结课');
            const canDrop = status === 1 && !item.hasScore;

            tr.innerHTML = `
                <td>${item.classNo || '-'}</td>
                <td>${item.courseName || '-'}</td>
                <td>${item.teacherName || '-'}</td>
                <td>${item.semester || '-'}</td>
                <td><span class="score-badge score-mid">${statusText}</span></td>
                <td>
                    <button class="btn btn-danger btn-sm" onclick="handleDropCourse(${item.enrollmentId})" ${canDrop ? '' : 'disabled'}>${canDrop ? '退课' : (item.hasScore ? '已出分' : '不可退')}</button>
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
        aiMessageDiv.className = 'message.ai';
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
