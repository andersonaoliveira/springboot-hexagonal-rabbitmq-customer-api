# Customer Management API (Hexagonal Architecture with Spring Boot and RabbitMQ)

[Português Brasileiro 🌐](README.pt-br.md)

This project is a RESTful API for customer management, developed with **Spring Boot** and following the principles of **Hexagonal Architecture (Ports & Adapters)**. The main goal is to demonstrate a decoupled, testable, and easily maintainable software design, utilizing asynchronous communication with **RabbitMQ** for sending welcome emails.

## 🚀 Features

* **Customer CRUD**: Create, Retrieve, Update, and Delete customer records.
* **JWT Security**: Endpoints are secured with JSON Web Token-based authentication and authorization, ensuring that only authorized users can access resources.
* **Interactive API Documentation**: Via Swagger UI (OpenAPI 3), allowing easy visualization and testing of endpoints.
* **Data Validation**: Input data validation to ensure data integrity.
* **Asynchronous Communication**: Sending welcome emails to new customers via a message queue with RabbitMQ, ensuring resilience and scalability.
* **Clean Architecture**: Clear separation between business logic (domain) and infrastructure details (adapters), promoting high cohesion and low coupling.

## 📐 Architecture

The application is built following the **Hexagonal Architecture (Ports & Adapters)**, which consists of:

* **Domain (Core)**: Contains the main business logic (`Cliente`, `ClienteService`), independent of frameworks or external technologies. It interacts with the outside world only through **Ports** (interfaces).
* **Ports (Interfaces)**:
    * **Driving Ports (Inbound Ports)**: Define the operations the application offers (e.g., `ClienteServicePort`).
    * **Driven Ports (Outbound Ports)**: Define the operations the application needs from external services (e.g., `ClienteRepositoryPort`, `EmailServicePort`).
* **Adapters (Implementations)**:
    * **Driving Adapters (Inbound Adapters)**: Implement the driving ports (e.g., `ClienteController` for HTTP, `WelcomeEmailMessageListener` for RabbitMQ).
    * **Driven Adapters (Outbound Adapters)**: Implement the driven ports (e.g., `ClienteJpaRepositoryAdapter` for JPA persistence, `RabbitMQEmailServiceAdapter` for sending messages to RabbitMQ).

This structure promotes testability, flexibility for technology changes, and clarity in design.

## 🛠️ Technologies Used

* **Java 17+**
* **Spring Boot 3.2.5+**
* **Spring Security** (for authentication and authorization)
* **JJWT (Java JWT)** (for creating and validating JWT tokens)
* **springdoc-openapi** (for API documentation generation with Swagger UI)
* **Spring Data JPA**
* **PostgreSQL** (main application database)
* **H2 Database** (for development and testing - easily replaceable)
* **Lombok**
* **Spring AMQP** (for RabbitMQ integration)
* **RabbitMQ** (as a message broker)
* **Docker** (for containerizing the application and services)
* **Maven** (dependency manager)

---

## 🧪 Testing Strategy

This project adopts a layered testing strategy, leveraging the benefits of Hexagonal Architecture to ensure code quality and functionality reliability.

---

### Implemented Test Types:

* **Unit Tests:**
    * **Focus:** Pure business logic (Domain).
    * **Example:** `ClienteServiceTest`.
    * **Verifies:** Business rules, validations, and interactions with Ports (mocks).
    * **Tools:** JUnit 5, Mockito.

* **Persistence Integration Tests:**
    * **Focus:** Interaction between the domain layer and data persistence.
    * **Example:** `ClienteJpaRepositoryAdapterTest`.
    * **Verifies:** ORM mapping and database operations (using in-memory H2 Database).
    * **Tools:** JUnit 5, Spring Boot `@DataJpaTest`.

* **Messaging Integration Tests (Mock):**
    * **Focus:** Interaction between the outbound adapter (producer) and the messaging client in isolation, without requiring a real message broker.
    * **Example:** `RabbitMQEmailServiceAdapterMockTest.java`.
    * **Verifies:** The correct invocation of the `RabbitTemplate`, ensuring that the expected exchange, routing key, and properly serialized JSON payload are sent.
    * **Tools:** JUnit 5, Spring Boot `@SpringBootTest`, `@MockBean`, Mockito.

* **End-to-End (E2E) Tests:**
    * **Focus:** Full application flow, simulating the user journey, including API HTTP, database persistence, and asynchronous communication.
    * **Example:** `ClienteE2ETest`.
    * **Verifies:** HTTP requests, business logic, DB saving, and message sending/processing via RabbitMQ, from a complete flow perspective.
    * **Tools:** JUnit 5, Spring Boot `@SpringBootTest`, Testcontainers (for RabbitMQ and dedicated H2 database), `WebTestClient`, Awaitility (for asynchronous assertions).

    * **Note on Consumer E2E Test:**
    To verify asynchronous message processing by `WelcomeEmailMessageListener` in E2E tests, a `public static AtomicBoolean messageProcessedForE2E` flag was temporarily added to the production listener's code. **It is important to note that this is a didactic technique to simplify test synchronization.** In a real production environment, the listener's code should not contain test-specific logic. Consumer processing verification would ideally be done through persistent side effects (e.g., database status) or with more advanced testing tools that replace the email service with a mock that logs calls.

### How to Run Tests:

Ensure **Docker is running** for Testcontainers to start the RabbitMQ container.

From the project root, execute the following Maven command:

