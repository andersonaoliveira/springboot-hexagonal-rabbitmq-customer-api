package com.example.clienteapi.domain.port.out;

import com.example.clienteapi.domain.model.Cliente;

public interface EmailServicePort {
    void sendWelcomeEmail(Cliente cliente);
}
