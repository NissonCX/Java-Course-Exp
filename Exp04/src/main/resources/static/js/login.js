document.addEventListener('DOMContentLoaded', () => {
    // Check if already logged in
    const token = API.getToken();
    const user = API.getUser();

    if (token && user) {
        if (user.role === 'STUDENT') {
            window.location.href = 'student.html';
            return;
        } else if (user.role === 'TEACHER') {
            window.location.href = 'teacher.html';
            return;
        }
    }

    const loginForm = document.getElementById('loginForm');
    const loginBtn = document.querySelector('.login-btn');

    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const username = document.getElementById('username').value.trim();
        const password = document.getElementById('password').value.trim();

        if (!username || !password) {
            Utils.showToast('请输入账号和密码', 'error');
            return;
        }

        // Set loading state
        const originalBtnText = loginBtn.innerText;
        loginBtn.innerText = '登录中...';
        loginBtn.disabled = true;

        try {
            const res = await API.login(username, password);

            if (res.code === 200) {
                Utils.showToast('登录成功', 'success');
                API.setSession(res.data);

                // Redirect based on role
                setTimeout(() => {
                    if (res.data.role === 'STUDENT') {
                        window.location.href = 'student.html';
                    } else if (res.data.role === 'TEACHER') {
                        window.location.href = 'teacher.html';
                    } else {
                        Utils.showToast('未知用户角色', 'error');
                    }
                }, 500);
            } else {
                Utils.showToast(res.message || '登录失败', 'error');
                loginBtn.innerText = originalBtnText;
                loginBtn.disabled = false;
            }
        } catch (error) {
            Utils.showToast('网络错误或服务器异常', 'error');
            loginBtn.innerText = originalBtnText;
            loginBtn.disabled = false;
        }
    });
});
