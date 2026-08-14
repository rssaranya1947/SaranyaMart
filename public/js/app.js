/**
 * SaranyaMart Client-Side Web Engine
 * Handles Authentication (Login, Register), Session Management, and Role Dashboards.
 */
class SaranyaMartApp {
    constructor() {
        this.currentUser = null;
        this.token = null;
        this.init();
    }

    init() {
        // Load session from localStorage
        const savedUser = localStorage.getItem('sm_user');
        const savedToken = localStorage.getItem('sm_token');

        if (savedUser && savedToken) {
            try {
                this.currentUser = JSON.parse(savedUser);
                this.token = savedToken;
                this.updateHeaderUI();
                this.renderRoleDashboard();
            } catch (e) {
                this.logout();
            }
        }
    }

    // Modal Control
    openModal(mode = 'login', role = 'buyer') {
        const modal = document.getElementById('auth-modal');
        modal.classList.remove('hidden');
        this.switchAuthTab(mode);

        if (mode === 'register' && role) {
            const roleInput = document.querySelector(`input[name="register-role"][value="${role}"]`);
            if (roleInput) {
                roleInput.checked = true;
                this.updateRoleUI();
            }
        }
    }

    closeModal() {
        const modal = document.getElementById('auth-modal');
        modal.classList.add('hidden');
    }

    switchAuthTab(tab) {
        const loginTab = document.getElementById('tab-login');
        const registerTab = document.getElementById('tab-register');
        const loginForm = document.getElementById('form-login');
        const registerForm = document.getElementById('form-register');

        if (tab === 'login') {
            loginTab.classList.add('active');
            registerTab.classList.remove('active');
            loginForm.classList.remove('hidden');
            registerForm.classList.add('hidden');
        } else {
            registerTab.classList.add('active');
            loginTab.classList.remove('active');
            registerForm.classList.remove('hidden');
            loginForm.classList.add('hidden');
        }
    }

    updateRoleUI() {
        // Triggers CSS styling update on radio selection
    }

    // Quick Demo Credentials Auto-Fill & Login
    async quickLogin(email, password) {
        document.getElementById('login-email').value = email;
        document.getElementById('login-password').value = password;
        this.openModal('login');
        
        // Auto-submit for convenience
        setTimeout(() => {
            const loginForm = document.getElementById('form-login');
            loginForm.dispatchEvent(new Event('submit', { cancelable: true, bubbles: true }));
        }, 300);
    }

