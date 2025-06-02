package com.example.clienteapi.adapter.in.web;

import com.example.clienteapi.domain.model.Cliente;
import com.example.clienteapi.domain.port.in.ClienteServicePort;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteServicePort clienteServicePort;

    public ClienteController(ClienteServicePort clienteServicePort) {
        this.clienteServicePort = clienteServicePort;
    }

    @Operation(summary = "Cria um novo cliente", description = "Cadastra um novo cliente no sistema e dispara um evento para envio de e-mail de boas-vindas.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cliente criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos (ex: e-mail ou CPF em formato incorreto ou já existente)")
    })
    @PostMapping
    public ResponseEntity<ClienteResponse> criarCliente(@Valid @RequestBody ClienteRequest request) {
        Cliente cliente = new Cliente(null, request.getNome(), request.getEmail(), request.getCpf());
        Cliente novoCliente = clienteServicePort.criarCliente(cliente);
        return new ResponseEntity<>(toResponse(novoCliente), HttpStatus.CREATED);
    }

    @Operation(summary = "Pesquisa o cliente por um ID", description = "Pesquisa informações do cliente utilizando um ID único.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente localizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Não foi possível localizar um cliente com este ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> buscarClientePorId(@PathVariable Long id) {
        return clienteServicePort.buscarClientePorId(id)
                .map(cliente -> new ResponseEntity<>(toResponse(cliente), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Pesquisa todos clientes", description = "Pesquisa informações de todos os clientes cadastrados.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista os clientes localizados ou informa '[]' caso não tenha cliente cadastrado.")
    })
    @GetMapping
    public ResponseEntity<List<ClienteResponse>> buscarTodosClientes() {
        List<ClienteResponse> clientes = clienteServicePort.buscarTodosClientes().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(clientes, HttpStatus.OK);
    }

    @Operation(summary = "Atualiza um cliente existente", description = "Atualiza os dados de um cliente com base no seu ID. Requer privilégios de ADMIN.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
        @ApiResponse(responseCode = "403", description = "Acesso negado. Requer papel de ADMIN."),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado com o ID fornecido")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteResponse> atualizarCliente(@PathVariable Long id, @Valid @RequestBody ClienteRequest request) {
        Cliente cliente = new Cliente(id, request.getNome(), request.getEmail(), request.getCpf());
        try {
            Cliente updatedCliente = clienteServicePort.atualizarCliente(id, cliente);
            return new ResponseEntity<>(toResponse(updatedCliente), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Deleta um cliente existente", description = "Remove um cliente do sistema com base no seu ID. Requer privilégios de ADMIN.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cliente deletado com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado. Requer papel de ADMIN."),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado com o ID fornecido")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
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
