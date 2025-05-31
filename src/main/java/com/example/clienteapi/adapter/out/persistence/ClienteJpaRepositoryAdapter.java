package com.example.clienteapi.adapter.out.persistence;

import com.example.clienteapi.domain.model.Cliente;
import com.example.clienteapi.domain.port.out.ClienteRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ClienteJpaRepositoryAdapter implements ClienteRepositoryPort {

    private final ClienteJpaRepository clienteJpaRepository;

    public ClienteJpaRepositoryAdapter(ClienteJpaRepository clienteJpaRepository) {
        this.clienteJpaRepository = clienteJpaRepository;
    }

    @Override
    public Cliente save(Cliente cliente) {
        ClienteJpaEntity entity = toJpaEntity(cliente);
        ClienteJpaEntity savedEntity = clienteJpaRepository.save(entity);
        return toDomainModel(savedEntity);
    }

    @Override
    public Optional<Cliente> findById(Long id) {
        return clienteJpaRepository.findById(id)
                .map(this::toDomainModel);
    }

    @Override
    public List<Cliente> findAll() {
        return clienteJpaRepository.findAll().stream()
                .map(this::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        clienteJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return clienteJpaRepository.existsByEmail(email);
    }

    private ClienteJpaEntity toJpaEntity(Cliente cliente) {
        return new ClienteJpaEntity(cliente.getId(), cliente.getNome(), cliente.getEmail(), cliente.getCpf());
    }

    private Cliente toDomainModel(ClienteJpaEntity entity) {
        return new Cliente(entity.getId(), entity.getNome(), entity.getEmail(), entity.getCpf());
    }
}
