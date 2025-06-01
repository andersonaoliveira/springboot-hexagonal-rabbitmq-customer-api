# API de Gerenciamento de Clientes (Arquitetura Hexagonal com Spring Boot e RabbitMQ)

[English 🌐](README.md)

Este projeto é uma API RESTful para gerenciamento de clientes, desenvolvida com **Spring Boot** e seguindo os princípios da **Arquitetura Hexagonal (Ports & Adapters)**. O objetivo principal é demonstrar um design de software desacoplado, testável e de fácil manutenção, utilizando comunicação assíncrona com **RabbitMQ** para o envio de e-mails de boas-vindas.

## 🚀 Funcionalidades

  * **CRUD de Clientes**: Cadastrar, Buscar, Atualizar e Deletar clientes.
  * **Segurança com JWT**: Endpoints protegidos com autenticação e autorização baseadas em JSON Web Tokens, garantindo que apenas usuários autorizados possam acessar os recursos.
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
  * **Spring Security** (para autenticação e autorização)
  * **JJWT (Java JWT)** (para criação e validação de tokens JWT)
  * **Spring Data JPA**
  * **H2 Database** (para desenvolvimento e testes - facilmente substituível)
  * **Lombok**
  * **Spring AMQP** (para integração com RabbitMQ)
  * **RabbitMQ** (como message broker)
  * **Maven** (gerenciador de dependências)

---

## 🧪 Estratégia de Testes

Este projeto adota uma estratégia de testes em camadas, alavancando os benefícios da Arquitetura Hexagonal para garantir a qualidade do código e a confiabilidade das funcionalidades.

---

### Tipos de Testes Implementados:

* **Testes Unitários:**
    * **Foco:** Lógica de negócio pura (Domínio).
    * **Exemplo:** `ClienteServiceTest`.
    * **Verifica:** Regras de negócio, validações e interações com as Portas (mocks).
    * **Ferramentas:** JUnit 5, Mockito.

* **Testes de Integração de Persistência:**
    * **Foco:** Interação entre a camada de domínio e a persistência de dados.
    * **Exemplo:** `ClienteJpaRepositoryAdapterTest`.
    * **Verifica:** Mapeamento ORM e operações de banco de dados (utilizando H2 Database em memória).
    * **Ferramentas:** JUnit 5, Spring Boot `@DataJpaTest`.

* **Testes de Integração de Mensageria (Mock):**
    * **Foco:** Interação do adaptador de saída (producer) com o cliente de mensageria, de forma isolada e sem a necessidade de um broker real.
    * **Exemplo:** `RabbitMQEmailServiceAdapterMockTest.java`.
    * **Verifica:** A correta invocação do `RabbitTemplate`, garantindo que a exchange, a routing key e o payload (serializado em JSON) são enviados como esperado.
    * **Ferramentas:** JUnit 5, Spring Boot `@SpringBootTest`, `@MockBean`, Mockito.

* **Testes de Ponta a Ponta (E2E):**
    * **Foco:** Fluxo completo da aplicação, simulando a jornada do usuário, incluindo a API HTTP, persistência em banco de dados e comunicação assíncrona.
    * **Exemplo:** `ClienteE2ETest`.
    * **Verifica:** Requisições HTTP, lógica de negócio, salvamento no DB e o envio/processamento de mensagens via RabbitMQ, do ponto de vista do fluxo completo.
    * **Ferramentas:** JUnit 5, Spring Boot `@SpringBootTest`, Testcontainers (para RabbitMQ e banco de dados H2 dedicado), `WebTestClient`, Awaitility (para asserções assíncronas).
    
    * **Observação sobre o Teste E2E do Consumidor:**
    Para verificar o processamento de mensagens assíncronas pelo `WelcomeEmailMessageListener` em testes E2E, uma flag `public static AtomicBoolean messageProcessedForE2E` foi temporariamente adicionada ao código do listener de produção. **É importante notar que esta é uma técnica didática para simplificar a sincronização do teste.** Em um ambiente de produção real, o código do listener não deve conter lógica específica de teste. A verificação do processamento do consumidor idealmente seria feita através de efeitos colaterais persistentes (ex: status no banco de dados) ou com ferramentas de teste mais avançadas que substituem o serviço de e-mail por um mock que registra as chamadas.

### Como Executar os Testes:

Certifique-se de ter o **Docker em execução** para que o Testcontainers possa iniciar o contêiner do RabbitMQ.

Na raiz do projeto, execute o seguinte comando Maven:

```bash
mvn clean install # Compila o projeto e suas classes de teste
mvn test          # Executa todos os testes (unitários, integração e E2E)
```

---

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

Com a implementação de segurança, a maioria dos endpoints agora está protegida. Para testá-los, você primeiro precisa obter um token de autenticação e, em seguida, usá-lo nas requisições subsequentes.

Você pode usar ferramentas como Postman, Insomnia ou `curl` para testar os endpoints.

### 1. Obter um Token de Autenticação

Envie uma requisição `POST` para o endpoint `/login` com as credenciais do usuário de demonstração.

```bash
POST http://localhost:8080/login
Content-Type: application/json

{
    "username": "admin",
    "password": "password"
}
```
* **Resposta esperada:** `200 OK` com um corpo contendo o token JWT.
  ```json
  {
      "token": "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJDbGllbnRlIEFQSSIsInN1YiI6ImFkbWluIiwiaWF0IjoxNzE3MjgyODAwLCJleHAiOjE3MTcyODY0MDB9.xxxxxxxxxxxx",
      "type": "Bearer"
  }
  ```
* **Próximo Passo:** Copie o valor do campo `"token"` para usar nos próximos testes.

### 2. Criar um Novo Cliente (Endpoint Público)

Este endpoint continua público e não requer autenticação.

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

### 3. Buscar Cliente e Buscar Cliente por ID (Endpoint Público)

Estes endpoints continuam públicos e não requerem autenticação.

```bash
GET http://localhost:8080/clientes
```
```bash
GET http://localhost:8080/clientes/{id_do_cliente}
```

* **Exemplo:** `GET http://localhost:8080/clientes/1`
* **Resposta esperada:** `200 OK` com os dados do cliente.

### 4. Atualizar um Cliente (Endpoint Protegido)

```bash
PUT http://localhost:8080/clientes/{id_do_cliente}
Content-Type: application/json
Authorization: Bearer {SEU_TOKEN_JWT}

{
    "nome": "Anderson Alterado",
    "email": "anderson.alterado@exemplo.com",
    "cpf": "12345678901"
}
```

* **Resposta esperada:** `200 OK` com os dados atualizados ou `404 Not Found`.

### 5. Deletar um Cliente (Endpoint Protegido)

```bash
DELETE http://localhost:8080/clientes/{id_do_cliente}
Authorization: Bearer {SEU_TOKEN_JWT}
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
