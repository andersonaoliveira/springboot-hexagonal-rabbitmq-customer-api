package com.example.clienteapi.domain.port.out;

import com.example.clienteapi.domain.model.Cliente;
import java.util.Optional;
import java.util.List;

public interface ClienteRepositoryPort {
    Cliente save(Cliente cliente);
    Optional<Cliente> findById(Long id);
    List<Cliente> findAll();
    void deleteById(Long id);
    boolean existsByEmail(String email);
}
