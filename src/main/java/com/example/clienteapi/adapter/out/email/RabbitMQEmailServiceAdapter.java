package com.example.clienteapi.adapter.out.email;

import com.example.clienteapi.config.RabbitMQConfig;
import com.example.clienteapi.domain.model.Cliente;
import com.example.clienteapi.domain.port.out.EmailServicePort;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Slf4j
public class RabbitMQEmailServiceAdapter implements EmailServicePort {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public RabbitMQEmailServiceAdapter(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void sendWelcomeEmail(Cliente cliente) {
        log.info("Enviando mensagem para a fila RabbitMQ para cliente: {}", cliente.getEmail());
        try {
            String clienteJson = objectMapper.writeValueAsString(cliente);
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                clienteJson
            );
            log.info("Mensagem de boas-vindas enviada com sucesso para RabbitMQ para {}", cliente.getEmail());
        } catch (Exception e) {
            log.error("Erro ao serializar ou enviar cliente para a fila: {}", e.getMessage(), e);
        }
    }
}
