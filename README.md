Roll no: 39
📱 Social Media Platform – Backend (Spring Boot)
🚀 Project Overview

This project is a production-ready Social Media Backend built using Spring Boot, designed to follow real-world backend architecture and security practices.
It focuses on clean code, scalability, authentication, authorization, and microservice communication concepts commonly used in industry-level applications.

The backend supports user registration with email verification, JWT-based authentication, role-based access control, post interactions, and external API integration.

🛠️ Tech Stack

Java

Spring Boot

Spring Web

Spring Data JPA

Spring Security

Spring Validation

Spring Mail

Spring Cache

JWT (JSON Web Token)

PostgreSQL / MySQL

Lombok

WebClient (HTTP Client)

🧩 Architecture

The project follows a layered architecture:

Controller → Service → Repository → Database

Additional layers:

DTO Layer (for API communication)

Security Layer (JWT & Role-based Authorization)

External API Integration Layer

✨ Core Features

User Registration with Email Verification

JWT Authentication

Role-Based Authorization (USER / ADMIN)

Entity Relationships:

One-to-One

One-to-Many

Many-to-Many

DTO-based API design

Pagination, Sorting, and Filtering

Custom JPA Queries

HTTP Client Integration (WebClient)

Spring Caching (for performance optimization)

Secure password handling

Clean separation of concerns

📦 Entities

User

UserInfo

Post

Comment

Like

🔐 Verification Status
public enum VerificationStatus {
    PENDING,
    VERIFIED
}

Each user:

Registers with status PENDING

Receives a verification email

Becomes VERIFIED after email confirmation

📧 Email Verification Flow

User registers

Account created with PENDING status

Verification token (UUID) generated

Verification email sent

User clicks verification link

Account status updated to VERIFIED

User is now allowed to login

Example verification endpoint:

GET /api/auth/verify?token=abc123
🔐 Authentication & Authorization
Authentication (JWT)

User logs in with valid credentials

Account must be VERIFIED

JWT token generated

Client sends token in Authorization header

Authorization

Roles:

ROLE_USER

ROLE_ADMIN

Rules:

Only authenticated users can like or comment

Users can delete only their own posts

Admin can delete any post

Implemented using:

@PreAuthorize

Method-level security

Custom access handling

📄 API Features
DTO Layer

DTOs are used to:

Prevent exposing sensitive fields

Separate internal entities from API responses

Examples:

RegisterRequestDTO

LoginRequestDTO

UserResponseDTO

PostResponseDTO

📊 Pagination, Sorting & Filtering

Supported query parameters:

GET /posts?page=0&size=10
GET /posts?sort=createdAt,desc
GET /posts?userId=5

Implemented using:

Pageable

Custom repository queries

⚡ Spring Cache

Caching is applied for:

Popular posts

User profile data

Annotations used:

@EnableCaching

@Cacheable

@CacheEvict

🌐 HTTP Client Integration

External APIs are accessed using WebClient.

Use cases:

Notify external systems on user registration

Send analytics data on post creation

Integrate moderation or notification services

Handled features:

Timeout handling

Retry logic

Response DTO mapping

📁 Project Structure
src/main/java
 ├── controller
 ├── service
 ├── repository
 ├── dto
 ├── entity
 ├── security
 ├── config
 └── external
🧠 Key Learnings

Real-world authentication & authorization flows

Email verification systems

Secure API design using DTOs

JWT-based stateless authentication

Role-based access control

Spring caching strategies

External service communication

Clean backend architecture

🏁 Future Enhancements

Full-text search

Notification service

Media upload support

Real-time updates using WebSockets

Rate limiting

API documentation using Swagger/OpenAPI

