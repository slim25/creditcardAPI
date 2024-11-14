# **Credit Card Management API**

A backend API service built with Spring Boot to manage user registrations, profiles, credit cards, and activity logs. This project demonstrates essential functionality for managing user data securely and efficiently, with role-based access control, JWT authentication, data caching, and data migration. The application is prepared for production-level containerization with Docker.

## **Features**

* **User Registration & Authentication**: Supports two roles (USER and ADMIN) with JWT-based authentication, including refresh token support.
* **Role-Based Functionality**:
  
  * `USER`: Register, populate profile, add and view credit cards.
  * `ADMIN`: View all users, manage user credit cards, view user activity logs.
* **Data Storage & Security**:
  * **MongoDB**: Stores user profiles and activity logs.
  * **PostgreSQL**: Stores sensitive data such as encrypted credit card information.
  * **Redis**: Caches frequently accessed data for performance optimization.
* **Tokenization & Validation**:
  * Simulated card tokenization (using Luhn algorithm on the frontend).
  * Supports token duplication checks and prevents storing duplicated cards.
* **Testing & Code Coverage**:
  * Covered with JUnit and integration tests.
  * Configured for generating code coverage reports with Jacoco.
* **Data Migration**: Uses Liquibase for consistent and manageable data migrations.


## Technologies

* **Java & Spring Boot**
* **Docker & Docker Compose**
* **MongoDB, PostgreSQL, Redis**
* **JWT Authentication** with refresh tokens
* **Liquibase** for database migration
* **Jacoco** for test coverage reporting

## Setup & Run
1. **Clone Repositories**: Ensure both `creditcardAPI` (backend) and `creditcardUI` (frontend) repositories are cloned in the same directory.

3. **Environment Requirements**:
   * **Java 20+**
   * **Docker & Docker Compose**
7. **Run the Application**: From the `creditcardAPI` directory, run:

    `docker-compose up --build`

    This command will start the backend, frontend, and necessary databases (MongoDB, PostgreSQL, Redis) in Docker containers. The application will have five containers in total: `backend`, `frontend`, `mongo`, `redis`, and `postgres`.

14. **Accessing Services**:

    *   **Frontend**: `http://localhost:3000`
    *   **Backend API**: `http://localhost:8080`


## API Endpoints

#### **Authentication & User Management**

* **Register** (POST `/api/auth/register`): Register a new user.
* **Login** (POST `/api/auth/login`): Authenticate user and obtain JWT.
* **Refresh Token** (POST `/api/auth/refresh-token`): Refresh the access token using a refresh token.
* **Logout** (POST `/api/auth/logout`): Log out user and invalidate refresh token.

#### User Profile & Credit Cards

* **Get User Profile** (GET `/api/user-profiles/{userId}`): Retrieve profile information.
* **Add Credit Card** (POST `/api/credit-cards`): Add a credit card to user profile.
* **Remove Credit Card** (DELETE `/api/credit-cards`): Remove a specific credit card (Admin-only).

#### Admin Functionalities

* **Get All User Profiles** (GET `/api/user-profiles/admin/all`): Retrieve all user profiles.
* **View User Activity Logs** (GET `/api/admin/activity-logs/user/{userId}/{limit}`): Retrieve activity logs of a user.

## Testing

* **Unit and Integration Tests**: Run tests with Gradle

`./gradlew test`

* **Code Coverage**: Generate Jacoco reports

`./gradlew jacocoTestReport`

## Caching & Data Storage

* **Caching**: Caches user profile and activity logs in Redis to optimize data retrieval.
* **Data Migration**: Uses Liquibase to manage and apply database schema changes.

## Sample Data

Use the following sample card details for testing purposes:

Card Type	Card Number	CVV	Expiry Date

Visa	4111 1111 1111 1111	123	12/25

MasterCard	5555 5555 5555 4444	456	11/26

Discover	6011 1111 1111 1117	012	08/24

JCB	3530 1113 3330 0000	345	09/28

Diners Club	3056 9309 0259 04	678	10/29
