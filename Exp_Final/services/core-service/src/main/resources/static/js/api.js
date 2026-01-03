/**
 * API Client Wrapper for Microservices Architecture
 * Handles JWT authentication and request formatting
 */

const API = {
    BASE_URL: '/api',

    /**
     * Get the stored JWT token
     */
    getToken: () => localStorage.getItem('token'),

    /**
     * Set the JWT token and user info
     */
    setSession: (data) => {
        localStorage.setItem('token', data.token);
        localStorage.setItem('user', JSON.stringify({
            username: data.username,
            realName: data.realName,
            role: data.role,
            userId: data.userId,
            roleId: data.roleId
        }));
    },

    /**
     * Clear session and logout
     */
    logout: () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = '/index.html';
    },

    /**
     * Get current user info
     */
    getUser: () => {
        const userStr = localStorage.getItem('user');
        return userStr ? JSON.parse(userStr) : null;
    },

    /**
     * Generic fetch wrapper
     */
    request: async (endpoint, options = {}) => {
        const token = API.getToken();

        const headers = {
            'Content-Type': 'application/json',
            ...options.headers
        };

        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const config = {
            ...options,
            headers
        };

        try {
            const response = await fetch(`${API.BASE_URL}${endpoint}`, config);

            // Handle 401 Unauthorized
            if (response.status === 401) {
                if (!window.location.pathname.endsWith('index.html') &&
                    !window.location.pathname.endsWith('/')) {
                    API.logout();
                }
                throw new Error('Unauthorized');
            }

            const data = await response.json();
            return data;
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    },

    // ==================== AUTH SERVICE ====================
    login: (username, password) => {
        return API.request('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ username, password })
        });
    },
    registerStudent: (data) => {
        return API.request('/auth/register/student', {
            method: 'POST',
            body: JSON.stringify(data)
        });
    },
    registerTeacher: (data) => {
        return API.request('/auth/register/teacher', {
            method: 'POST',
            body: JSON.stringify(data)
        });
    },

    // ==================== STUDENT SERVICE ====================
    getStudentProfile: () => API.request('/student/profile'),
    updateStudentProfile: (data) => {
        return API.request('/student/profile', {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    },
    getStudentScores: () => API.request('/student/scores'),
    getStudentEnrollments: () => API.request('/student/enrollments'),
    enrollCourse: (teachingClassId) => {
        return API.request('/student/enroll', {
            method: 'POST',
            body: JSON.stringify({ teachingClassId })
        });
    },
    dropCourse: (enrollmentId) => {
        return API.request(`/student/enroll/${enrollmentId}`, {
            method: 'DELETE'
        });
    },

    // ==================== TEACHER SERVICE ====================
    getTeacherProfile: () => API.request('/teacher/profile'),
    updateTeacherProfile: (data) => {
        return API.request('/teacher/profile', {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    },
    getTeacherClasses: () => API.request('/teacher/classes'),
    getClassStudents: (classId) => API.request(`/teacher/class/${classId}/students`),

    // ==================== COURSE SERVICE ====================
    getAvailableClasses: (semester) => {
        const query = semester ? `?semester=${encodeURIComponent(semester)}` : '';
        return API.request(`/course/classes${query}`);
    },

    // ==================== SCORE SERVICE ====================
    getClassStatistics: (classId, teacherId = null) => {
        const user = API.getUser();
        const tid = teacherId || (user ? user.roleId : null);
        const query = tid ? `?teacherId=${tid}` : '';
        return API.request(`/score/class/${classId}/statistics${query}`);
    },
    inputScore: (data, teacherId = null) => {
        const user = API.getUser();
        const tid = teacherId || (user ? user.roleId : null);
        return API.request(`/score/input?teacherId=${tid}`, {
            method: 'POST',
            body: JSON.stringify(data)
        });
    },
    batchInputScores: (teachingClassId, scores, teacherId = null) => {
        const user = API.getUser();
        const tid = teacherId || (user ? user.roleId : null);
        return API.request(`/score/batch?teacherId=${tid}`, {
            method: 'POST',
            body: JSON.stringify({ teachingClassId, scores })
        });
    },

    // ==================== AI SERVICE ====================
    studentAiConsult: (message) => {
        return API.request('/ai/student/consult', {
            method: 'POST',
            body: JSON.stringify({ message })
        });
    },
    teacherAiConsult: (message) => {
        return API.request('/ai/teacher/consult', {
            method: 'POST',
            body: JSON.stringify({ message })
        });
    },

    // ==================== ADMIN SERVICE ====================
    adminCreateStudent: (data) => {
        return API.request('/admin/students', {
            method: 'POST',
            body: JSON.stringify(data)
        });
    },
    adminCreateTeacher: (data) => {
        return API.request('/admin/teachers', {
            method: 'POST',
            body: JSON.stringify(data)
        });
    },
    adminCreateCourse: (data) => {
        return API.request('/admin/courses', {
            method: 'POST',
            body: JSON.stringify(data)
        });
    },
    adminCreateTeachingClass: (data) => {
        return API.request('/admin/teaching-classes', {
            method: 'POST',
            body: JSON.stringify(data)
        });
    },
    adminListCourses: () => API.request('/admin/courses'),
    adminListTeachers: () => API.request('/admin/teachers'),
    adminListStudents: () => API.request('/admin/students'),
    adminListTeachingClasses: () => API.request('/admin/teaching-classes'),

    // ==================== LEGACY COMPATIBILITY ====================
    updateTeachingClassStatus: (classId, status) => {
        return API.request(`/teacher/class/${classId}/status`, {
            method: 'PUT',
            body: JSON.stringify({ status })
        });
    }
};
