# Health Store - Backend

This is the backend service for the E-commerce Multi-Vendor Health Store application, built with Spring Boot.

## Prerequisites

- Java 17 or higher
- Maven 3.6.3 or higher
- MySQL 8.0 or higher
- Docker (optional, for containerization)

## Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd Ecommerce-Multi-Vendor-Health-Store/backend-spring-boot
   ```

2. **Configure the database**
   - Create a MySQL database named `health_store`
   - Update the database configuration in `src/main/resources/application.properties`
     ```properties
     spring.datasource.url=jdbc:mysql://localhost:3306/health_store
     spring.datasource.username=your_username
     spring.datasource.password=your_password
     ```

3. **Build the application**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```
   The application will be available at `http://localhost:5454`

## API Documentation

Once the application is running, you can access the following:

- **Swagger UI**: `http://localhost:5454/swagger-ui.html`
- **H2 Console** (if enabled): `http://localhost:5454/h2-console`

## Running with Docker

1. **Build the Docker image**
   ```bash
   docker build -t health-store-backend .
   ```

2. **Run the container**
   ```bash
   docker run -p 5454:5454 health-store-backend
   ```

## Project Structure

```
src/main/java/com/healthstore/
├── config/           # Configuration classes
├── controller/       # REST controllers
├── dto/              # Data Transfer Objects
├── exception/        # Exception handling
├── model/            # Entity classes
├── repository/       # JPA repositories
├── security/         # Security configuration
├── service/          # Business logic
└── util/             # Utility classes
```

## Features

- User authentication and authorization with JWT
- Role-based access control (USER, SELLER, ADMIN)
- RESTful API endpoints
- MySQL database integration
- Input validation
- Exception handling
- Email service
- Payment integration (Stripe)
- Containerization with Docker

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
