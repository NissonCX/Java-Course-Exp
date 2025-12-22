/**
 * Utility functions for the application
 */

const Utils = {
    /**
     * Show a toast notification
     * @param {string} message - The message to display
     * @param {string} type - 'success', 'error', or 'info'
     */
    showToast: (message, type = 'info') => {
        let container = document.querySelector('.toast-container');
        if (!container) {
            container = document.createElement('div');
            container.className = 'toast-container';
            document.body.appendChild(container);
        }

        const toast = document.createElement('div');
        toast.className = `toast ${type}`;

        let icon = 'ℹ️';
        if (type === 'success') icon = '✅';
        if (type === 'error') icon = '❌';

        toast.innerHTML = `
            <span class="toast-icon">${icon}</span>
            <span class="toast-message">${message}</span>
        `;

        container.appendChild(toast);

        // Remove after 3 seconds
        setTimeout(() => {
            toast.style.opacity = '0';
            setTimeout(() => toast.remove(), 300);
        }, 3000);
    },

    /**
     * Format a score based on rules
     * @param {number} score
     * @returns {string} Formatted score or '-'
     */
    formatScore: (score) => {
        return (score === null || score === undefined) ? '-' : score;
    },

    /**
     * Get CSS class for score badge
     * @param {number} score
     */
    getScoreClass: (score) => {
        if (score === null || score === undefined) return '';
        if (score >= 90) return 'score-high';
        if (score >= 80) return 'score-mid';
        if (score >= 60) return 'score-pass';
        return 'score-fail';
    },

    /**
     * Create loading spinner element
     */
    createSpinner: () => {
        const spinner = document.createElement('div');
        spinner.className = 'spinner';
        return spinner;
    },

    /**
     * Parse JWT token to get payload
     */
    parseJwt: (token) => {
        try {
            const base64Url = token.split('.')[1];
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
            const jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function(c) {
                return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
            }).join(''));
            return JSON.parse(jsonPayload);
        } catch (e) {
            return null;
        }
    }
};
