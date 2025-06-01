package com.example.clienteapi.e2e;

import com.example.clienteapi.ClienteapiApplication;
import com.example.clienteapi.adapter.in.web.ClienteRequest;
import com.example.clienteapi.adapter.out.persistence.ClienteJpaRepository;
import com.example.clienteapi.adapter.in.messagequeue.WelcomeEmailMessageListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = ClienteapiApplication.class
)
@Testcontainers
@DisplayName("Testes de Ponta a Ponta para a API de Clientes (com H2)")
class ClienteE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ClienteJpaRepository clienteJpaRepository;

    @Container
    public static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer(DockerImageName.parse("rabbitmq:3-management"))
            .withExposedPorts(5672, 15672);

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitMQContainer::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMQContainer::getAdminPassword);

        registry.add("spring.datasource.url", () -> "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "");


        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.H2Dialect");

        registry.add("spring.rabbitmq.listener.simple.auto-startup", () -> "true");
    }

    @BeforeEach
    void setUp() {
        clienteJpaRepository.deleteAll();
        WelcomeEmailMessageListener.messageProcessedForE2E.set(false);
    }


    @Test
    @DisplayName("Deve criar um cliente, salvá-lo no DB (H2) e processar o email via RabbitMQ E2E")
    void deveCriarClienteESalvarEProcessarEmailE2E() {
        ClienteRequest request = new ClienteRequest();
        request.setNome("Cliente E2E H2");
        request.setEmail("e2e.h2.test@example.com");
        request.setCpf("10120230344");

        webTestClient.post().uri("/clientes")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.email").isEqualTo("e2e.h2.test@example.com");


        await().atMost(Duration.ofSeconds(5))
                .until(() -> clienteJpaRepository.findByEmail("e2e.h2.test@example.com").isPresent());

        assertThat(clienteJpaRepository.findByEmail("e2e.h2.test@example.com")).isPresent();

        await().atMost(Duration.ofSeconds(10))
                .untilTrue(WelcomeEmailMessageListener.messageProcessedForE2E);
    }
}