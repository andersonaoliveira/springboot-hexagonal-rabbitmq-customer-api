package com.example.clienteapi.domain.service;

import com.example.clienteapi.domain.model.Cliente;
import com.example.clienteapi.domain.port.out.ClienteRepositoryPort;
import com.example.clienteapi.domain.port.out.EmailServicePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários para ClienteService")
class ClienteServiceTest {

    @Mock
    private ClienteRepositoryPort clienteRepositoryPort;

    @Mock
    private EmailServicePort emailServicePort;

    @InjectMocks
    private ClienteService clienteService;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Deve criar um cliente com sucesso quando o email não existe")
    void deveCriarClienteComSucessoQuandoEmailNaoExiste() {
        Cliente novoCliente = new Cliente(null, "Teste Unitario", "teste@example.com", "12345678901");
        Cliente clienteSalvo = new Cliente(1L, "Teste Unitario", "teste@example.com", "12345678901");

        when(clienteRepositoryPort.existsByEmail(anyString())).thenReturn(false);
        when(clienteRepositoryPort.save(any(Cliente.class))).thenReturn(clienteSalvo);

        Cliente resultado = clienteService.criarCliente(novoCliente);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getEmail()).isEqualTo("teste@example.com");

        verify(clienteRepositoryPort, times(1)).existsByEmail("teste@example.com");
        verify(clienteRepositoryPort, times(1)).save(novoCliente);
        verify(emailServicePort, times(1)).sendWelcomeEmail(clienteSalvo);
        verifyNoMoreInteractions(clienteRepositoryPort, emailServicePort);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar cliente com email já existente")
    void deveLancarExcecaoQuandoEmailJaExiste() {
        Cliente clienteExistente = new Cliente(null, "Existente", "existente@example.com", "11122233344");

        when(clienteRepositoryPort.existsByEmail(anyString())).thenReturn(true);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            clienteService.criarCliente(clienteExistente);
        });
        assertThat(thrown.getMessage()).isEqualTo("Email já cadastrado.");

        verify(clienteRepositoryPort, times(1)).existsByEmail("existente@example.com");
        verify(clienteRepositoryPort, never()).save(any(Cliente.class));
        verify(emailServicePort, never()).sendWelcomeEmail(any(Cliente.class));
        verifyNoMoreInteractions(clienteRepositoryPort, emailServicePort);
    }

    @Test
    @DisplayName("Deve buscar cliente por ID com sucesso")
    void deveBuscarClientePorIdComSucesso() {
        Long clienteId = 1L;
        Cliente clienteEncontrado = new Cliente(clienteId, "Buscar Teste", "buscar@example.com", "44455566677");
        when(clienteRepositoryPort.findById(clienteId)).thenReturn(Optional.of(clienteEncontrado));

        Optional<Cliente> resultado = clienteService.buscarClientePorId(clienteId);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getEmail()).isEqualTo("buscar@example.com");
        verify(clienteRepositoryPort, times(1)).findById(clienteId);
        verifyNoMoreInteractions(clienteRepositoryPort);
    }

    @Test
    @DisplayName("Não deve encontrar cliente por ID inexistente")
    void naoDeveEncontrarClientePorIdInexistente() {
        Long clienteId = 99L;
        when(clienteRepositoryPort.findById(clienteId)).thenReturn(Optional.empty());

        Optional<Cliente> resultado = clienteService.buscarClientePorId(clienteId);

        assertThat(resultado).isEmpty();
        verify(clienteRepositoryPort, times(1)).findById(clienteId);
        verifyNoMoreInteractions(clienteRepositoryPort);
    }
}