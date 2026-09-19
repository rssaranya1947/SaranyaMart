/**
 * SaranyaMart Client-Side Web Engine - Weeks 1 to 7 Complete
 */
class SaranyaMartApp {
    constructor() {
        this.currentUser = null;
        this.token = null;
        this.cart = [];
        this.wishlist = [];
        this.currentCategory = 'all';
        this.searchQuery = '';
        this.minPrice = null;
        this.maxPrice = null;
        this.sortBy = 'newest';
        this.allProducts = [];
        this.activeReviewProduct = null;
        
        // Multi-Currency & Coupon State
        this.currency = 'INR';
        this.rates = { INR: 1.0, USD: 0.012, EUR: 0.011 };
        this.symbols = { INR: '₹', USD: '$', EUR: '€' };
        this.appliedCoupon = null;

        // Theme State (Week 7)
        this.theme = 'dark';

        this.init();
    }

    init() {
        // Load session, cart, wishlist, currency, and theme from localStorage
        const savedUser = localStorage.getItem('sm_user');
        const savedToken = localStorage.getItem('sm_token');
        const savedCart = localStorage.getItem('sm_cart');
        const savedWishlist = localStorage.getItem('sm_wishlist');
        const savedCurrency = localStorage.getItem('sm_currency');
        const savedTheme = localStorage.getItem('sm_theme');

        if (savedUser && savedToken) {
            try {
                this.currentUser = JSON.parse(savedUser);
                this.token = savedToken;
                this.updateHeaderUI();
            } catch (e) {
                this.logout();
            }
        }

        if (savedCart) {
            try { this.cart = JSON.parse(savedCart); } catch (e) { this.cart = []; }
        }

        if (savedWishlist) {
            try { this.wishlist = JSON.parse(savedWishlist); } catch (e) { this.wishlist = []; }
        }

        if (savedCurrency && this.rates[savedCurrency]) {
            this.currency = savedCurrency;
            const sel1 = document.getElementById('currency-select');
            const sel2 = document.getElementById('currency-select-user');
            if (sel1) sel1.value = savedCurrency;
            if (sel2) sel2.value = savedCurrency;
        }

        if (savedTheme === 'light') {
            this.setTheme('light');
        }

        this.updateCartBadge();
        this.updateWishlistBadge();
        this.loadCatalog();
        if (this.currentUser) {
            this.renderRoleDashboard();
        }
    }

    // WEEK 7: DARK / LIGHT THEME TOGGLE
    toggleTheme() {
        const newTheme = this.theme === 'dark' ? 'light' : 'dark';
        this.setTheme(newTheme);
    }

    setTheme(t) {
        this.theme = t;
        localStorage.setItem('sm_theme', t);
        const btn1 = document.getElementById('btn-theme-toggle');
        const btn2 = document.getElementById('btn-theme-toggle-user');
        const iconHtml = t === 'light' ? '<i class="fa-solid fa-sun text-amber"></i>' : '<i class="fa-solid fa-moon"></i>';

        if (btn1) btn1.innerHTML = iconHtml;
        if (btn2) btn2.innerHTML = iconHtml;

        if (t === 'light') {
            document.body.classList.add('light-theme');
        } else {
            document.body.classList.remove('light-theme');
        }
    }

    // MULTI-CURRENCY CONVERSION HELPER
    formatPrice(amountInINR) {
        if (amountInINR === null || amountInINR === undefined) return '₹0';
        const rate = this.rates[this.currency] || 1.0;
        const sym = this.symbols[this.currency] || '₹';
        const converted = amountInINR * rate;
        if (this.currency === 'INR') {
            return `${sym}${Math.round(converted).toLocaleString('en-IN')}`;
        }
        return `${sym}${converted.toFixed(2)}`;
    }

