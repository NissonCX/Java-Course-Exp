/**
 * API Client Wrapper
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
                // If not already on login page
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

    // --- Auth Methods ---
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

    // --- Student Methods ---
    getStudentProfile: () => API.request('/student/profile'),
    updateStudentProfile: (data) => {
        return API.request('/student/profile', {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    },
    getAvailableClasses: (semester) => {
        const query = semester ? `?semester=${semester}` : '';
        return API.request(`/course/classes${query}`);
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
    studentAiConsult: (message) => {
        return API.request('/student/ai/consult', {
            method: 'POST',
            body: JSON.stringify({ message })
        });
    },

    // --- Teacher Methods ---
    getTeacherProfile: () => API.request('/teacher/profile'),
    updateTeacherProfile: (data) => {
        return API.request('/teacher/profile', {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    },
    getTeacherClasses: () => API.request('/teacher/classes'),
    getClassStudents: (classId) => API.request(`/teacher/class/${classId}/students`),
    getClassScores: (classId) => API.request(`/teacher/class/${classId}/scores`),
    getClassStatistics: (classId) => API.request(`/teacher/class/${classId}/statistics`),
    inputScore: (data) => {
        return API.request('/teacher/score/input', {
            method: 'POST',
            body: JSON.stringify(data)
        });
    },
    batchInputScores: (teachingClassId, scores) => {
        return API.request('/teacher/score/batch', {
            method: 'POST',
            body: JSON.stringify({ teachingClassId, scores })
        });
    },
    teacherAiConsult: (teachingClassId, message) => {
        return API.request('/teacher/ai/consult', {
            method: 'POST',
            body: JSON.stringify({ teachingClassId, message })
        });
    },

    updateTeachingClassStatus: (classId, status) => {
        return API.request(`/teacher/class/${classId}/status`, {
            method: 'PUT',
            body: JSON.stringify({ status })
        });
    }
};