```bash
mvn clean install # Compiles the project and its test classes
mvn test          # Executes all tests (unit, integration, and E2E)
```

---

## 📦 How to Run the Project

### Prerequisites

Make sure you have the following installed:

* **JDK 17+**
* **Maven 3.x**
* **Docker** (essential for running PostgreSQL and RabbitMQ)

### 1. Clone the Repository

```bash
git clone [https://github.com/andersonaoliveira/springboot-hexagonal-rabbitmq-customer-api.git](https://github.com/andersonaoliveira/springboot-hexagonal-rabbitmq-customer-api.git)
cd springboot-hexagonal-rabbitmq-customer-api
````

### 2. Configure External Services with Docker

Ensure Docker is running.

  **a. RabbitMQ**
  Open a terminal and execute:
  ```bash
  docker run -d --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management-alpine
  ```
  The RabbitMQ management interface will be available at `http://localhost:15672`.

  **b. PostgreSQL**
  In another terminal, execute:
  ```bash
  docker run -d --rm --name postgres-db -e POSTGRES_DB=clientedb -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 postgres:15-alpine
  ```
  This will start a PostgreSQL container with the `clientedb` database and `postgres/postgres` credentials.

### 3\. Run the Spring Boot Application

Navigate to the project's root directory and execute:

```bash
mvn clean install
mvn spring-boot:run
```

The application will start on port `8080` and connect to the PostgreSQL and RabbitMQ containers.

### 4. Access H2 Console (For Tests)

H2 is now primarily used for persistence layer integration tests (`@DataJpaTest`). If you run the application with the test profile or execute these specific tests, an in-memory H2 database will be used. The H2 console for the main application database (`clientedb`) is no longer relevant as the main application uses PostgreSQL.

## 🧪 Testing the API

You can explore and test the API interactively using **Swagger UI**, available at:
`http://localhost:8080/swagger-ui/index.html`

Alternatively, with the security implementation, most endpoints are now secured. To test them with tools like Postman, Insomnia, or `curl`, you must first obtain an authentication token.

### 1. Obtain an Authentication Token

Send a `POST` request to the `/login` endpoint with the demo user's credentials.

```bash
POST http://localhost:8080/login
Content-Type: application/json

{
    "username": "admin",
    "password": "password"
}
```

* **Expected Response:** `200 OK` with a body containing the JWT.
    ```json
    {
        "token": "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJDbGllbnRlIEFQSSIsInN1YiI6ImFkbWluIiwiaWF0IjoxNzE3MjgyODAwLCJleHAiOjE3MTcyODY0MDB9.xxxxxxxxxxxx",
        "type": "Bearer"
    }
    ```
* **Next Step:** Copy the value from the `"token"` field to use in the next tests.

### 2. Create a New Customer (Public Endpoint)

This endpoint remains public and does not require authentication.

```bash
POST http://localhost:8080/clientes
Content-Type: application/json

{
    "nome": "Anderson Oliveira",
    "email": "anderson.oliveira@example.com",
    "cpf": "12345678901"
}
```

* **Expected Response:** `201 Created` with the customer data and the generated ID.

### 3. Get Customers and Get Customer by ID (Public Endpoint)

These endpoints remain public and do not require authentication.

```bash
GET http://localhost:8080/clientes
```
```bash
GET http://localhost:8080/clientes/{customer_id}
```

* **Example:** `GET http://localhost:8080/clientes/1`
* **Expected Response:** `200 OK` with the customer data.

### 4. Update a Customer (Secured Endpoint)

This endpoint is also secured and requires the token.

```bash
PUT http://localhost:8080/clientes/{customer_id}
Content-Type: application/json
Authorization: Bearer {YOUR_JWT}

{
    "nome": "Anderson Updated",
    "email": "anderson.updated@example.com",
    "cpf": "12345678901"
}
```

* **Expected Response:** `200 OK` with the updated data or `404 Not Found`.

### 5. Delete a Customer (Secured Endpoint)

This endpoint is secured and requires the token.

```bash
DELETE http://localhost:8080/clientes/{customer_id}
Authorization: Bearer {YOUR_JWT}
```

* **Expected Response:** `204 No Content` or `404 Not Found`.

## 🐳 Containerization with Docker

This project is set up to be easily containerized using Docker.

### Dockerfile

A multi-stage `Dockerfile` is included in the project root. It is responsible for:
1.  Compiling the Java application using a Maven image.
2.  Creating a lean final image containing only the JRE and the application JAR.

To build the API's Docker image, navigate to the project root and run:
```bash
docker build -t your-username/clienteapi .
```
(Replace `your-username/clienteapi` with your desired image name).

### Docker Compose (Next Step)

To orchestrate the API along with its dependencies (PostgreSQL and RabbitMQ) in a simplified manner, the next step in this project's evolution will be the implementation of a `docker-compose.yml` file. This will allow starting the entire environment with a single command.

## 🤝 Contribution

Contributions are welcome\! Feel free to open issues or pull requests in the repository.

## 📝 License

This project is licensed under the [MIT License](https://www.google.com/search?q=https://github.com/andersonaoliveira/springboot-hexagonal-rabbitmq-customer-api/blob/main/LICENSE), as detailed in the `LICENSE` file.

-----

## 👤 About the Author

Developed by  **Anderson de Aguiar de Oliveira**  
[LinkedIn](https://www.linkedin.com/in/anderson-de-aguiar-de-oliveira) • [GitHub](https://github.com/andersonaoliveira)

