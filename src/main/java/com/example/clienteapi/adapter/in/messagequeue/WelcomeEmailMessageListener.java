package com.example.clienteapi.adapter.in.messagequeue;

import com.example.clienteapi.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
public class WelcomeEmailMessageListener {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public static AtomicBoolean messageProcessedForE2E = new AtomicBoolean(false);


    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveMessage(String message) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            String email = jsonNode.get("email").asText();
            String nome = jsonNode.get("nome").asText();

            log.info("Mensagem recebida da fila RabbitMQ para processar e-mail de boas-vindas:");
            log.info("Para: {}", email);
            log.info("Nome: {}", nome);
            log.info("Processando envio de e-mail (simulado, pode demorar)...");
            Thread.sleep(3000);
            log.info("E-mail de boas-vindas enviado com sucesso para {}", email);

            messageProcessedForE2E.set(true);

        } catch (Exception e) {
            log.error("Erro ao processar mensagem da fila: {}", e.getMessage(), e);
        }
    }
}
