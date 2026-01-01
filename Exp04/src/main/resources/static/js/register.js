document.addEventListener('DOMContentLoaded', () => {
    const tabs = document.querySelectorAll('.tab-item');
    const forms = document.querySelectorAll('.register-form');

    // Tab Switching
    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            // Update Tabs
            tabs.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');

            // Update Forms
            const target = tab.dataset.tab;
            forms.forEach(f => {
                f.classList.remove('active');
                if (f.id === `${target}Form`) {
                    f.classList.add('active');
                }
            });
        });
    });

    // Student Registration
    const studentForm = document.getElementById('studentForm');
    studentForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const formData = new FormData(studentForm);
        const data = Object.fromEntries(formData.entries());

        try {
            const res = await API.registerStudent(data);
            if (res.code === 200) {
                Utils.showToast('注册成功，请登录', 'success');
                setTimeout(() => {
                    window.location.href = 'index.html';
                }, 1500);
            } else {
                Utils.showToast(res.message, 'error');
            }
        } catch (error) {
            Utils.showToast('注册失败，请重试', 'error');
        }
    });

    // Teacher Registration
    const teacherForm = document.getElementById('teacherForm');
    teacherForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const formData = new FormData(teacherForm);
        const data = Object.fromEntries(formData.entries());

        try {
            const res = await API.registerTeacher(data);
            if (res.code === 200) {
                Utils.showToast('注册成功，请登录', 'success');
                setTimeout(() => {
                    window.location.href = 'index.html';
                }, 1500);
            } else {
                Utils.showToast(res.message, 'error');
            }
        } catch (error) {
            Utils.showToast('注册失败，请重试', 'error');
        }
    });
});

