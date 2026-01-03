/**
 * Utility functions for the application
 */

const Utils = {
    // --- Theme (light/dark) ---
    themeKey: 'theme',

    getSystemTheme: () => {
        try {
            return window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches
                ? 'dark'
                : 'light';
        } catch (e) {
            return 'light';
        }
    },

    getStoredTheme: () => {
        const t = localStorage.getItem(Utils.themeKey);
        return (t === 'light' || t === 'dark') ? t : null;
    },

    getEffectiveTheme: () => {
        return Utils.getStoredTheme() || Utils.getSystemTheme();
    },

    applyTheme: (theme) => {
        if (theme === 'dark') {
            document.documentElement.setAttribute('data-theme', 'dark');
        } else {
            document.documentElement.removeAttribute('data-theme');
        }
    },

    updateThemeToggleButtons: () => {
        const effective = Utils.getEffectiveTheme();
        const isDark = effective === 'dark';

        const candidates = Array.from(document.querySelectorAll('[data-theme-toggle], #themeToggleBtn'));
        const buttons = [...new Set(candidates)];

        buttons.forEach(btn => {
            if (!(btn instanceof HTMLElement)) return;
            btn.textContent = isDark ? '亮色' : '暗色';
            btn.setAttribute('aria-label', isDark ? '切换到亮色主题' : '切换到暗色主题');
            btn.title = isDark ? '切换到亮色主题' : '切换到暗色主题';
        });
    },

    initTheme: () => {
        // Apply stored or system theme ASAP
        Utils.applyTheme(Utils.getEffectiveTheme());

        // If user hasn't chosen, follow system changes
        const stored = Utils.getStoredTheme();
        if (!stored && window.matchMedia) {
            const media = window.matchMedia('(prefers-color-scheme: dark)');
            // Avoid duplicate listeners
            if (!Utils._themeMedia) {
                Utils._themeMedia = media;
                const handler = () => {
                    if (!Utils.getStoredTheme()) {
                        Utils.applyTheme(Utils.getEffectiveTheme());
                        Utils.updateThemeToggleButtons();
                    }
                };
                if (typeof media.addEventListener === 'function') {
                    media.addEventListener('change', handler);
                } else if (typeof media.addListener === 'function') {
                    media.addListener(handler);
                }
            }
        }

        Utils.updateThemeToggleButtons();
    },

    toggleTheme: () => {
        const current = Utils.getEffectiveTheme();
        const next = current === 'dark' ? 'light' : 'dark';
        localStorage.setItem(Utils.themeKey, next);
        Utils.applyTheme(next);
        Utils.updateThemeToggleButtons();
    },

    bindThemeToggle: (buttonEl) => {
        if (!buttonEl) return;
        buttonEl.addEventListener('click', () => Utils.toggleTheme());
        Utils.updateThemeToggleButtons();
    },

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

// Auto-init theme and bind default toggle button if present
(() => {
    // Apply theme early to reduce flash
    try {
        Utils.initTheme();
    } catch (e) {
        // ignore
    }

    document.addEventListener('DOMContentLoaded', () => {
        try {
            Utils.initTheme();
            const btn = document.getElementById('themeToggleBtn');
            if (btn) Utils.bindThemeToggle(btn);
        } catch (e) {
            // ignore
        }
    });
})();