    // Submit Registration (POST /api/register)
    async submitRegister(event) {
        event.preventDefault();

        const fullName = document.getElementById('register-name').value.trim();
        const email = document.getElementById('register-email').value.trim();
        const password = document.getElementById('register-password').value;
        const role = document.querySelector('input[name="register-role"]:checked').value;

        const btn = document.getElementById('btn-register-submit');
        btn.disabled = true;
        btn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Registering...';

        try {
            const response = await fetch('/api/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ fullName, email, password, role })
            });

            const data = await response.json();

            if (response.ok && data.success) {
                this.showToast(data.message, 'success');
                this.currentUser = data.user;
                this.token = data.token;
                localStorage.setItem('sm_user', JSON.stringify(data.user));
                localStorage.setItem('sm_token', data.token);

                this.closeModal();
                this.updateHeaderUI();
                this.renderRoleDashboard();
            } else {
                this.showToast(data.message || 'Registration failed.', 'error');
            }
        } catch (error) {
            console.error('Registration API Error:', error);
            this.showToast('Network or server error during registration.', 'error');
        } finally {
            btn.disabled = false;
            btn.innerHTML = '<span>Create SaranyaMart Account</span> <i class="fa-solid fa-circle-check"></i>';
        }
    }

    // Submit Login (POST /api/login)
    async submitLogin(event) {
        event.preventDefault();

        const email = document.getElementById('login-email').value.trim();
        const password = document.getElementById('login-password').value;

        const btn = document.getElementById('btn-login-submit');
        btn.disabled = true;
        btn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Logging in...';

        try {
            const response = await fetch('/api/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password })
            });

            const data = await response.json();

            if (response.ok && data.success) {
                this.showToast(data.message, 'success');
                this.currentUser = data.user;
                this.token = data.token;
                localStorage.setItem('sm_user', JSON.stringify(data.user));
                localStorage.setItem('sm_token', data.token);

                this.closeModal();
                this.updateHeaderUI();
                this.renderRoleDashboard();
            } else {
                this.showToast(data.message || 'Invalid email or password.', 'error');
            }
        } catch (error) {
            console.error('Login API Error:', error);
            this.showToast('Network or server error during login.', 'error');
        } finally {
            btn.disabled = false;
            btn.innerHTML = '<span>Login to Account</span> <i class="fa-solid fa-arrow-right"></i>';
        }
    }

    // Logout Function
    logout() {
        this.currentUser = null;
        this.token = null;
        localStorage.removeItem('sm_user');
        localStorage.removeItem('sm_token');

        this.updateHeaderUI();
        this.showHome();
        this.showToast('Logged out successfully.', 'info');
    }

    // Header UI State Handler
    updateHeaderUI() {
        const guestActions = document.getElementById('nav-actions');
        const userHeader = document.getElementById('user-profile-header');

        if (this.currentUser) {
            guestActions.classList.add('hidden');
            userHeader.classList.remove('hidden');

            document.getElementById('header-user-name').textContent = this.currentUser.fullName;
            document.getElementById('header-user-role').textContent = this.currentUser.role.toUpperCase();
            document.getElementById('user-avatar-initial').textContent = this.currentUser.fullName.charAt(0).toUpperCase();

            // Set role badge color
            const roleBadge = document.getElementById('header-user-role');
            if (this.currentUser.role === 'admin') roleBadge.style.color = '#a855f7';
            else if (this.currentUser.role === 'seller') roleBadge.style.color = '#10b981';
            else roleBadge.style.color = '#3b82f6';

        } else {
            guestActions.classList.remove('hidden');
            userHeader.classList.add('hidden');
        }
    }

    // View Routing: Render Dashboard based on role
    renderRoleDashboard() {
        if (!this.currentUser) {
            this.showHome();
            return;
        }

        document.getElementById('hero-view').classList.add('hidden');
        document.getElementById('buyer-dashboard').classList.add('hidden');
        document.getElementById('seller-dashboard').classList.add('hidden');
        document.getElementById('admin-dashboard').classList.add('hidden');

        const role = this.currentUser.role.toLowerCase();

        if (role === 'admin') {
            document.getElementById('admin-dashboard').classList.remove('hidden');
            this.loadUsersTable();
        } else if (role === 'seller') {
            document.getElementById('seller-dashboard').classList.remove('hidden');
        } else {
            document.getElementById('buyer-dashboard').classList.remove('hidden');
        }
    }

    showHome() {
        document.getElementById('hero-view').classList.remove('hidden');
        document.getElementById('buyer-dashboard').classList.add('hidden');
        document.getElementById('seller-dashboard').classList.add('hidden');
        document.getElementById('admin-dashboard').classList.add('hidden');
    }

    // Fetch and populate Admin User Listing Table (GET /api/users)
    async loadUsersTable() {
        try {
            const response = await fetch('/api/users');
            const data = await response.json();

            if (data.success && data.users) {
                const tbody = document.getElementById('users-table-body');
                tbody.innerHTML = '';

                let sellerCount = 0;
                let buyerCount = 0;

                data.users.forEach(user => {
                    if (user.role === 'seller') sellerCount++;
                    if (user.role === 'buyer') buyerCount++;

                    const tr = document.createElement('tr');
                    const roleColorClass = user.role === 'admin' ? 'admin' : (user.role === 'seller' ? 'seller' : 'buyer');
                    
                    tr.innerHTML = `
                        <td>#${user.id}</td>
                        <td><strong>${user.fullName}</strong></td>
                        <td>${user.email}</td>
                        <td><span class="chip-role ${roleColorClass}">${user.role}</span></td>
                        <td>${user.createdAt || 'Just now'}</td>
                    `;
                    tbody.appendChild(tr);
                });

                document.getElementById('admin-total-users').textContent = data.users.length;
                document.getElementById('admin-total-sellers').textContent = sellerCount;
                document.getElementById('admin-total-buyers').textContent = buyerCount;
            }
        } catch (e) {
            console.error('Failed to load admin user table:', e);
        }
    }

    // Toast Notification Banner
    showToast(message, type = 'info') {
        const container = document.getElementById('toast-container');
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;

        const iconClass = type === 'success' ? 'fa-circle-check' : (type === 'error' ? 'fa-circle-xmark' : 'fa-circle-info');

        toast.innerHTML = `
            <i class="fa-solid ${iconClass}"></i>
            <span>${message}</span>
        `;

        container.appendChild(toast);

        setTimeout(() => {
            toast.style.opacity = '0';
            setTimeout(() => toast.remove(), 300);
        }, 4000);
    }
}

// Global App Initialization
document.addEventListener('DOMContentLoaded', () => {
    window.app = new SaranyaMartApp();
});
