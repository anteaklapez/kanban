# Kanban Board API

Modern REST API application for managing Kanban boards with real-time WebSocket features, built on the Spring Boot platform.

## Contents

- [Description of technology choices](#description-of-technology-choices)
- [Features](#features)
- [Instructions for running](#instructions-for-running)
- [API documentation](#api-documentation)
- [WebSocket integration](#websocket-integration)
- [Testing](#testing)
- [Configuration](#configuration)

## Description of technology choices

### **Backend Framework**
- **Spring Boot 3.5.0** - Latest version for modern Java applications
- **Java 21** - LTS version with latest performance improvements
- **Spring Security 6** - Advanced security with JWT authentication
- **Spring Data JPA** - Simplifies database operations

### **Database**
- **PostgreSQL 15** - Robust, ACID-compliant relational database
- **Testcontainers** - Isolated tests with real PostgreSQL instances

### **Real-time communication**
- **Spring WebSocket** - Bidirectional real-time communication
- **STOMP protocol** - Structured messages over WebSocket
- **SimpMessagingTemplate** - Easy message sending to subscribers

### **Security**
- **JWT (JSON Web Tokens)** - Stateless authentication
- **BCrypt** - Secure password hashing
- **JJWT 0.11.5** - Modern JWT library

### **Additional features**
- **JSON Patch RFC 6902** - Standardized partial updates
- **Bucket4j** - Rate limiting for API protection
- **OpenAPI/Swagger** - Automatic API documentation
- **Spring Cache** - Caching for performance improvement

### **DevOps and deployment**
- **Docker & Docker Compose** - Containerization and orchestration
- **Maven** - Dependency management and build process
- **Spring Boot Actuator** - Monitoring and health checks

## Features

### **Authentication and authorization**
- User registration and login
- JWT token authentication
- Secure WebSocket authentication

### **Task management**
- CRUD operations for tasks
- Pagination and filtering by status
- JSON Patch support for partial updates
- Cache optimization for reading

### **Real-time features**
- Instant notifications for all task changes
- WebSocket broadcasting on `/topic/tasks`
- Authenticated WebSocket access

### **API features**
- RESTful API design
- Comprehensive input validation
- Custom enum validators
- OpenAPI documentation
- Rate limiting protection

## Instructions for running

### **Prerequisites**
- Docker and Docker Compose
- Java 21 (for local running)
- Maven 3.9+ (for local running)

### **1. Running with Docker Compose (recommended)**

```bash
# Clone the repository
git clone 
cd kanban

# Start the application and database
docker-compose up -d

# The application will be available at http://localhost:8080
```

### **2. Local running**

```bash
# Start PostgreSQL database
docker run -d \
  --name kanban-db \
  -e POSTGRES_DB=kanban \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=admin \
  -p 5432:5432 \
  postgres:15-alpine

# Start the application
mvn spring-boot:run
```

### **3. Build and run JAR**

```bash
# Build the application
mvn clean package -DskipTests

# Run the JAR
java -jar target/kanban-0.0.1-SNAPSHOT.jar
```

## API documentation

After starting the application, documentation is available at:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI spec**: http://localhost:8080/v3/api-docs

### **Main endpoints**

#### **Authentication**
```http
POST /api/auth/register
POST /api/auth/login
```

#### **Tasks**
```http
GET    /api/tasks              # Get all tasks (paginated)
GET    /api/tasks/{id}         # Get specific task
POST   /api/tasks              # Create new task
PUT    /api/tasks/{id}         # Update task
PATCH  /api/tasks/{id}         # Partial update (JSON Patch)
DELETE /api/tasks/{id}         # Delete task
```

### **Usage example**

```bash
# Registration
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe", 
    "email": "john@example.com",
    "password": "password123"
  }'

# Creating a task
curl -X POST http://localhost:8080/api/tasks \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "New task",
    "description": "Task description",
    "status": "TO_DO",
    "priority": "HIGH"
  }'
```

## WebSocket integration

### **Client configuration**

```javascript
// Connecting to WebSocket
const socket = new SockJS('http://localhost:8080/ws?token=YOUR_JWT_TOKEN');
const stompClient = Stomp.over(socket);

// Connecting and subscribing
stompClient.connect({}, function(frame) {
    console.log('Connected: ' + frame);
    
    // Subscribe to task notifications
    stompClient.subscribe('/topic/tasks', function(message) {
        const event = JSON.parse(message.body);
        console.log('Task event:', event);
    });
});
```

### **Event format**
```json
{
  "eventType": "CREATED|UPDATED|DELETED",
  "data": {
    "id": "task-uuid",
    "title": "New Task",
    "description": "Task description",
    "status": "TO_DO",
    "priority": "HIGH"
  }
}


```

### **Event Types:**
- `CREATED` - New task was created
- `UPDATED` - Existing task was modified
- `DELETED` - Task was removed

## Testing

### **Running tests**
```bash
# All tests
mvn test

# Specific test class
mvn test -Dtest=TaskServiceIntegrationTest

# Tests with coverage
mvn test jacoco:report
```

### **Test configuration**
- **Testcontainers** - Real PostgreSQL for integration tests
- **@Transactional** - Automatic rollback of test data
- **Test profile** - Separate configuration in `application-test.properties`

## Configuration

### **Environment variables**

```bash
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/kanban
SPRING_DATASOURCE_USERNAME=admin
SPRING_DATASOURCE_PASSWORD=admin

# JWT configuration
JWT_SECRET_KEY=your-secret-key
JWT_EXPIRATION=3600000

# Server port
SERVER_PORT=8080
```

### **Profiles**
- **default** - Production configuration
- **test** - Test configuration with Testcontainers

### **Health check**
- **Endpoint**: http://localhost:8080/actuator/health
- **Metrics**: http://localhost:8080/actuator/prometheus

## Development

### **Code style**
- Using Java 21 features
- Constructor injection for dependency injection
- Comprehensive error handling
- Structured logging

### **Architecture**
```
src/main/java/com/hivetech/kanban/
├── config/          # Spring configuration
├── controller/      # REST controllers
├── dto/            # Data Transfer Objects
├── interceptor/    # WebSocket interceptors
├── model/          # JPA entities
├── repository/     # Spring Data repositories
├── security/       # Security components
├── service/        # Business logic
├── util/          # Utility classes
└── validator/     # Custom validators
```

## License

This project was developed as a technical assessment task for the **HiveTech** interview process.

**Purpose**: Demonstration of Spring Boot development skills, including:
- RESTful API design and implementation
- Real-time WebSocket communication
- JWT authentication and security
- Integration testing with Testcontainers
- Modern Java development practices

**Status**: Interview assessment project  
**Rights**: All rights reserved by the candidate and HiveTech  
**Usage**: For evaluation and educational purposes only

---

**Author**: Antea Klapež 

**Version**: 0.0.1-SNAPSHOT  
**Spring Boot**: 3.5.0  
**Java**: 21