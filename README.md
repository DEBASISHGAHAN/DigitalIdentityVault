# 🌐 Digital Identity Vault 🚀

A secure and scalable Digital Identity Vault built with Java Spring Boot. This project provides secure user registration, OTP verification, JWT authentication, document upload, and access management. It is designed with security, scalability, and best practices in mind.

📌 Features
- 🔒 User Authentication & Authorization
  - User Registration with OTP Verification (Email-based).
  - Secure Login with JWT Authentication.
  - Role-based access control.

- 📧 Secure OTP Management
  - OTP generation using Redis with TTL.
  - Email-based OTP delivery.

- 📁 Document Management
  - Secure document upload and storage.
  - Document access verification with OTP.

- 📅 Inactivity Notification
  - Scheduled email notifications for inactive users.

- 🌐 Security
  - JWT-based authentication for API security.
  - Password encryption using BCrypt.
  - Redis for OTP and session management.

🚀 Technologies Used
- ☕️ Java 21
- 🍃 Spring Boot 3.4.5
  - Spring Security (JWT)
  - Spring Data JPA (PostgreSQL)
  - Spring Data Redis (Session Management)
  - Spring Scheduler (Inactivity Notifications)
- 📧 Spring Boot Mail (JavaMailSender)
- 🗄️ PostgreSQL (Database)
- 📌 Redis (In-memory data store for OTPs and session token)
- 🌐 JWT (JSON Web Token) for Secure API Access
- 🌐 Lombok for Cleaner Code

⚡️ Getting Started

✅ Prerequisites
- ✅ Java 21 installed
- ✅ PostgreSQL installed and running
- ✅ Redis installed and running
- ✅ Maven installed

✅ Clone the Repository

git clone https://github.com/DEBASISHGAHAN/DigitalIdentityVault.git
