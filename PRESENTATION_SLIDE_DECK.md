# SaranyaMart — Capstone Review Slide Deck
**Anna University R2025 Specification Checkpoint Review**  
**Presenter & Developer**: rssaranya1947  
**Live Application URL**: `http://localhost:8080`

---

## ─── SLIDE 1: Title & Project Overview ───

### SaranyaMart — Multi-Seller E-Commerce Marketplace
- **Architecture**: Layered MVC (Front Controller) over Java 17 & Spring Boot 2.7.18
- **Database**: Pure Java Storage Engine & SQLite Schema (`db/schema.sql`)
- **Key Features**: Multi-role access (Buyer, Seller, Admin), product catalog, shopping cart, mock checkout, printable GST invoices, star reviews, in-app messaging, and AI Chatbot.

---

## ─── SLIDE 2: Problem Statement & Scope ───

### Problem & Challenge
- Modern e-commerce platforms require seamless multi-seller listing management, role-based authorization, instant catalog filtering, security compliance, and real-time customer support.

### Scope Boundaries (Per Specification)
- **Included**: Buyer/Seller/Admin authentication, product CRUD, coupon engine (`SARANYA10`), printable invoices, security headers, unit testing suite, CI pipeline, and AI Chatbot widget (Phase 3).
- **Excluded**: Real-time WebSockets, external mapping APIs, real payment gateways.

---

## ─── SLIDE 3: System Architecture & Design Patterns ───

```
Browser (Vanilla JS Fetch) ──> SecurityFilter ──> RestController (Front Controller)
                                                        │
                                                        ▼
                                                  ChatService / DAO
                                                        │
                                                        ▼
                                                  DatabaseManager
```

### Applied Design Patterns (Section 12)
- **DAO Pattern**: Abstracted data access (`UserDao`, `ProductDao`, `OrderDao`)
- **Front Controller**: Central dispatch REST endpoints
- **Singleton**: Thread-safe memory store & ID sequence generator
- **Strategy Pattern**: `MockChatProvider` vs. `GeminiChatProvider`
- **Builder Pattern**: Dynamic JSON envelopes & GST invoice construction

---

## ─── SLIDE 4: AI Chatbot Integration (Phase 3) ───

### Architecture & Guardrails (Section 11 & 17)
- **Pluggable Backend**: Interface `ChatProvider` supporting zero-latency `MockChatProvider` and `GeminiChatProvider`.
- **Per-Session Rate Limiting**: Max 10 messages/minute per session ID.
- **Input Cap**: Maximum 300 characters per query.
- **Interactive Floating UI**: Floating action button, typing indicator, active provider badge (`Mock Mode` / `Gemini AI`), and catalog filter action chips (e.g., clicking *"Browse Laptops"* filters main catalog!).

---

## ─── SLIDE 5: Testing, CI Pipeline & Security ───

### Testing & Validation (Section 9 & 18)
- **JUnit 5 Suite**: 8 automated test cases covering `UserDaoTest`, `ProductDaoTest`, and `ChatServiceTest` with 100% pass rate (`BUILD SUCCESS`).
- **CI Pipeline**: GitHub Actions (`.github/workflows/build.yml`) running `mvn -B clean verify` on push.
- **Security Checklist**: Salted bcrypt hashing, `X-Frame-Options: DENY`, `X-XSS-Protection`, and `/api/v1/health` status monitor.

---

## ─── SLIDE 6: Completed vs. Planned Timeline ───

| Phase | Milestone | Status |
| :--- | :--- | :--- |
| **Weeks 1–2** | Auth, Roles, DB Schema | ✅ 100% Completed |
| **Weeks 3–4** | Seller Dashboard & Admin Moderation | ✅ 100% Completed |
| **Weeks 5–6** | Search, Wishlist, Reviews & Invoices | ✅ 100% Completed |
| **Weeks 7–8** | In-App Messaging, Test Suite & CI Pipeline | ✅ 100% Completed |
| **Weeks 9–10**| AI Chatbot Integration & Refinement | ✅ 100% Completed |
| **Week 11** | Final Regression Pass & Review Package | ✅ 100% Completed |
