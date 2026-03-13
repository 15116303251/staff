# Staff Account Service

A Spring Boot-based microservice for user account management in the Staff platform.

## Overview

The Account Service provides RESTful APIs for managing user accounts, including:
- User registration and account creation
- Account retrieval and updates
- Password management and verification
- Account listing and search
- Email and phone number management

## Architecture

This is a multi-module Maven project with the following structure:

```
staff/
├── account-api/          # API definitions and DTOs
├── account-svc/          # Service implementation
├── common/              # Shared utilities and common code
└── pom.xml              # Parent POM
```

## Prerequisites

- Java 8 or higher
- Maven 3.6+
- MySQL 5.7+
- Docker (optional, for containerization)

## Quick Start

### 1. Clone and Build

```bash
git clone <repository-url>
cd staff
mvn clean package
```

### 2. Database Setup

Create a MySQL database:

```sql
CREATE DATABASE staffjoy_account;
CREATE USER 'staffjoy'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON staffjoy_account.* TO 'staffjoy'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Configuration

Copy the development configuration:

```bash
cp account-svc/src/main/resources/application-dev.yml account-svc/src/main/resources/application.yml
```

Edit `application.yml` with your database credentials.

### 4. Run the Service

```bash
cd account-svc
java -jar target/account-svc-*.jar
```

Or using Maven:

```bash
mvn spring-boot:run -pl account-svc
```

## API Documentation

### Account Management

#### Create Account
```http
POST /v1/account/create
Content-Type: application/json
Authorization: Bearer <token>

{
  "name": "John Doe",
  "email": "john@example.com",
  "phoneNumber": "+1234567890"
}
```

#### Get Account
```http
GET /v1/account/get?userId=<user-id>
Authorization: Bearer <token>
```

#### Update Account
```http
POST /v1/account/update
Content-Type: application/json
Authorization: Bearer <token>

{
  "id": "<user-id>",
  "name": "Updated Name",
  "email": "updated@example.com"
}
```

#### List Accounts
```http
GET /v1/account/list?offset=0&limit=10
Authorization: Bearer <token>
```

### Password Management

#### Update Password
```http
POST /v1/account/update_password
Content-Type: application/json
Authorization: Bearer <token>

{
  "userId": "<user-id>",
  "password": "new-password"
}
```

#### Verify Password
```http
POST /v1/account/verify_password
Content-Type: application/json
Authorization: Bearer <token>

{
  "email": "user@example.com",
  "password": "password"
}
```

## Docker Deployment

### Build Docker Image

```bash
docker build -t staffjoy/account-svc:latest -f account-svc/Dockerfile .
```

### Run with Docker Compose

```yaml
version: '3.8'
services:
  account-service:
    image: staffjoy/account-svc:latest
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/staffjoy_account
      - SPRING_DATASOURCE_USERNAME=staffjoy
      - SPRING_DATASOURCE_PASSWORD=password
      - SPRING_PROFILES_ACTIVE=prod
    depends_on:
      - mysql

  mysql:
    image: mysql:5.7
    environment:
      - MYSQL_ROOT_PASSWORD=root
      - MYSQL_DATABASE=staffjoy_account
      - MYSQL_USER=staffjoy
      - MYSQL_PASSWORD=password
    ports:
      - "3306:3306"
```

## Testing

### Unit Tests

```bash
mvn test -pl account-svc
```

### Integration Tests

```bash
mvn verify -pl account-svc
```

### Test Coverage Report

```bash
mvn jacoco:report -pl account-svc
```

## Development

### Code Style

This project uses:
- Google Java Style Guide
- Lombok for boilerplate reduction
- ModelMapper for object mapping
- Spring Boot best practices

### Adding New Features

1. Add DTOs in `account-api` module
2. Implement service logic in `account-svc`
3. Add REST endpoints in `AccountController`
4. Write unit and integration tests
5. Update API documentation

### Database Migrations

For production deployments, use Flyway or Liquibase for database migrations. Currently, the service uses JPA auto-ddl for development.

## Monitoring and Logging

- **Logging**: Structured logging with structlog4j
- **Metrics**: Spring Boot Actuator endpoints
- **Error Tracking**: Sentry integration
- **Health Checks**: `/actuator/health` endpoint

## Security

- JWT-based authentication
- Role-based authorization
- Password hashing with BCrypt
- Input validation with Bean Validation
- SQL injection prevention with JPA

## Performance Considerations

- Connection pooling with HikariCP
- Pagination for list endpoints
- Caching for frequently accessed accounts
- Asynchronous email sending

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For issues and questions:
- Create an issue in the GitHub repository
- Check the API documentation
- Review the troubleshooting guide

## Changelog

### v1.0.0 (2024-01-01)
- Initial release
- Basic account management
- Password handling
- REST API endpoints
- Docker support