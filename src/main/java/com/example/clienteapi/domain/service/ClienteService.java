package com.example.clienteapi.domain.service;

import com.example.clienteapi.domain.model.Cliente;
import com.example.clienteapi.domain.port.in.ClienteServicePort;
import com.example.clienteapi.domain.port.out.ClienteRepositoryPort;
import com.example.clienteapi.domain.port.out.EmailServicePort; // Depende da interface
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ClienteService implements ClienteServicePort {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final EmailServicePort emailServicePort; // Injetada via interface

    public ClienteService(ClienteRepositoryPort clienteRepositoryPort, EmailServicePort emailServicePort) {
        this.clienteRepositoryPort = clienteRepositoryPort;
        this.emailServicePort = emailServicePort;
    }

    @Override
    public Cliente criarCliente(Cliente cliente) {
        if (clienteRepositoryPort.existsByEmail(cliente.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado.");
        }
        Cliente savedCliente = clienteRepositoryPort.save(cliente);
        emailServicePort.sendWelcomeEmail(savedCliente); // Chama a porta, sem saber quem a implementa
        return savedCliente;
    }

    @Override
    public Optional<Cliente> buscarClientePorId(Long id) {
        return clienteRepositoryPort.findById(id);
    }

    @Override
    public List<Cliente> buscarTodosClientes() {
        return clienteRepositoryPort.findAll();
    }

    @Override
    public Cliente atualizarCliente(Long id, Cliente cliente) {
        return clienteRepositoryPort.findById(id)
                .map(existingCliente -> {
                    existingCliente.setNome(cliente.getNome());
                    existingCliente.setEmail(cliente.getEmail());
                    existingCliente.setCpf(cliente.getCpf());
                    return clienteRepositoryPort.save(existingCliente);
                })
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com ID: " + id));
    }

    @Override
    public void deletarCliente(Long id) {
        clienteRepositoryPort.deleteById(id);
    }
}