    changeCurrency(newCurr) {
        if (this.rates[newCurr]) {
            this.currency = newCurr;
            localStorage.setItem('sm_currency', newCurr);
            
            const sel1 = document.getElementById('currency-select');
            const sel2 = document.getElementById('currency-select-user');
            if (sel1) sel1.value = newCurr;
            if (sel2) sel2.value = newCurr;

            this.renderProductGrid(this.allProducts);
            this.updateCartBadge();
            if (this.currentUser) {
                this.renderRoleDashboard();
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
            if (roleInput) roleInput.checked = true;
        }
    }

    closeModal() {
        document.getElementById('auth-modal').classList.add('hidden');
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

    // Quick Demo Logins
    async quickLogin(email, password) {
        document.getElementById('login-email').value = email;
        document.getElementById('login-password').value = password;
        this.openModal('login');
        setTimeout(() => {
            document.getElementById('form-login').dispatchEvent(new Event('submit', { cancelable: true, bubbles: true }));
        }, 300);
    }

    // Authentication API (POST /api/register & POST /api/login)
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
            this.showToast('Server error during registration.', 'error');
        } finally {
            btn.disabled = false;
            btn.innerHTML = '<span>Create SaranyaMart Account</span> <i class="fa-solid fa-circle-check"></i>';
        }
    }

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
            this.showToast('Server error during login.', 'error');
        } finally {
            btn.disabled = false;
            btn.innerHTML = '<span>Login to Account</span> <i class="fa-solid fa-arrow-right"></i>';
        }
    }

    logout() {
        this.currentUser = null;
        this.token = null;
        localStorage.removeItem('sm_user');
        localStorage.removeItem('sm_token');

        this.updateHeaderUI();
        this.showHome();
        this.showToast('Logged out successfully.', 'info');
    }

    updateHeaderUI() {
        const guestActions = document.getElementById('nav-actions');
        const userHeader = document.getElementById('user-profile-header');

        if (this.currentUser) {
            guestActions.classList.add('hidden');
            userHeader.classList.remove('hidden');

            document.getElementById('header-user-name').textContent = this.currentUser.fullName;
            document.getElementById('header-user-role').textContent = (this.currentUser.role || 'buyer').toUpperCase();
            document.getElementById('user-avatar-initial').textContent = this.currentUser.fullName.charAt(0).toUpperCase();

            const roleBadge = document.getElementById('header-user-role');
            const role = (this.currentUser.role || 'buyer').toLowerCase();
            if (role === 'admin') roleBadge.style.color = '#a855f7';
            else if (role === 'seller') roleBadge.style.color = '#10b981';
            else roleBadge.style.color = '#3b82f6';

        } else {
            guestActions.classList.remove('hidden');
            userHeader.classList.add('hidden');
        }
    }

    // PRODUCT CATALOG & FILTERS
    async loadCatalog() {
        const grid = document.getElementById('product-catalog-grid');
        grid.innerHTML = '<div class="text-center" style="grid-column: 1/-1; padding: 2rem;"><i class="fa-solid fa-spinner fa-spin fa-2x"></i><p>Loading Products...</p></div>';

        try {
            let url = `/api/products?category=${encodeURIComponent(this.currentCategory)}`;
            if (this.searchQuery) url += `&search=${encodeURIComponent(this.searchQuery)}`;
            if (this.minPrice !== null && this.minPrice !== '') url += `&minPrice=${this.minPrice}`;
            if (this.maxPrice !== null && this.maxPrice !== '') url += `&maxPrice=${this.maxPrice}`;
            if (this.sortBy) url += `&sortBy=${encodeURIComponent(this.sortBy)}`;

            const response = await fetch(url);
            const data = await response.json();

            if (data.success && data.products) {
                this.allProducts = data.products;
                document.getElementById('catalog-count-label').textContent = `Showing ${data.products.length} Products`;
                this.renderProductGrid(data.products);
            }
        } catch (error) {
            grid.innerHTML = '<div style="grid-column: 1/-1; text-align: center; color: #ef4444;"><p>Failed to load catalog.</p></div>';
        }
    }

    selectCategory(cat, element) {
        this.currentCategory = cat;
        document.querySelectorAll('.cat-tab').forEach(t => t.classList.remove('active'));
        if (element) element.classList.add('active');
        this.loadCatalog();
    }

    handleSearch(event) {
        this.searchQuery = document.getElementById('global-search').value.trim();
        if (event.key === 'Enter') {
            this.loadCatalog();
        }
    }

    applyFilters() {
        const minVal = document.getElementById('filter-min-price').value;
        const maxVal = document.getElementById('filter-max-price').value;
        const sortVal = document.getElementById('sort-by-select').value;

        this.minPrice = minVal !== '' ? parseFloat(minVal) : null;
        this.maxPrice = maxVal !== '' ? parseFloat(maxVal) : null;
        this.sortBy = sortVal || 'newest';

        this.loadCatalog();
    }

    clearFilters() {
        document.getElementById('filter-min-price').value = '';
        document.getElementById('filter-max-price').value = '';
        document.getElementById('sort-by-select').value = 'newest';

        this.minPrice = null;
        this.maxPrice = null;
        this.sortBy = 'newest';
        this.loadCatalog();
    }

    renderProductGrid(products) {
        const grid = document.getElementById('product-catalog-grid');
        grid.innerHTML = '';

        if (products.length === 0) {
            grid.innerHTML = `
                <div style="grid-column: 1/-1; text-align: center; padding: 3rem;" class="empty-state-card">
                    <i class="fa-solid fa-box-open fa-3x" style="color: var(--text-muted); margin-bottom: 1rem;"></i>
                    <h3>No products found</h3>
                    <p style="color: var(--text-muted);">Try adjusting your category filter, price range, or search query.</p>
                </div>`;
            return;
        }

        products.forEach(p => {
            const isWishlisted = this.wishlist.includes(p.id);
            const avgRating = p.averageRating ? p.averageRating.toFixed(1) : '5.0';
            const reviewCount = p.reviewCount || 0;
            const priceFormatted = this.formatPrice(p.price);

            const card = document.createElement('div');
            card.className = 'product-card';
            card.innerHTML = `
                <button class="wishlist-toggle-btn ${isWishlisted ? 'active' : ''}" onclick="app.toggleWishlist(${p.id})" title="${isWishlisted ? 'Remove from Wishlist' : 'Add to Wishlist'}">
                    <i class="fa-solid fa-heart"></i>
                </button>
                <div class="product-img-box" onclick="app.openReviewModal(${p.id})">
                    <img src="${p.imageUrl}" alt="${p.title}" onerror="this.src='https://images.unsplash.com/photo-1526170375885-4d8ecf77b99f?w=500'">
                    <span class="category-chip">${p.category}</span>
                </div>
                <div class="product-body">
                    <h3 class="product-title" onclick="app.openReviewModal(${p.id})">${p.title}</h3>
                    <div class="rating-badge" onclick="app.openReviewModal(${p.id})">
                        <i class="fa-solid fa-star text-amber"></i> ${avgRating} <span class="review-cnt">(${reviewCount} reviews)</span>
                    </div>
                    <p class="product-desc">${p.description || 'Quality product from verified seller.'}</p>
                    <div class="product-meta">
                        <span class="product-price">${priceFormatted}</span>
                        <span class="seller-chip" onclick="app.openSellerStoreModal(${p.sellerId}, '${p.sellerName}')"><i class="fa-solid fa-store"></i> ${p.sellerName}</span>
                    </div>
                    <button class="btn btn-primary btn-block" onclick="app.addToCart(${p.id})">
                        <i class="fa-solid fa-cart-plus"></i> Add to Cart
                    </button>
                </div>
            `;
            grid.appendChild(card);
        });
    }

    // WEEK 7: SELLER STOREFRONT MODAL
    async openSellerStoreModal(sellerId, sellerName) {
        const modal = document.getElementById('seller-store-modal');
        const body = document.getElementById('seller-store-body');
        body.innerHTML = '<p class="text-center"><i class="fa-solid fa-spinner fa-spin"></i> Loading Seller Storefront...</p>';
        modal.classList.remove('hidden');

        try {
            const res = await fetch(`/api/products/seller/${sellerId}`);
            const data = await res.json();

            if (data.success && data.products) {
                let itemsHtml = '';
                data.products.forEach(p => {
                    itemsHtml += `
                        <div class="cart-item-row" style="margin-bottom:0.75rem;">
                            <img src="${p.imageUrl}" style="width: 60px; height: 60px; object-fit: cover; border-radius: 8px;">
                            <div class="cart-item-info">
                                <div class="cart-item-title">${p.title}</div>
                                <div class="cart-item-price">${this.formatPrice(p.price)}</div>
                                <div style="font-size:0.75rem; color:var(--text-muted);"><i class="fa-solid fa-star text-amber"></i> ${p.averageRating ? p.averageRating.toFixed(1) : '5.0'} (${p.reviewCount || 0} reviews)</div>
                            </div>
                            <button class="btn btn-primary btn-sm" onclick="app.addToCart(${p.id}); app.closeSellerStoreModal();"><i class="fa-solid fa-cart-plus"></i> Add to Cart</button>
                        </div>
                    `;
                });

                body.innerHTML = `
                    <div style="display:flex; align-items:center; gap:1rem; margin-bottom:1.25rem;">
                        <div class="user-avatar" style="width:54px; height:54px; font-size:1.5rem;">${sellerName.charAt(0).toUpperCase()}</div>
                        <div>
                            <h2>${sellerName} Storefront</h2>
                            <p style="color:var(--text-muted); font-size:0.88rem;"><i class="fa-solid fa-certificate text-emerald"></i> Verified Seller | ${data.products.length} Products Listed</p>
                        </div>
                    </div>
                    <hr class="modal-divider">
                    <h4 style="margin-bottom:1rem;"><i class="fa-solid fa-boxes-stacked"></i> Products by ${sellerName}</h4>
                    <div>${itemsHtml || '<p>No products listed by this seller yet.</p>'}</div>
                `;
            }
        } catch (e) {
            body.innerHTML = '<p style="color:#ef4444;">Failed to load seller storefront.</p>';
        }
    }

    closeSellerStoreModal() {
        document.getElementById('seller-store-modal').classList.add('hidden');
    }

    // WEEK 7: BUYER-SELLER MESSAGING & INQUIRIES
    openSendMessageFromDetail() {
        if (!this.activeReviewProduct) return;
        this.closeReviewModal();
        this.openSendMessageModal(this.activeReviewProduct);
    }

    openSendMessageModal(product) {
        if (!this.currentUser) {
            this.openModal('login');
            this.showToast('Please login as Buyer to ask seller a question.', 'info');
            return;
        }

        document.getElementById('msg-product-id').value = product.id;
        document.getElementById('msg-seller-id').value = product.sellerId;
        document.getElementById('msg-seller-name').value = product.sellerName;
        document.getElementById('send-message-desc').textContent = `Inquire directly about "${product.title}" to ${product.sellerName}.`;
        document.getElementById('msg-text').value = '';

        document.getElementById('send-message-modal').classList.remove('hidden');
    }

    closeSendMessageModal() {
        document.getElementById('send-message-modal').classList.add('hidden');
    }

    async submitMessageInquiry(event) {
        event.preventDefault();
        const productId = parseInt(document.getElementById('msg-product-id').value);
        const recipientId = parseInt(document.getElementById('msg-seller-id').value);
        const recipientName = document.getElementById('msg-seller-name').value;
        const messageText = document.getElementById('msg-text').value.trim();

        const product = this.allProducts.find(p => p.id === productId);

        const payload = {
            senderId: this.currentUser.id,
            senderName: this.currentUser.fullName,
            recipientId: recipientId,
            recipientName: recipientName,
            productId: productId,
            productTitle: product ? product.title : 'Product Inquiry',
            messageText: messageText
        };

        try {
            const response = await fetch('/api/messages', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            const data = await response.json();

            if (response.ok && data.id) {
                this.showToast('Inquiry sent to seller successfully!', 'success');
                this.closeSendMessageModal();
            } else {
                this.showToast(data.message || 'Error sending message.', 'error');
            }
        } catch (e) {
            this.showToast('Server error sending message.', 'error');
        }
    }

    async openInboxModal() {
        if (!this.currentUser) return;
        const list = document.getElementById('inbox-messages-list');
        list.innerHTML = '<p class="text-center"><i class="fa-solid fa-spinner fa-spin"></i> Loading Inbox Messages...</p>';
        document.getElementById('messages-modal').classList.remove('hidden');

        try {
            const response = await fetch(`/api/messages/user/${this.currentUser.id}`);
            const messages = await response.json();

            list.innerHTML = '';
            if (!messages || messages.length === 0) {
                list.innerHTML = '<p style="text-align:center; color:var(--text-muted); padding:2rem;">Your inbox is empty. No messages yet.</p>';
                return;
            }

            messages.forEach(m => {
                const card = document.createElement('div');
                card.className = 'review-card';
                card.innerHTML = `
                    <div class="review-header">
                        <span class="review-author"><i class="fa-solid fa-circle-user"></i> ${m.senderName} ➔ ${m.recipientName}</span>
                        <span class="review-date">${m.createdAt || 'Recent'}</span>
                    </div>
                    <div style="font-size:0.8rem; color:#818cf8; font-weight:700; margin-bottom:0.35rem;">Re: ${m.productTitle}</div>
                    <div class="review-text">${m.messageText}</div>
                `;
                list.appendChild(card);
            });
        } catch (e) {
            list.innerHTML = '<p style="color:#ef4444;">Failed to load messages inbox.</p>';
        }
    }

    closeInboxModal() {
        document.getElementById('messages-modal').classList.add('hidden');
    }

    // WISHLIST ENGINE
    toggleWishlist(productId) {
        const index = this.wishlist.indexOf(productId);
        if (index > -1) {
            this.wishlist.splice(index, 1);
            this.showToast('Removed item from Wishlist.', 'info');
        } else {
            this.wishlist.push(productId);
            this.showToast('Added item to Wishlist ♥', 'success');
        }

        localStorage.setItem('sm_wishlist', JSON.stringify(this.wishlist));
        this.updateWishlistBadge();
        this.renderProductGrid(this.allProducts);
        if (!document.getElementById('wishlist-modal').classList.contains('hidden')) {
            this.renderWishlistModal();
        }
    }

    updateWishlistBadge() {
        const count = this.wishlist.length;
        document.getElementById('wishlist-badge-count').textContent = count;
        const uBadge = document.getElementById('wishlist-badge-count-user');
        if (uBadge) uBadge.textContent = count;
    }

    openWishlistModal() {
        this.renderWishlistModal();
        document.getElementById('wishlist-modal').classList.remove('hidden');
    }

    closeWishlistModal() {
        document.getElementById('wishlist-modal').classList.add('hidden');
    }

    renderWishlistModal() {
        const container = document.getElementById('wishlist-items-container');
        container.innerHTML = '';

        if (this.wishlist.length === 0) {
            container.innerHTML = '<p style="text-align: center; color: var(--text-muted); padding: 2rem;">Your wishlist is currently empty.</p>';
            return;
        }

        this.wishlist.forEach(id => {
            const p = this.allProducts.find(item => item.id === id);
            if (!p) return;

            const row = document.createElement('div');
            row.className = 'cart-item-row';
            row.innerHTML = `
                <img src="${p.imageUrl}" style="width: 50px; height: 50px; object-fit: cover; border-radius: 8px;">
                <div class="cart-item-info">
                    <div class="cart-item-title">${p.title}</div>
                    <div class="cart-item-price">${this.formatPrice(p.price)}</div>
                    <div style="font-size: 0.75rem; color: var(--text-muted);"><i class="fa-solid fa-star text-amber"></i> ${p.averageRating ? p.averageRating.toFixed(1) : '5.0'}</div>
                </div>
                <div>
                    <button class="btn btn-primary btn-sm" onclick="app.addToCart(${p.id})"><i class="fa-solid fa-cart-plus"></i> Add to Cart</button>
                    <button class="btn btn-danger btn-sm" onclick="app.toggleWishlist(${p.id})"><i class="fa-solid fa-trash"></i></button>
                </div>
            `;
            container.appendChild(row);
        });
    }

    // PRODUCT REVIEWS & STAR RATINGS
    async openReviewModal(productId) {
        const product = this.allProducts.find(p => p.id === productId);
        if (!product) return;

        this.activeReviewProduct = product;
        document.getElementById('review-product-id').value = product.id;

        const hero = document.getElementById('review-product-hero');
        hero.innerHTML = `
            <img src="${product.imageUrl}" class="detail-img">
            <div>
                <h3>${product.title}</h3>
                <div style="color: var(--secondary); font-weight: 800; font-size: 1.25rem;">${this.formatPrice(product.price)}</div>
                <div style="font-size: 0.85rem; color: var(--text-muted); margin-top: 0.25rem;">Seller: ${product.sellerName} | Category: ${product.category}</div>
                <div style="margin-top: 0.5rem;" class="rating-badge"><i class="fa-solid fa-star text-amber"></i> Average Rating: <strong>${product.averageRating ? product.averageRating.toFixed(1) : '5.0'} / 5</strong></div>
            </div>
        `;

        this.setStarRating(5);
        document.getElementById('review-comment').value = '';

        try {
            const res = await fetch(`/api/reviews/product/${productId}`);
            const reviews = await res.json();
            this.renderReviewsList(reviews);
        } catch (e) {
            this.renderReviewsList([]);
        }

        document.getElementById('review-modal').classList.remove('hidden');
    }

    closeReviewModal() {
        document.getElementById('review-modal').classList.add('hidden');
    }

    setStarRating(val) {
        document.getElementById('review-rating-value').value = val;
        const stars = document.querySelectorAll('#star-rating-picker .star-btn');
        stars.forEach(s => {
            const v = parseInt(s.getAttribute('data-val'));
            if (v <= val) s.classList.add('active');
            else s.classList.remove('active');
        });
    }

    async submitReview(event) {
        event.preventDefault();
        if (!this.currentUser) {
            this.closeReviewModal();
            this.openModal('login');
            this.showToast('Please login as Buyer to write a product review.', 'info');
            return;
        }

        const productId = parseInt(document.getElementById('review-product-id').value);
        const rating = parseInt(document.getElementById('review-rating-value').value);
        const comment = document.getElementById('review-comment').value.trim();

        const payload = {
            productId,
            buyerId: this.currentUser.id,
            buyerName: this.currentUser.fullName,
            rating,
            comment
        };

        try {
            const response = await fetch('/api/reviews', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            const saved = await response.json();

            if (response.ok && saved.id) {
                this.showToast('Review submitted successfully! Thank you.', 'success');
                document.getElementById('review-comment').value = '';
                this.loadCatalog();
                this.openReviewModal(productId);
            } else {
                this.showToast(saved.message || 'Error submitting review.', 'error');
            }
        } catch (e) {
            this.showToast('Server error submitting review.', 'error');
        }
    }

    renderReviewsList(reviews) {
        const container = document.getElementById('reviews-list-container');
        document.getElementById('review-modal-count').textContent = reviews ? reviews.length : 0;
        container.innerHTML = '';

        if (!reviews || reviews.length === 0) {
            container.innerHTML = '<p style="text-align: center; color: var(--text-muted); padding: 1rem;">No reviews yet. Be the first to review this product!</p>';
            return;
        }

        reviews.forEach(r => {
            const card = document.createElement('div');
            card.className = 'review-card';
            let starsHtml = '';
            for (let i = 1; i <= 5; i++) {
                starsHtml += `<i class="fa-solid fa-star ${i <= r.rating ? 'text-amber' : ''}" style="${i > r.rating ? 'color:rgba(255,255,255,0.2)' : ''}"></i>`;
            }

            card.innerHTML = `
                <div class="review-header">
                    <span class="review-author"><i class="fa-solid fa-circle-user"></i> ${r.buyerName}</span>
                    <span class="review-stars">${starsHtml}</span>
                </div>
                <div class="review-text">${r.comment}</div>
                <div class="review-date">${r.createdAt || 'Recent'}</div>
            `;
            container.appendChild(card);
        });
    }

    // SHOPPING CART & PROMO COUPONS
    addToCart(productId) {
        const product = this.allProducts.find(p => p.id === productId);
        if (!product) return;

        const existing = this.cart.find(item => item.productId === productId);
        if (existing) {
            existing.quantity += 1;
        } else {
            this.cart.push({
                productId: product.id,
                title: product.title,
                price: product.price,
                imageUrl: product.imageUrl,
                quantity: 1,
                sellerId: product.sellerId,
                sellerName: product.sellerName
            });
        }

        localStorage.setItem('sm_cart', JSON.stringify(this.cart));
        this.updateCartBadge();
        this.showToast(`Added "${product.title}" to cart!`, 'success');
    }

    updateCartBadge() {
        const count = this.cart.reduce((sum, item) => sum + item.quantity, 0);
        document.getElementById('cart-badge-count').textContent = count;
        const uBadge = document.getElementById('cart-badge-count-user');
        if (uBadge) uBadge.textContent = count;
    }

    openCartModal() {
        this.renderCartModal();
        document.getElementById('cart-modal').classList.remove('hidden');
    }

    closeCartModal() {
        document.getElementById('cart-modal').classList.add('hidden');
    }

    async applyCouponCart() {
        const code = document.getElementById('cart-coupon-input').value.trim().toUpperCase();
        const subtotal = this.cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
        const msgDiv = document.getElementById('cart-coupon-msg');

        if (!code) {
            msgDiv.textContent = 'Please enter a coupon code.';
            msgDiv.style.color = '#ef4444';
            return;
        }

        try {
            const response = await fetch('/api/coupons/apply', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ code, orderTotal: subtotal })
            });
            const data = await response.json();

            if (response.ok && data.valid) {
                this.appliedCoupon = data;
                msgDiv.textContent = data.message;
                msgDiv.style.color = '#34d399';
                this.renderCartModal();
                this.showToast(`Promo Code '${data.code}' Applied!`, 'success');
            } else {
                this.appliedCoupon = null;
                msgDiv.textContent = data.message || 'Invalid coupon.';
                msgDiv.style.color = '#ef4444';
                this.renderCartModal();
            }
        } catch (e) {
            msgDiv.textContent = 'Error applying coupon.';
            msgDiv.style.color = '#ef4444';
        }
    }

    renderCartModal() {
        const container = document.getElementById('cart-items-container');
        container.innerHTML = '';

        if (this.cart.length === 0) {
            container.innerHTML = '<p style="text-align: center; color: var(--text-muted); padding: 2rem;">Your shopping cart is empty.</p>';
            document.getElementById('cart-total-display').textContent = this.formatPrice(0);
            document.getElementById('cart-discount-row').classList.add('hidden');
            return;
        }

        let subtotal = 0;
        this.cart.forEach(item => {
            const itemSub = item.price * item.quantity;
            subtotal += itemSub;

            const row = document.createElement('div');
            row.className = 'cart-item-row';
            row.innerHTML = `
                <div class="cart-item-info">
                    <div class="cart-item-title">${item.title}</div>
                    <div class="cart-item-price">${this.formatPrice(item.price)} × ${item.quantity} = ${this.formatPrice(itemSub)}</div>
                    <div style="font-size: 0.75rem; color: var(--text-muted);">Seller: ${item.sellerName}</div>
                </div>
                <div class="cart-qty-ctrl">
                    <button class="cart-qty-btn" onclick="app.updateCartQty(${item.productId}, -1)">-</button>
                    <span>${item.quantity}</span>
                    <button class="cart-qty-btn" onclick="app.updateCartQty(${item.productId}, 1)">+</button>
                    <button class="btn btn-danger btn-sm" onclick="app.removeFromCart(${item.productId})"><i class="fa-solid fa-trash"></i></button>
                </div>
            `;
            container.appendChild(row);
        });

        document.getElementById('cart-total-display').textContent = this.formatPrice(subtotal);

        const discountRow = document.getElementById('cart-discount-row');
        if (this.appliedCoupon && this.appliedCoupon.valid) {
            discountRow.classList.remove('hidden');
            document.getElementById('cart-discount-display').textContent = `-${this.formatPrice(this.appliedCoupon.discountAmount)}`;
        } else {
            discountRow.classList.add('hidden');
        }
    }

    updateCartQty(productId, delta) {
        const item = this.cart.find(i => i.productId === productId);
        if (!item) return;

        item.quantity += delta;
        if (item.quantity <= 0) {
            this.removeFromCart(productId);
            return;
        }

        localStorage.setItem('sm_cart', JSON.stringify(this.cart));
        this.updateCartBadge();
        this.renderCartModal();
    }

    removeFromCart(productId) {
        this.cart = this.cart.filter(i => i.productId !== productId);
        localStorage.setItem('sm_cart', JSON.stringify(this.cart));
        this.updateCartBadge();
        this.renderCartModal();
    }

    // CHECKOUT & PLACE ORDER
    openCheckoutModal() {
        if (this.cart.length === 0) {
            this.showToast('Your cart is empty. Add products first!', 'info');
            return;
        }
        if (!this.currentUser) {
            this.closeCartModal();
            this.openModal('login');
            this.showToast('Please login as Buyer to checkout.', 'info');
            return;
        }

        let total = this.cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
        if (this.appliedCoupon && this.appliedCoupon.valid) {
            total = Math.max(0, total - this.appliedCoupon.discountAmount);
            const discInfo = document.getElementById('checkout-discount-info');
            discInfo.classList.remove('hidden');
            discInfo.textContent = `Promo Coupon '${this.appliedCoupon.code}' Applied (-${this.formatPrice(this.appliedCoupon.discountAmount)})`;
        }

        document.getElementById('checkout-total-price').textContent = this.formatPrice(total);
        this.closeCartModal();
        document.getElementById('checkout-modal').classList.remove('hidden');
    }

    closeCheckoutModal() {
        document.getElementById('checkout-modal').classList.add('hidden');
    }

    async submitCheckout(event) {
        event.preventDefault();
        const address = document.getElementById('checkout-address').value.trim();
        if (!address) {
            this.showToast('Please enter shipping address.', 'error');
            return;
        }

        let totalAmount = this.cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
        if (this.appliedCoupon && this.appliedCoupon.valid) {
            totalAmount = Math.max(0, totalAmount - this.appliedCoupon.discountAmount);
        }

        const orderPayload = {
            buyerId: this.currentUser.id,
            buyerName: this.currentUser.fullName,
            buyerEmail: this.currentUser.email,
            totalAmount: totalAmount,
            shippingAddress: address,
            items: this.cart.map(i => ({
                productId: i.productId,
                title: i.title,
                price: i.price,
                quantity: i.quantity,
                sellerId: i.sellerId
            }))
        };

        const btn = document.getElementById('btn-confirm-order');
        btn.disabled = true;
        btn.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> Confirming Order...';

        try {
            const response = await fetch('/api/orders/checkout', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(orderPayload)
            });
            const data = await response.json();

            if (response.ok && data.success) {
                this.showToast('Order Placed Successfully!', 'success');
                const newOrder = data.order;
                this.cart = [];
                this.appliedCoupon = null;
                localStorage.removeItem('sm_cart');
                this.updateCartBadge();
                this.closeCheckoutModal();
                
                if (newOrder) {
                    this.openInvoiceModal(newOrder);
                } else {
                    this.showBuyerOrders();
                }
            } else {
                this.showToast(data.message || 'Checkout failed.', 'error');
            }
        } catch (error) {
            this.showToast('Server error during checkout.', 'error');
        } finally {
            btn.disabled = false;
            btn.innerHTML = '<span>Confirm Order Now</span> <i class="fa-solid fa-circle-check"></i>';
        }
    }

    // BUYER ORDER HISTORY & PRINTABLE INVOICE
    async showBuyerOrders() {
        if (!this.currentUser) return;
        this.showView('buyer-orders-view');

        const container = document.getElementById('buyer-orders-list');
        container.innerHTML = '<p class="text-center"><i class="fa-solid fa-spinner fa-spin"></i> Loading Order History...</p>';

        try {
            const response = await fetch(`/api/orders/buyer/${this.currentUser.id}`);
            const data = await response.json();

            if (data.success && data.orders) {
                container.innerHTML = '';
                if (data.orders.length === 0) {
                    container.innerHTML = '<div class="empty-state-card text-center"><i class="fa-solid fa-box-open fa-3x" style="color: var(--text-muted); margin-bottom: 1rem;"></i><h3>No Orders Placed Yet</h3><p>Start shopping to place your first order!</p></div>';
                    return;
                }

                data.orders.forEach(o => {
                    const card = document.createElement('div');
                    card.className = 'order-card';
                    const statusClass = (o.status || 'pending').toLowerCase();
                    const canCancel = statusClass === 'pending' || statusClass === 'processing';

                    card.innerHTML = `
                        <div class="order-header">
                            <div><strong>Order #${o.id}</strong> | Date: ${o.orderDate || 'Recent'}</div>
                            <div>
                                <span class="status-chip ${statusClass}">${o.status}</span>
                                <button class="btn btn-outline btn-sm" style="margin-left:0.5rem;" onclick='app.openInvoiceModal(${JSON.stringify(o)})'><i class="fa-solid fa-receipt"></i> Invoice</button>
                                ${canCancel ? `<button class="btn btn-danger btn-sm" style="margin-left:0.5rem;" onclick="app.cancelOrder(${o.id})"><i class="fa-solid fa-ban"></i> Cancel Order</button>` : ''}
                            </div>
                        </div>
                        <div style="font-size: 0.9rem; margin-bottom: 0.5rem;">
                            <strong>Delivery Address:</strong> ${o.shippingAddress}
                        </div>
                        <div style="font-size: 0.9rem; margin-bottom: 0.75rem;">
                            <strong>Items (${o.items ? o.items.length : 0}):</strong>
                            <ul style="padding-left: 1.25rem; color: var(--text-muted);">
                                ${o.items ? o.items.map(i => `<li>${i.title} × ${i.quantity} (${this.formatPrice(i.price)})</li>`).join('') : ''}
                            </ul>
                        </div>
                        <div style="font-size: 1.1rem; font-weight: 800; color: var(--secondary);">
                            Total: ${this.formatPrice(o.totalAmount)}
                        </div>
                    `;
                    container.appendChild(card);
                });
            }
        } catch (e) {
            container.innerHTML = '<p style="color: #ef4444;">Failed to load order history.</p>';
        }
    }

    openInvoiceModal(order) {
        const body = document.getElementById('printable-invoice-body');
        const gstTax = Math.round(order.totalAmount * 0.18 * 100.0) / 100.0;
        const subtotalBeforeTax = Math.round((order.totalAmount - gstTax) * 100.0) / 100.0;

        let itemsHtml = '';
        if (order.items) {
            order.items.forEach(i => {
                const itemTotal = i.price * i.quantity;
                itemsHtml += `
                    <tr>
                        <td>${i.title}</td>
                        <td>${i.quantity}</td>
                        <td>${this.formatPrice(i.price)}</td>
                        <td>${this.formatPrice(itemTotal)}</td>
                    </tr>
                `;
            });
        }

        body.innerHTML = `
            <div class="invoice-box">
                <div class="invoice-header-row">
                    <div class="invoice-brand">Saranya<span>Mart</span></div>
                    <div class="invoice-title-tag">TAX INVOICE</div>
                </div>
                <div class="invoice-meta-grid">
                    <div>
                        <strong>Billed To:</strong><br>
                        ${order.buyerName}<br>
                        ${order.buyerEmail}<br>
                        ${order.shippingAddress}
                    </div>
                    <div style="text-align: right;">
                        <strong>Invoice No:</strong> #INV-${order.id}<br>
                        <strong>Order Date:</strong> ${order.orderDate || 'Recent'}<br>
                        <strong>Payment Status:</strong> Paid (Verified)<br>
                        <strong>GSTIN:</strong> 33AAACS1947M1Z5
                    </div>
                </div>
                <table class="invoice-table">
                    <thead>
                        <tr>
                            <th>Item Description</th>
                            <th>Qty</th>
                            <th>Unit Price</th>
                            <th>Total</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${itemsHtml}
                    </tbody>
                </table>
                <table class="invoice-summary-table">
                    <tr>
                        <td>Subtotal:</td>
                        <td>${this.formatPrice(subtotalBeforeTax)}</td>
                    </tr>
                    <tr>
                        <td>GST (18%):</td>
                        <td>${this.formatPrice(gstTax)}</td>
                    </tr>
                    <tr class="invoice-grand-total">
                        <td>Total Paid:</td>
                        <td>${this.formatPrice(order.totalAmount)}</td>
                    </tr>
                </table>
            </div>
        `;

        document.getElementById('invoice-modal').classList.remove('hidden');
    }

    closeInvoiceModal() {
        document.getElementById('invoice-modal').classList.add('hidden');
    }

    async cancelOrder(orderId) {
        if (!confirm('Are you sure you want to cancel this order? Item stock will be automatically restored.')) return;
        try {
            const response = await fetch(`/api/orders/${orderId}/cancel`, { method: 'PUT' });
            const data = await response.json();
            if (data.success) {
                this.showToast(data.message, 'success');
                this.showBuyerOrders();
                this.loadCatalog();
            } else {
                this.showToast(data.message || 'Order could not be cancelled.', 'error');
            }
        } catch (e) {
            this.showToast('Error cancelling order.', 'error');
        }
    }

    // SELLER DASHBOARD ENGINE & LOW STOCK RESTOCK (Week 7)
    async loadSellerDashboard() {
        if (!this.currentUser) return;

        try {
            const prodRes = await fetch(`/api/products/seller/${this.currentUser.id}`);
            const prodData = await prodRes.json();

            if (prodData.success && prodData.products) {
                const tbody = document.getElementById('seller-products-table-body');
                tbody.innerHTML = '';
                let totalValue = 0;

                prodData.products.forEach(p => {
                    totalValue += (p.price * p.stockQuantity);
                    const isLowStock = p.stockQuantity < 5;
                    const tr = document.createElement('tr');
                    tr.innerHTML = `
                        <td>#${p.id}</td>
                        <td><strong>${p.title}</strong></td>
                        <td>${p.category}</td>
                        <td class="text-emerald">${this.formatPrice(p.price)}</td>
                        <td>
                            ${p.stockQuantity} pcs
                            ${isLowStock ? '<span class="badge-low-stock" style="margin-left:0.4rem;">Low Stock</span>' : ''}
                        </td>
                        <td><i class="fa-solid fa-star text-amber"></i> ${p.averageRating ? p.averageRating.toFixed(1) : '5.0'} (${p.reviewCount || 0})</td>
                        <td>
                            <button class="btn btn-secondary btn-sm" title="Quick Restock +10" onclick="app.quickRestockSeller(${p.id})"><i class="fa-solid fa-cubes-stacked"></i> +10 Stock</button>
                            <button class="btn btn-outline btn-sm" onclick="app.openProductModal(${p.id})"><i class="fa-solid fa-pen"></i> Edit</button>
                            <button class="btn btn-danger btn-sm" onclick="app.deleteProductSeller(${p.id})"><i class="fa-solid fa-trash"></i></button>
                        </td>
                    `;
                    tbody.appendChild(tr);
                });

                document.getElementById('seller-stat-products').textContent = prodData.products.length;
                document.getElementById('seller-stat-value').textContent = this.formatPrice(totalValue);
            }

            const orderRes = await fetch(`/api/orders/seller/${this.currentUser.id}`);
            const orderData = await orderRes.json();

            if (orderData.success && orderData.orders) {
                const tbody = document.getElementById('seller-orders-table-body');
                tbody.innerHTML = '';
                document.getElementById('seller-stat-orders').textContent = orderData.orders.length;

                orderData.orders.forEach(o => {
                    const tr = document.createElement('tr');
                    tr.innerHTML = `
                        <td>#${o.id}</td>
                        <td><strong>${o.buyerName}</strong><br><small style="color:var(--text-muted);">${o.buyerEmail}</small></td>
                        <td>${o.items ? o.items.map(i => `${i.title} (×${i.quantity})`).join(', ') : ''}</td>
                        <td class="text-emerald">${this.formatPrice(o.totalAmount)}</td>
                        <td>${o.orderDate || 'Recent'}</td>
                        <td>
                            <select onchange="app.updateOrderStatusSeller(${o.id}, this.value)" class="form-select" style="padding:0.25rem 0.5rem; font-size:0.8rem;">
                                <option value="Pending" ${o.status === 'Pending' ? 'selected' : ''}>Pending</option>
                                <option value="Processing" ${o.status === 'Processing' ? 'selected' : ''}>Processing</option>
                                <option value="Delivered" ${o.status === 'Delivered' ? 'selected' : ''}>Delivered</option>
                                <option value="Cancelled" ${o.status === 'Cancelled' ? 'selected' : ''}>Cancelled</option>
                            </select>
                        </td>
                    `;
                    tbody.appendChild(tr);
                });
            }
        } catch (e) {
            console.error('Seller Dashboard Load Error:', e);
        }
    }

    async quickRestockSeller(productId) {
        const prod = this.allProducts.find(p => p.id === productId);
        if (!prod) return;

        const newStock = prod.stockQuantity + 10;
        const payload = { ...prod, stockQuantity: newStock };

        try {
            const response = await fetch(`/api/products/${productId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            const data = await response.json();
            if (data.success) {
                this.showToast(`Restocked "${prod.title}"! New stock: ${newStock} pcs`, 'success');
                this.loadCatalog();
                this.loadSellerDashboard();
            }
        } catch (e) {
            this.showToast('Error restocking inventory.', 'error');
        }
    }

    openProductModal(productId = null) {
        document.getElementById('product-id').value = productId || '';
        const form = document.getElementById('form-product');
        form.reset();

        if (productId) {
            document.getElementById('product-modal-title').innerHTML = '<i class="fa-solid fa-pen"></i> Edit Product Listing';
            const prod = this.allProducts.find(p => p.id === productId);
            if (prod) {
                document.getElementById('product-id').value = prod.id;
                document.getElementById('product-title').value = prod.title;
                document.getElementById('product-category').value = prod.category;
                document.getElementById('product-price').value = prod.price;
                document.getElementById('product-stock').value = prod.stockQuantity;
                document.getElementById('product-desc').value = prod.description;
                document.getElementById('product-image').value = prod.imageUrl;
            }
        } else {
            document.getElementById('product-modal-title').innerHTML = '<i class="fa-solid fa-plus"></i> Add New Product';
        }

        document.getElementById('product-modal').classList.remove('hidden');
    }

    closeProductModal() {
        document.getElementById('product-modal').classList.add('hidden');
    }

    async submitProductForm(event) {
        event.preventDefault();
        const id = document.getElementById('product-id').value;
        const title = document.getElementById('product-title').value.trim();
        const category = document.getElementById('product-category').value;
        const price = parseFloat(document.getElementById('product-price').value);
        const stockQuantity = parseInt(document.getElementById('product-stock').value);
        const description = document.getElementById('product-desc').value.trim();
        const imageUrl = document.getElementById('product-image').value.trim();

        const payload = {
            title, category, price, stockQuantity, description, imageUrl,
            sellerId: this.currentUser.id,
            sellerName: this.currentUser.fullName
        };

        const method = id ? 'PUT' : 'POST';
        const url = id ? `/api/products/${id}` : '/api/products';

        try {
            const response = await fetch(url, {
                method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            const data = await response.json();

            if (response.ok && data.success) {
                this.showToast(data.message, 'success');
                this.closeProductModal();
                this.loadCatalog();
                this.loadSellerDashboard();
            } else {
                this.showToast(data.message || 'Product save failed.', 'error');
            }
        } catch (e) {
            this.showToast('Server error saving product.', 'error');
        }
    }

    async deleteProductSeller(productId) {
        if (!confirm('Are you sure you want to delete this product listing?')) return;
        try {
            const response = await fetch(`/api/products/${productId}`, { method: 'DELETE' });
            const data = await response.json();
            if (data.success) {
                this.showToast(data.message, 'success');
                this.loadCatalog();
                this.loadSellerDashboard();
            }
        } catch (e) {
            this.showToast('Error deleting product.', 'error');
        }
    }

    async updateOrderStatusSeller(orderId, newStatus) {
        try {
            const response = await fetch(`/api/orders/${orderId}/status`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ status: newStatus })
            });
            const data = await response.json();
            if (data.success) {
                this.showToast(data.message, 'success');
            }
        } catch (e) {
            this.showToast('Error updating order status.', 'error');
        }
    }

    // ADMIN DASHBOARD & CSV REPORT EXPORT
    async loadAdminData() {
        try {
            const userRes = await fetch('/api/users');
            const userData = await userRes.json();

            if (userData.success && userData.users) {
                this.adminUsers = userData.users;
                const tbody = document.getElementById('admin-users-tbody');
                tbody.innerHTML = '';
                let sellers = 0, buyers = 0;

                userData.users.forEach(u => {
                    if (u.role === 'seller') sellers++;
                    if (u.role === 'buyer') buyers++;

                    const tr = document.createElement('tr');
                    tr.innerHTML = `
                        <td>#${u.id}</td>
                        <td><strong>${u.fullName}</strong></td>
                        <td>${u.email}</td>
                        <td><span class="chip-role ${u.role}">${u.role}</span></td>
                        <td>${u.createdAt || 'Recent'}</td>
                    `;
                    tbody.appendChild(tr);
                });

                document.getElementById('admin-total-users').textContent = userData.users.length;
                document.getElementById('admin-total-sellers').textContent = sellers;
                document.getElementById('admin-total-buyers').textContent = buyers;
            }

            const orderRes = await fetch('/api/orders');
            const orderData = await orderRes.json();

            if (orderData.success && orderData.orders) {
                this.adminOrders = orderData.orders;
                const tbody = document.getElementById('admin-orders-tbody');
                tbody.innerHTML = '';
                document.getElementById('admin-total-orders').textContent = orderData.orders.length;

                orderData.orders.forEach(o => {
                    const tr = document.createElement('tr');
                    tr.innerHTML = `
                        <td>#${o.id}</td>
                        <td><strong>${o.buyerName}</strong></td>
                        <td>${o.shippingAddress}</td>
                        <td class="text-emerald">${this.formatPrice(o.totalAmount)}</td>
                        <td><span class="status-chip ${(o.status||'pending').toLowerCase()}">${o.status}</span></td>
                        <td>
                            <button class="btn btn-outline btn-sm" onclick='app.openInvoiceModal(${JSON.stringify(o)})'><i class="fa-solid fa-receipt"></i> Invoice</button>
                        </td>
                    `;
                    tbody.appendChild(tr);
                });
            }

            const prodRes = await fetch('/api/products?admin=true');
            const prodData = await prodRes.json();

            if (prodData.success && prodData.products) {
                const tbody = document.getElementById('admin-products-tbody');
                tbody.innerHTML = '';

                prodData.products.forEach(p => {
                    const tr = document.createElement('tr');
                    tr.innerHTML = `
                        <td>#${p.id}</td>
                        <td><strong>${p.title}</strong></td>
                        <td>${p.sellerName}</td>
                        <td class="text-emerald">${this.formatPrice(p.price)}</td>
                        <td>${p.category}</td>
                        <td><span class="status-chip ${p.status === 'flagged' ? 'pending' : 'delivered'}">${p.status}</span></td>
                        <td>
                            <button class="btn btn-danger btn-sm" onclick="app.adminRemoveProduct(${p.id})">
                                <i class="fa-solid fa-trash"></i> Remove Product
                            </button>
                        </td>
                    `;
                    tbody.appendChild(tr);
                });
            }

        } catch (e) {
            console.error('Admin Load Error:', e);
        }
    }

    exportUsersCSV() {
        if (!this.adminUsers || this.adminUsers.length === 0) {
            this.showToast('No user data available to export.', 'info');
            return;
        }

        let csv = 'ID,Full Name,Email,Role,Registered Date\n';
        this.adminUsers.forEach(u => {
            csv += `"${u.id}","${u.fullName}","${u.email}","${u.role}","${u.createdAt || ''}"\n`;
        });

        this.downloadCSV(csv, 'saranyamart_users_report.csv');
        this.showToast('Users CSV Report exported!', 'success');
    }

    exportOrdersCSV() {
        if (!this.adminOrders || this.adminOrders.length === 0) {
            this.showToast('No order data available to export.', 'info');
            return;
        }

        let csv = 'Order ID,Buyer Name,Buyer Email,Total Amount (INR),Status,Shipping Address,Order Date\n';
        this.adminOrders.forEach(o => {
            csv += `"${o.id}","${o.buyerName}","${o.buyerEmail}","${o.totalAmount}","${o.status}","${o.shippingAddress}","${o.orderDate || ''}"\n`;
        });

        this.downloadCSV(csv, 'saranyamart_orders_report.csv');
        this.showToast('Orders CSV Report exported!', 'success');
    }

    downloadCSV(csvContent, fileName) {
        const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
        const link = document.createElement('a');
        const url = URL.createObjectURL(blob);
        link.setAttribute('href', url);
        link.setAttribute('download', fileName);
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }

    switchAdminTab(tab) {
        ['users', 'orders', 'products'].forEach(t => {
            document.getElementById(`btn-admin-tab-${t}`).classList.remove('active');
            document.getElementById(`admin-tab-view-${t}`).classList.add('hidden');
        });

        document.getElementById(`btn-admin-tab-${tab}`).classList.add('active');
        document.getElementById(`admin-tab-view-${tab}`).classList.remove('hidden');
    }

    async adminRemoveProduct(productId) {
        if (!confirm('Admin Action: Remove this inappropriate product from platform?')) return;
        try {
            const response = await fetch(`/api/products/${productId}`, { method: 'DELETE' });
            const data = await response.json();
            if (data.success) {
                this.showToast('Product removed by Admin.', 'success');
                this.loadCatalog();
                this.loadAdminData();
            }
        } catch (e) {
            this.showToast('Error removing product.', 'error');
        }
    }

    // View Routing
    renderRoleDashboard() {
        if (!this.currentUser) {
            this.showHome();
            return;
        }

        const role = (this.currentUser.role || 'buyer').toLowerCase();

        if (role === 'admin') {
            this.showView('admin-dashboard');
            this.loadAdminData();
        } else if (role === 'seller') {
            this.showView('seller-dashboard');
            this.loadSellerDashboard();
        } else {
            this.showHome();
        }
    }

    showView(viewId) {
        ['hero-view', 'buyer-orders-view', 'seller-dashboard', 'admin-dashboard'].forEach(v => {
            document.getElementById(v).classList.add('hidden');
        });
        document.getElementById(viewId).classList.remove('hidden');
    }

    showHome() {
        this.showView('hero-view');
        this.loadCatalog();
    }

    showToast(message, type = 'info') {
        const container = document.getElementById('toast-container');
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        const iconClass = type === 'success' ? 'fa-circle-check' : (type === 'error' ? 'fa-circle-xmark' : 'fa-circle-info');
        toast.innerHTML = `<i class="fa-solid ${iconClass}"></i><span>${message}</span>`;
        container.appendChild(toast);
        setTimeout(() => {
            toast.style.opacity = '0';
            setTimeout(() => toast.remove(), 300);
        }, 4000);
    }

    // ==========================================================================
    // AI CHATBOT WIDGET CONTROLLER (Week 9 & 10 Phase 3)
    // ==========================================================================
    toggleChatbot() {
        const panel = document.getElementById('ai-chat-panel');
        if (panel) {
            panel.classList.toggle('hidden');
            if (!panel.classList.contains('hidden')) {
                const input = document.getElementById('chat-input-text');
                if (input) input.focus();
                this.scrollChatToBottom();
            }
        }
    }

    sendQuickChat(questionText) {
        const input = document.getElementById('chat-input-text');
        if (input) {
            input.value = questionText;
            this.submitChatMessage(new Event('submit'));
        }
    }

    triggerCategoryFromChat(categoryName) {
        this.showHome();
        const tabBtn = document.querySelector(`.cat-tab[data-cat="${categoryName}"]`);
        if (tabBtn) {
            this.selectCategory(categoryName, tabBtn);
        }
        this.renderChatMessage(`🔍 Filtering marketplace products for **${categoryName}**...`, 'bot');
        this.showToast(`Catalog filtered by category: ${categoryName}`, 'success');
    }

    clearChatHistory() {
        const body = document.getElementById('chat-messages-body');
        if (!body) return;
        
        body.innerHTML = `
            <div class="chat-message bot">
                <div class="msg-bubble">
                    👋 Hi! I'm your <strong>SaranyaMart AI Assistant</strong>. Conversation cleared. How can I help you?
                    <div class="msg-time">Just now</div>
                </div>
            </div>
            <div class="chat-chips-container" id="chat-quick-chips">
                <button class="chat-chip" onclick="app.triggerCategoryFromChat('Laptop')">💻 Browse Laptops</button>
                <button class="chat-chip" onclick="app.triggerCategoryFromChat('Mobile')">📱 Browse Mobiles</button>
                <button class="chat-chip" onclick="app.sendQuickChat('What is your return policy?')">🔄 Return Policy</button>
                <button class="chat-chip" onclick="app.sendQuickChat('How long does shipping take?')">🚚 Shipping Info</button>
                <button class="chat-chip" onclick="app.sendQuickChat('What payment methods are supported?')">💳 Payments</button>
                <button class="chat-chip" onclick="app.sendQuickChat('How do I apply a coupon code?')">🎟️ Promo Coupons</button>
            </div>
        `;
        this.showToast('Chat history reset.', 'info');
    }

    async submitChatMessage(event) {
        if (event && event.preventDefault) event.preventDefault();
        const input = document.getElementById('chat-input-text');
        const messageText = input ? input.value.trim() : '';

        if (!messageText) return;

        // Render user message bubble
        this.renderChatMessage(messageText, 'user');
        input.value = '';

        // Hide chips after first message
        const chips = document.getElementById('chat-quick-chips');
        if (chips) chips.style.display = 'none';

        // Show typing indicator
        const typing = document.getElementById('chat-typing-indicator');
        if (typing) typing.classList.remove('hidden');
        this.scrollChatToBottom();

        try {
            const response = await fetch('/api/chat', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ message: messageText })
            });

            const data = await response.json();

            if (typing) typing.classList.add('hidden');

            if (data.success && (data.reply || (data.data && data.data.reply))) {
                const reply = data.reply || data.data.reply;
                const provider = (data.data && data.data.provider) ? data.data.provider : 'mock';
                
                const pill = document.getElementById('chat-provider-pill');
                if (pill) pill.textContent = provider.toUpperCase() === 'GEMINI' ? 'Gemini AI' : 'Mock Mode';

                this.renderChatMessage(reply, 'bot');
            } else if (data.error && data.error.message) {
                this.renderChatMessage(`⚠️ ${data.error.message}`, 'bot');
            } else {
                this.renderChatMessage("🤖 I am here to help! Please try asking another question.", 'bot');
            }
        } catch (error) {
            if (typing) typing.classList.add('hidden');
            this.renderChatMessage("🤖 *SaranyaMart Assistant*: Our AI service is operating in offline mode. Feel free to ask about return policies or shipping!", 'bot');
        }
    }

    renderChatMessage(text, sender) {
        const body = document.getElementById('chat-messages-body');
        if (!body) return;

        const msgDiv = document.createElement('div');
        msgDiv.className = `chat-message ${sender}`;

        const timeStr = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
        
        let formattedText = text.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
                                .replace(/\*(.*?)\*/g, '<em>$1</em>')
                                .replace(/\n/g, '<br>');

        msgDiv.innerHTML = `
            <div class="msg-bubble">
                ${formattedText}
                <div class="msg-time">${timeStr}</div>
            </div>
        `;

        body.appendChild(msgDiv);
        this.scrollChatToBottom();
    }

    scrollChatToBottom() {
        const body = document.getElementById('chat-messages-body');
        if (body) {
            body.scrollTop = body.scrollHeight;
        }
    }
}



document.addEventListener('DOMContentLoaded', () => {
    window.app = new SaranyaMartApp();
});
