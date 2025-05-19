🔐 SpringBoot JWT Authentication System with OTP Verification + React Frontend
-

A robust and secure fullstack system built with Spring Boot for the backend and React.js for the frontend. It features OTP-based registration, JWT access & refresh token handling, token blacklisting with Redis, and secure role-based access — all optimized for production deployment.

---------------------------------------------------------------------------------
📌 Features
-

🔑 OTP verification during user registration (via email)

🛡️ JWT-based login with access & refresh tokens

♻️ Token refresh endpoint with auto-blacklisting of expired tokens

🚪 Secure logout invalidating both tokens (Redis-backed)

👥 Role-based access control using annotations

📧 SMTP email integration for OTP delivery

🗃️ Redis integration via Docker for session/token handling

🌍 CORS configuration for cross-origin frontend communication

🧱 Production-ready configuration (HTTPS, logging, secrets management)

🖥️ React.js frontend with protected routes, toast notifications, and authentication context

------------------------------------------------------------------------------------
🛠️ Tech Stack
-
| Layer       | Technology                  |
| ----------- | --------------------------- |
| Backend     | Spring Boot 3.x             |
| Auth        | Spring Security, JWT (jjwt) |
| Data Store  | MySQL                       |
| Token Store | Redis (Dockerized)          |
| Email       | Jakarta Mail (SMTP)         |
| Build Tool  | Maven                       |
| Java        | Java 17+                    |
| Frontend    | React.js, Tailwind CSS      |
| Routing     | react-router-dom            |
| Notification| react-toastify              |

----------------------------------------------------------
📦 Prerequisites
-

✅ Java JDK 17+

✅ Maven 3.8+

✅ MySQL (running locally or in Docker)

✅ Docker (for Redis container)

✅ Internet connection (SMTP email service)

✅ Node.js & npm (for frontend setup)

--------------------
🐳 Redis Setup with Docker
-

docker run --name redis-auth -p 6379:6379 -d redis

-----------------------------------
📧 SMTP Setup for OTP Delivery
-

Update your application.properties:

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

⚠️ Use an App Password (not your Gmail login password) to avoid authentication issues.

---------------------------------------------------------------------
⚙️ Core Configuration
-

redis.host=localhost
redis.port=6379

cors.allowed.origins=http://localhost:3000
cors.allowed.methods=GET,POST,PUT,DELETE
cors.allowed.headers=*
cors.allowed.credentials=true

--------------------------------
🧱 Module Structure
--------------------

### Backend
- config, filters, security, service, controller, repository, entity, utils

### Frontend (React.js)
- `/pages`: Login, Register, Dashboard
- `/components`: Header, Footer, ProtectedRoute, LoadingSpinner
- `/context`: AuthContext
- `/App.jsx`: Routing setup with conditional footer and toast messages

🔐 Authentication Flow
--------------

Register: Email OTP verification before saving the user

Login: Returns access & refresh tokens

Token Validation: Validates JWT via filters

Token Refresh: Issues new access token using refresh token

🚪 Logout
-------

Blacklists both access & refresh tokens via Redis

Clears Security Context

🧾 Token Filters
---

JWTAuthenticationFilter: Login

JWTValidationFilter: Validates access token

JWTRefreshFilter: Refresh token logic

All filters check Redis for blacklisted tokens

🔒 Role-Based Authorization
------

Use annotations in controller:

@PreAuthorize("hasRole('ADMIN')")
@GetMapping("/admin/data")
public ResponseEntity<?> getAdminData() {
    // Only accessible by admin role
}

-------------------------------
🚀 Production-Ready Practices
----

🔐 HTTPS support (to be added during deployment)

🔑 Move secrets (DB, mail, jwt) to env variables or secrets manager

🔒 Secure Redis with password if exposed outside

🌐 Restrict CORS to production domains

📊 Logging with SLF4J + Logback (pending addition)

🛡️ Token revocation and Redis TTLs for cleanup

-------------------------------------------
🌐 React Frontend Highlights
----

✅ AuthContext using Context API and localStorage persistence

✅ Protected routes for `/dashboard`

✅ Styled and animated `LoadingSpinner` using Tailwind

✅ Footer shown only on login page

✅ JWT token shown securely on dashboard (overflow handled)

✅ Custom button styles on Register

✅ Responsive layout with Tailwind and custom CSS

✅ Toast notifications for login/register success and errors

-----------------------
✅ Completed ✅
---
✅ OTP Registration Flow

✅ Login with JWT

✅ Refresh Token Mechanism

✅ Token Validation Filter

✅ Logout & Token Blacklisting

✅ Redis + SMTP Integration

✅ CORS Configuration

✅ React Integration with Routing, Auth Context, and Styling

🧩 To Do (Optional Enhancements)
---
Add Swagger/OpenAPI documentation

Add logging (SLF4J, Logback) — In Progress

Enable HTTPS (during production deployment)

Add monitoring (Prometheus/Grafana optional)

Handle persistent login state after page refresh in frontend

🧑‍💻 Author
--
Ankit Pandey • LinkedIn (https://www.linkedin.com/in/ankitpandeyap/)
-
GitHub: @ankitpandeyap
-
