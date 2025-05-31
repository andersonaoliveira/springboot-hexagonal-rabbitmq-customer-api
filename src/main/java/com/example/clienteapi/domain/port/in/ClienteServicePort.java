package com.example.clienteapi.domain.port.in;

import com.example.clienteapi.domain.model.Cliente;
import java.util.List;
import java.util.Optional;

public interface ClienteServicePort {
    Cliente criarCliente(Cliente cliente);
    Optional<Cliente> buscarClientePorId(Long id);
    List<Cliente> buscarTodosClientes();
    Cliente atualizarCliente(Long id, Cliente cliente);
    void deletarCliente(Long id);
}
