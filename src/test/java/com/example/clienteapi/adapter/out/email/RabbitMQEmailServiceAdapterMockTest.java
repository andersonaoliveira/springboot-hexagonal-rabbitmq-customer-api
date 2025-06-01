package com.example.clienteapi.adapter.out.email;

import com.example.clienteapi.config.RabbitMQConfig;
import com.example.clienteapi.domain.model.Cliente;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {RabbitMQEmailServiceAdapter.class, ObjectMapper.class})
@DisplayName("Teste de Unidade/Integração para RabbitMQEmailServiceAdapter (com Mock)")
class RabbitMQEmailServiceAdapterMockTest {

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMQEmailServiceAdapter emailServiceAdapter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve chamar o método 'convertAndSend' do RabbitTemplate com os parâmetros corretos")
    void deveChamarConvertAndSendCorretamente() throws Exception {

        Cliente clienteParaEnvio = new Cliente(1L, "Cliente Mock Teste", "mock.teste@example.com", "55544433322");

        String payloadJsonEsperado = objectMapper.writeValueAsString(clienteParaEnvio);

        emailServiceAdapter.sendWelcomeEmail(clienteParaEnvio);

        ArgumentCaptor<String> payloadArgumentCaptor = ArgumentCaptor.forClass(String.class);

        verify(rabbitTemplate, times(1)).convertAndSend(
            eq(RabbitMQConfig.EXCHANGE_NAME),
            eq(RabbitMQConfig.ROUTING_KEY),
            payloadArgumentCaptor.capture()
        );

        assertThat(payloadArgumentCaptor.getValue()).isEqualTo(payloadJsonEsperado);
    }
}