package com.example.clienteapi.adapter.in.messagequeue;

import com.example.clienteapi.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

@Component
@Slf4j
public class WelcomeEmailMessageListener {

    private final ObjectMapper objectMapper = new ObjectMapper();

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
            // Simulação de trabalho real (ex: chamar AWS SES SDK)
            Thread.sleep(3000); // Simula um processamento demorado
            log.info("E-mail de boas-vindas enviado com sucesso para {}", email);

        } catch (Exception e) {
            log.error("Erro ao processar mensagem da fila: {}", e.getMessage(), e);
            // Em um cenário real, você poderia rejogar a exceção para que a mensagem seja reenviada
            // ou movida para uma DLQ (Dead Letter Queue) para reprocessamento.
        }
    }
}
