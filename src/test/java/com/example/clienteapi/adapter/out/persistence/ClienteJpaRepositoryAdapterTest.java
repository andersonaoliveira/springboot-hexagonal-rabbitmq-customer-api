package com.example.clienteapi.adapter.out.persistence;

import com.example.clienteapi.domain.model.Cliente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import(ClienteJpaRepositoryAdapter.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) 
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@DisplayName("Testes de Integração para ClienteJpaRepositoryAdapter")
class ClienteJpaRepositoryAdapterTest {

    @Autowired
    private ClienteJpaRepositoryAdapter clienteJpaRepositoryAdapter;

    @Autowired
    private ClienteJpaRepository clienteJpaRepository;


    @Test
    @DisplayName("Deve salvar um novo cliente no banco de dados")
    void deveSalvarNovoCliente() {
        Cliente novoCliente = new Cliente(null, "Maria Teste", "maria.teste@example.com", "11111111111");

        Cliente clienteSalvo = clienteJpaRepositoryAdapter.save(novoCliente);

        assertThat(clienteSalvo).isNotNull();
        assertThat(clienteSalvo.getId()).isNotNull();
        assertThat(clienteSalvo.getNome()).isEqualTo("Maria Teste");
        assertThat(clienteSalvo.getEmail()).isEqualTo("maria.teste@example.com");

        Optional<ClienteJpaEntity> foundEntity = clienteJpaRepository.findById(clienteSalvo.getId());
        assertTrue(foundEntity.isPresent());
        assertThat(foundEntity.get().getEmail()).isEqualTo("maria.teste@example.com");
    }

    @Test
    @DisplayName("Deve buscar um cliente existente por ID")
    void deveBuscarClientePorIdExistente() {
        ClienteJpaEntity entity = new ClienteJpaEntity(null, "Joao Busca", "joao.busca@example.com", "22222222222");
        ClienteJpaEntity savedEntity = clienteJpaRepository.save(entity);

        Optional<Cliente> resultado = clienteJpaRepositoryAdapter.findById(savedEntity.getId());

        assertThat(resultado).isPresent();
        assertThat(resultado.get().getId()).isEqualTo(savedEntity.getId());
        assertThat(resultado.get().getEmail()).isEqualTo("joao.busca@example.com");
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar cliente por ID inexistente")
    void deveRetornarVazioAoBuscarClientePorIdInexistente() {

        Optional<Cliente> resultado = clienteJpaRepositoryAdapter.findById(999L);

        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Deve buscar todos os clientes no banco de dados")
    void deveBuscarTodosClientes() {
        clienteJpaRepository.save(new ClienteJpaEntity(null, "Cliente A", "a@example.com", "33333333333"));
        clienteJpaRepository.save(new ClienteJpaEntity(null, "Cliente B", "b@example.com", "44444444444"));

        List<Cliente> clientes = clienteJpaRepositoryAdapter.findAll();

        assertThat(clientes).isNotNull();
        assertThat(clientes).hasSize(2);
        assertThat(clientes).extracting(Cliente::getEmail).contains("a@example.com", "b@example.com");
    }

    @Test
    @DisplayName("Deve deletar um cliente por ID")
    void deveDeletarClientePorId() {
        ClienteJpaEntity entity = new ClienteJpaEntity(null, "Cliente Delete", "delete@example.com", "55555555555");
        ClienteJpaEntity savedEntity = clienteJpaRepository.save(entity);

        clienteJpaRepositoryAdapter.deleteById(savedEntity.getId());

        Optional<ClienteJpaEntity> foundEntity = clienteJpaRepository.findById(savedEntity.getId());
        assertFalse(foundEntity.isPresent());
    }

    @Test
    @DisplayName("Deve retornar true se o email do cliente existir")
    void deveRetornarTrueSeEmailExistir() {
        clienteJpaRepository.save(new ClienteJpaEntity(null, "Email Existe", "existente@example.com", "66666666666"));

        boolean existe = clienteJpaRepositoryAdapter.existsByEmail("existente@example.com");

        assertTrue(existe);
    }

    @Test
    @DisplayName("Deve retornar false se o email do cliente não existir")
    void deveRetornarFalseSeEmailNaoExistir() {

        boolean existe = clienteJpaRepositoryAdapter.existsByEmail("nao.existe@example.com");

        assertFalse(existe);
    }
}