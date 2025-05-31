# API de Gerenciamento de Clientes (Arquitetura Hexagonal com Spring Boot e RabbitMQ)

[English 🌐](README.md)

Este projeto é uma API RESTful para gerenciamento de clientes, desenvolvida com **Spring Boot** e seguindo os princípios da **Arquitetura Hexagonal (Ports & Adapters)**. O objetivo principal é demonstrar um design de software desacoplado, testável e de fácil manutenção, utilizando comunicação assíncrona com **RabbitMQ** para o envio de e-mails de boas-vindas.

## 🚀 Funcionalidades

  * **CRUD de Clientes**: Cadastrar, Buscar, Atualizar e Deletar clientes.
  * **Validação de Dados**: Validação de entrada de dados para garantir a integridade.
  * **Comunicação Assíncrona**: Envio de e-mails de boas-vindas para novos clientes via fila de mensagens com RabbitMQ, garantindo resiliência e escalabilidade.
  * **Arquitetura Limpa**: Separação clara entre a lógica de negócio (domínio) e os detalhes de infraestrutura (adaptadores), promovendo alta coesão e baixo acoplamento.

## 📐 Arquitetura

A aplicação foi construída seguindo a **Arquitetura Hexagonal (Ports & Adapters)**, que consiste em:

  * **Domínio (Core)**: Contém a lógica de negócio principal (`Cliente`, `ClienteService`), independente de frameworks ou tecnologias externas. Ele interage com o mundo exterior apenas através de **Portas** (interfaces).
  * **Portas (Interfaces)**:
      * **Portas de Entrada (Driving Ports)**: Definem as operações que a aplicação oferece (ex: `ClienteServicePort`).
      * **Portas de Saída (Driven Ports)**: Definem as operações que a aplicação precisa de serviços externos (ex: `ClienteRepositoryPort`, `EmailServicePort`).
  * **Adaptadores (Implementações)**:
      * **Adaptadores de Entrada (Driving Adapters)**: Implementam as portas de entrada (ex: `ClienteController` para HTTP, `WelcomeEmailMessageListener` para RabbitMQ).
      * **Adaptadores de Saída (Driven Adapters)**: Implementam as portas de saída (ex: `ClienteJpaRepositoryAdapter` para persistência JPA, `RabbitMQEmailServiceAdapter` para envio de mensagens ao RabbitMQ).

Essa estrutura promove a testabilidade, flexibilidade para troca de tecnologias e clareza no design.

## 🛠️ Tecnologias Utilizadas

  * **Java 17+**
  * **Spring Boot 3.2.5+**
  * **Spring Data JPA**
  * **H2 Database** (para desenvolvimento e testes - facilmente substituível)
  * **Lombok**
  * **Spring AMQP** (para integração com RabbitMQ)
  * **RabbitMQ** (como message broker)
  * **Maven** (gerenciador de dependências)

## 📦 Como Rodar o Projeto

### Pré-requisitos

Certifique-se de ter instalado:

  * **JDK 17+**
  * **Maven 3.x**
  * **Docker** (recomendado para rodar o RabbitMQ e o H2, embora o H2 possa ser em memória)

### 1\. Clonar o Repositório

```bash
git clone https://github.com/andersonaoliveira/springboot-hexagonal-rabbitmq-customer-api.git
cd springboot-hexagonal-rabbitmq-customer-api
```

### 2\. Configurar o RabbitMQ com Docker

Certifique-se de que o Docker esteja rodando.
Abra um terminal e execute:

```bash
docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

A interface de gerenciamento do RabbitMQ estará disponível em `http://localhost:15672` (usuário: `guest`, senha: `guest`).

### 3\. Rodar a Aplicação Spring Boot

Navegue até o diretório raiz do projeto e execute:

```bash
mvn clean install
mvn spring-boot:run
```

A aplicação será iniciada na porta `8080`.

### 4\. Acessar o Console H2 (Opcional)

Durante o desenvolvimento, você pode acessar o console do H2 em:
`http://localhost:8080/h2-console`
Use as seguintes credenciais:

  * **JDBC URL:** `jdbc:h2:mem:clientedb`
  * **User Name:** `sa`
  * **Password:** (deixe em branco)

## 🧪 Testando a API

Você pode usar ferramentas como Postman, Insomnia ou `curl` para testar os endpoints.

### Criar um Novo Cliente

```bash
POST http://localhost:8080/clientes
Content-Type: application/json

{
    "nome": "Anderson Oliveira",
    "email": "anderson.oliveira@exemplo.com",
    "cpf": "12345678901"
}
```

  * **Resposta esperada:** `201 Created` com os dados do cliente e o ID gerado.
  * **Observação:** Verifique o console da aplicação para logs de envio e processamento do e-mail via RabbitMQ. Você também pode conferir a fila no console de gerenciamento do RabbitMQ.

### Buscar Todos os Clientes

```bash
GET http://localhost:8080/clientes
```

  * **Resposta esperada:** `200 OK` com uma lista de clientes.

### Buscar Cliente por ID

```bash
GET http://localhost:8080/clientes/{id_do_cliente}
```

  * **Exemplo:** `GET http://localhost:8080/clientes/1`
  * **Resposta esperada:** `200 OK` com os dados do cliente ou `404 Not Found`.

### Atualizar um Cliente

```bash
PUT http://localhost:8080/clientes/{id_do_cliente}
Content-Type: application/json

{
    "nome": "Anderson Alterado",
    "email": "anderson.alterado@exemplo.com",
    "cpf": "12345678901"
}
```

  * **Resposta esperada:** `200 OK` com os dados atualizados ou `404 Not Found`.

### Deletar um Cliente

```bash
DELETE http://localhost:8080/clientes/{id_do_cliente}
```

  * **Resposta esperada:** `204 No Content` ou `404 Not Found`.

## 🤝 Contribuição

Contribuições são bem-vindas\! Sinta-se à vontade para abrir issues ou pull requests no repositório.

## 📝 Licença

Este projeto está licenciado sob a [Licença MIT](https://www.google.com/search?q=https://github.com/andersonaoliveira/springboot-hexagonal-rabbitmq-customer-api/blob/main/LICENSE), conforme detalhado no arquivo `LICENSE`.

-----

## 👤 Sobre o Autor

Desenvolvido por **Anderson de Aguiar de Oliveira**  
[LinkedIn](https://www.linkedin.com/in/anderson-de-aguiar-de-oliveira) • [GitHub](https://github.com/andersonaoliveira)
