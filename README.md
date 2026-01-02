# 🛒 Cartline E-Commerce API

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.x-green)
![Hibernate](https://img.shields.io/badge/Hibernate-ORM-59666C?style=flat&logo=hibernate&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=flat&logo=apachemaven&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-Security-black?style=flat&logo=jsonwebtokens&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Enabled-blue)
![Redis](https://img.shields.io/badge/Redis-Caching-red)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI-brightgreen)

**Cartline** is a backend for an e-commerce platform. It features a secure **Dual-Token Authentication** system, Role-Based Access Control (**RBAC**), and high-performance caching using **Redis**. The application is containerized with Docker for easy deployment and scalability.

---

## Key Features

### Security & Authentication
* **Dual-Token System:** Implemented "Access Token" (Short-lived JWT) and "Refresh Token" (Long-lived, stored in Redis) for secure, seamless user sessions.
* **Role-Based Access Control (RBAC):** Distinct permissions for `USER`, `SELLER`, and `ADMIN`.
* **OTP Verification:** Email verification using OTPs stored in Redis with expiration logic.
* **Secure Profile Management:** Users can update passwords, emails, and personal details securely.

### Product & Catalog
* **Advanced Search:** Filter products by **Brand**, **Category**, **Name**, **Seller**, or any combination of these.
* **Inventory Management:** Sellers and Admins can add, update, and remove products.
* **Category Management:** Admin exclusive control over product categories.

### Shopping Experience
* **Smart Cart:** Add items, update quantities, remove items, or clear the cart instantly.
* **Order Lifecycle:** Place orders and cancel them (restricted to orders not yet `IN_TRANSIT`).

### Seller & Admin Portal
* **Seller Application:** Users can apply to become sellers.
* **Admin Review:** Admins review and approve/reject seller profiles.

---

## Tech Stack

* **Language:** Java 21
* **Framework:** Spring Boot (Web, Security, Data JPA, Validation)
* **Database:** MySQL 8.0
* **Caching & Session:** Redis (for Refresh Tokens & OTPs)
* **Authentication:** Spring Security + JWT (JSON Web Tokens)
* **Containerization:** Docker & Docker Compose
* **Documentation:** SpringDoc OpenAPI (Swagger UI)
* **Build Tool:** Maven

---

## Getting Started

Follow these steps to set up the project locally using Docker.

### Prerequisites
* [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed and running.
* Git installed.

### 1. Clone the Repository
```bash
git clone https://github.com/Yearis/cartline-backend.git
cd cartline-backend
```

**2. Configure Environment**
Create a .env file in the root directory (refer to .env.example) and add your database and JWT secrets.

**3. Build & Run**
```bash
docker-compose up --build
```
The application will start on port 8080.

### 📚 API Documentation
Once the application is running, you can explore and test all endpoints using the interactive Swagger UI:

[View API Docs](https://blog-application-backend-nllp.onrender.com/blog-application/swagger-ui/index.html)

Created by 👨‍💻 [Yearis](https://github.com/Yearis)
