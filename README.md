# Customer Management API (Hexagonal Architecture with Spring Boot and RabbitMQ)

[Português Brasileiro 🌐](README.pt-br.md)

This project is a RESTful API for customer management, developed with **Spring Boot** and following the principles of **Hexagonal Architecture (Ports & Adapters)**. The main goal is to demonstrate a decoupled, testable, and easily maintainable software design, utilizing asynchronous communication with **RabbitMQ** for sending welcome emails.

## 🚀 Features

* **Customer CRUD**: Create, Retrieve, Update, and Delete customer records.
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
* **Spring Data JPA**
* **H2 Database** (for development and testing - easily replaceable)
* **Lombok**
* **Spring AMQP** (for RabbitMQ integration)
* **RabbitMQ** (as a message broker)
* **Maven** (dependency manager)

## 📦 How to Run the Project

### Prerequisites

Make sure you have the following installed:

* **JDK 17+**
* **Maven 3.x**
* **Docker** (recommended for running RabbitMQ and H2, although H2 can run in-memory)

### 1. Clone the Repository

```bash
git clone [https://github.com/andersonaoliveira/springboot-hexagonal-rabbitmq-customer-api.git](https://github.com/andersonaoliveira/springboot-hexagonal-rabbitmq-customer-api.git)
cd springboot-hexagonal-rabbitmq-customer-api
````

### 2\. Configure RabbitMQ with Docker

Ensure Docker is running.
Open a terminal and execute:

```bash
docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

The RabbitMQ management interface will be available at `http://localhost:15672` (username: `guest`, password: `guest`).

### 3\. Run the Spring Boot Application

Navigate to the project's root directory and execute:

```bash
mvn clean install
mvn spring-boot:run
```

The application will start on port `8080`.

### 4\. Access H2 Console (Optional)

During development, you can access the H2 console at:
`http://localhost:8080/h2-console`
Use the following credentials:

  * **JDBC URL:** `jdbc:h2:mem:clientedb`
  * **User Name:** `sa`
  * **Password:** (leave blank)

## 🧪 Testing the API

You can use tools like Postman, Insomnia, or `curl` to test the endpoints.

### Create a New Customer

```bash
POST http://localhost:8080/clientes
Content-Type: application/json

{
    "nome": "Anderson Oliveira",
    "email": "anderson.oliveira@example.com",
    "cpf": "12345678901"
}
```

  * **Expected Response:** `201 Created` with customer data and generated ID.
  * **Note:** Check your application console for logs indicating email sending and processing via RabbitMQ. You can also verify the queue in the RabbitMQ management console.

### Get All Customers

```bash
GET http://localhost:8080/clientes
```

  * **Expected Response:** `200 OK` with a list of customers.

### Get Customer by ID

```bash
GET http://localhost:8080/clientes/{customer_id}
```

  * **Example:** `GET http://localhost:8080/clientes/1`
  * **Expected Response:** `200 OK` with customer data or `404 Not Found`.

### Update a Customer

```bash
PUT http://localhost:8080/clientes/{customer_id}
Content-Type: application/json

{
    "nome": "Anderson Updated",
    "email": "anderson.updated@example.com",
    "cpf": "12345678901"
}
```

  * **Expected Response:** `200 OK` with updated data or `404 Not Found`.

### Delete a Customer

```bash
DELETE http://localhost:8080/clientes/{customer_id}
```

  * **Expected Response:** `204 No Content` or `404 Not Found`.

## 🤝 Contribution

Contributions are welcome\! Feel free to open issues or pull requests in the repository.

## 📝 License

This project is licensed under the [MIT License](https://www.google.com/search?q=https://github.com/andersonaoliveira/springboot-hexagonal-rabbitmq-customer-api/blob/main/LICENSE), as detailed in the `LICENSE` file.

-----

## 👤 About the Author

Developed by  **Anderson de Aguiar de Oliveira**  
[LinkedIn](https://www.linkedin.com/in/anderson-de-aguiar-de-oliveira) • [GitHub](https://github.com/andersonaoliveira)

