package com.example.clienteapi.adapter.in.web;

import com.example.clienteapi.domain.model.Cliente;
import com.example.clienteapi.domain.port.in.ClienteServicePort;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteServicePort clienteServicePort;

    public ClienteController(ClienteServicePort clienteServicePort) {
        this.clienteServicePort = clienteServicePort;
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> criarCliente(@Valid @RequestBody ClienteRequest request) {
        Cliente cliente = new Cliente(null, request.getNome(), request.getEmail(), request.getCpf());
        Cliente novoCliente = clienteServicePort.criarCliente(cliente);
        return new ResponseEntity<>(toResponse(novoCliente), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> buscarClientePorId(@PathVariable Long id) {
        return clienteServicePort.buscarClientePorId(id)
                .map(cliente -> new ResponseEntity<>(toResponse(cliente), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponse>> buscarTodosClientes() {
        List<ClienteResponse> clientes = clienteServicePort.buscarTodosClientes().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(clientes, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> atualizarCliente(@PathVariable Long id, @Valid @RequestBody ClienteRequest request) {
        Cliente cliente = new Cliente(id, request.getNome(), request.getEmail(), request.getCpf());
        try {
            Cliente updatedCliente = clienteServicePort.atualizarCliente(id, cliente);
            return new ResponseEntity<>(toResponse(updatedCliente), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        try {
            clienteServicePort.deletarCliente(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(cliente.getId(), cliente.getNome(), cliente.getEmail(), cliente.getCpf());
    }
}
