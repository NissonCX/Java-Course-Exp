document.addEventListener('DOMContentLoaded', () => {
    const user = API.getUser();
    if (!user || user.role !== 'SUPER_ADMIN') {
        window.location.href = 'index.html';
        return;
    }

    // Init UI
    document.getElementById('userName').textContent = user.realName || user.username || 'SUPER_ADMIN';

    document.getElementById('logoutBtn').addEventListener('click', () => API.logout());

    const navItems = document.querySelectorAll('.nav-item');
    const sections = document.querySelectorAll('.view-section');

    const teachingCourseSelect = document.getElementById('teachingCourseSelect');
    const teachingTeacherSelect = document.getElementById('teachingTeacherSelect');
    let teachingListsLoaded = false;

    function setSelectLoading(selectEl, loadingText) {
        if (!selectEl) return;
        selectEl.innerHTML = '';
        const opt = document.createElement('option');
        opt.value = '';
        opt.textContent = loadingText;
        selectEl.appendChild(opt);
    }

    function fillSelect(selectEl, items, getValue, getLabel, placeholder) {
        if (!selectEl) return;
        selectEl.innerHTML = '';

        const placeholderOpt = document.createElement('option');
        placeholderOpt.value = '';
        placeholderOpt.textContent = placeholder;
        selectEl.appendChild(placeholderOpt);

        (items || []).forEach(item => {
            const opt = document.createElement('option');
            opt.value = String(getValue(item));
            opt.textContent = getLabel(item);
            selectEl.appendChild(opt);
        });
    }

    async function ensureTeachingListsLoaded() {
        if (teachingListsLoaded) return;
        if (!teachingCourseSelect || !teachingTeacherSelect) return;

        setSelectLoading(teachingCourseSelect, '加载课程中...');
        setSelectLoading(teachingTeacherSelect, '加载教师中...');

        showLoading(true);
        try {
            const [coursesRes, teachersRes] = await Promise.all([
                API.adminListCourses(),
                API.adminListTeachers()
            ]);

            if (coursesRes.code !== 200) throw new Error(coursesRes.message || '加载课程失败');
            if (teachersRes.code !== 200) throw new Error(teachersRes.message || '加载教师失败');

            fillSelect(
                teachingCourseSelect,
                coursesRes.data,
                c => c.id,
                c => `${c.courseNo || ''} ${c.courseName || ''}`.trim() || `课程#${c.id}`,
                '请选择课程'
            );

            fillSelect(
                teachingTeacherSelect,
                teachersRes.data,
                t => t.id,
                t => `${t.teacherNo || ''} ${t.name || ''}`.trim() || `教师#${t.id}`,
                '请选择教师'
            );

            teachingListsLoaded = true;
        } catch (err) {
            setSelectLoading(teachingCourseSelect, '加载失败');
            setSelectLoading(teachingTeacherSelect, '加载失败');
            Utils.showToast('加载课程/教师列表失败: ' + (err.message || '网络错误'), 'error');
        } finally {
            showLoading(false);
        }
    }

    navItems.forEach(item => {
        item.addEventListener('click', (e) => {
            e.preventDefault();
            const viewId = item.dataset.view;

            navItems.forEach(n => n.classList.remove('active'));
            item.classList.add('active');

            sections.forEach(section => {
                section.style.display = section.id === `view-${viewId}` ? 'block' : 'none';
            });

            if (viewId === 'create-teaching-class') {
                ensureTeachingListsLoaded();
            }
        });
    });

    function showLoading(show) {
        const loading = document.getElementById('loading');
        if (!loading) return;
        loading.style.display = show ? 'block' : 'none';
    }

    function toJsonPretty(value) {
        try {
            return JSON.stringify(value ?? null, null, 2);
        } catch (e) {
            return String(value);
        }
    }

    function getFormData(form) {
        const fd = new FormData(form);
        const obj = {};
        fd.forEach((v, k) => {
            obj[k] = typeof v === 'string' ? v.trim() : v;
        });
        return obj;
    }

    // --- Create Student ---
    const createStudentForm = document.getElementById('createStudentForm');
    const studentResult = document.getElementById('studentResult');

    createStudentForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const data = getFormData(createStudentForm);

        // Convert optional numbers
        if (data.grade === '') delete data.grade; else data.grade = Number(data.grade);
        if (data.enrollmentYear === '') delete data.enrollmentYear; else data.enrollmentYear = Number(data.enrollmentYear);
        if (data.birthDate === '') delete data.birthDate;
        if (data.email === '') delete data.email;
        if (data.phone === '') delete data.phone;
        if (data.className === '') delete data.className;

        showLoading(true);
        try {
            const res = await API.adminCreateStudent(data);
            if (res.code === 200) {
                Utils.showToast('创建学生成功', 'success');
                studentResult.textContent = toJsonPretty(res.data);
                createStudentForm.reset();
            } else {
                Utils.showToast(res.message || '创建学生失败', 'error');
            }
        } catch (err) {
            Utils.showToast('创建学生失败: ' + (err.message || '网络错误'), 'error');
        } finally {
            showLoading(false);
        }
    });

    // --- Create Teacher ---
    const createTeacherForm = document.getElementById('createTeacherForm');
    const teacherResult = document.getElementById('teacherResult');

    createTeacherForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const data = getFormData(createTeacherForm);

        if (data.title === '') delete data.title;
        if (data.email === '') delete data.email;
        if (data.phone === '') delete data.phone;

        showLoading(true);
        try {
            const res = await API.adminCreateTeacher(data);
            if (res.code === 200) {
                Utils.showToast('创建教师成功', 'success');
                teacherResult.textContent = toJsonPretty(res.data);
                createTeacherForm.reset();
            } else {
                Utils.showToast(res.message || '创建教师失败', 'error');
            }
        } catch (err) {
            Utils.showToast('创建教师失败: ' + (err.message || '网络错误'), 'error');
        } finally {
            showLoading(false);
        }
    });

    // --- Create Course ---
    const createCourseForm = document.getElementById('createCourseForm');
    const courseResult = document.getElementById('courseResult');

    createCourseForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const data = getFormData(createCourseForm);

        data.credit = Number(data.credit);
        data.hours = Number(data.hours);
        if (data.courseType === '') delete data.courseType;
        if (data.description === '') delete data.description;

        showLoading(true);
        try {
            const res = await API.adminCreateCourse(data);
            if (res.code === 200) {
                Utils.showToast('创建课程成功', 'success');
                courseResult.textContent = toJsonPretty(res.data);
                createCourseForm.reset();
            } else {
                Utils.showToast(res.message || '创建课程失败', 'error');
            }
        } catch (err) {
            Utils.showToast('创建课程失败: ' + (err.message || '网络错误'), 'error');
        } finally {
            showLoading(false);
        }
    });

    // --- Create Teaching Class ---
    const createTeachingClassForm = document.getElementById('createTeachingClassForm');
    const teachingClassResult = document.getElementById('teachingClassResult');

    createTeachingClassForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const data = getFormData(createTeachingClassForm);

        data.courseId = Number(data.courseId);
        data.teacherId = Number(data.teacherId);
        data.maxStudents = Number(data.maxStudents);
        if (data.classroom === '') delete data.classroom;
        if (data.schedule === '') delete data.schedule;
        if (data.status === '') delete data.status; else data.status = Number(data.status);

        showLoading(true);
        try {
            const res = await API.adminCreateTeachingClass(data);
            if (res.code === 200) {
                Utils.showToast('创建教学班成功', 'success');
                teachingClassResult.textContent = toJsonPretty(res.data);
                createTeachingClassForm.reset();
            } else {
                Utils.showToast(res.message || '创建教学班失败', 'error');
            }
        } catch (err) {
            Utils.showToast('创建教学班失败: ' + (err.message || '网络错误'), 'error');
        } finally {
            showLoading(false);
        }
    });
});
