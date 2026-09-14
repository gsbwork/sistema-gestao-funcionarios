package com.saam.gestao.service;

import com.saam.gestao.dao.FuncionarioDAO;
import com.saam.gestao.exception.ValidationException;
import com.saam.gestao.model.Funcionario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testes de FuncionarioService usando um FuncionarioDAO em memoria
 * (sem acessar o PostgreSQL), cobrindo as regras de validacao de campos.
 */
class FuncionarioServiceTest {

    static class FuncionarioDAOEmMemoria extends FuncionarioDAO {
        private final List<Funcionario> base = new ArrayList<>();
        private long proximoId = 1L;

        @Override
        public Funcionario cadastrar(Funcionario funcionario) {
            funcionario.setId(proximoId++);
            base.add(funcionario);
            return funcionario;
        }

        @Override
        public void atualizar(Funcionario funcionario) {
            for (int i = 0; i < base.size(); i++) {
                if (base.get(i).getId().equals(funcionario.getId())) {
                    base.set(i, funcionario);
                    return;
                }
            }
        }

        @Override
        public void excluir(Long id) {
            base.removeIf(f -> f.getId().equals(id));
        }

        @Override
        public List<Funcionario> listarTodos() {
            return base;
        }
    }

    private FuncionarioService funcionarioService;

    @BeforeEach
    void setUp() {
        funcionarioService = new FuncionarioService(new FuncionarioDAOEmMemoria());
    }

    @Test
    void deveCadastrarFuncionarioValido() {
        Funcionario funcionario = funcionarioService.cadastrar(
                "Maria Souza", LocalDate.of(2024, 1, 10), new BigDecimal("3500.00"), true);

        assertEquals("Maria Souza", funcionario.getNome());
        assertTrue(funcionario.isStatus());
        assertEquals(1, funcionarioService.listarTodos().size());
    }

    @Test
    void naoDeveCadastrarComNomeVazio() {
        assertThrows(ValidationException.class,
                () -> funcionarioService.cadastrar("", LocalDate.now(), new BigDecimal("2000"), true));
    }

    @Test
    void naoDeveCadastrarComDataDeAdmissaoFutura() {
        assertThrows(ValidationException.class,
                () -> funcionarioService.cadastrar("Maria Souza", LocalDate.now().plusDays(1),
                        new BigDecimal("2000"), true));
    }

    @Test
    void naoDeveCadastrarComSalarioZeroOuNegativo() {
        assertThrows(ValidationException.class,
                () -> funcionarioService.cadastrar("Maria Souza", LocalDate.now(), BigDecimal.ZERO, true));
    }

    @Test
    void naoDeveCadastrarComSalarioNulo() {
        assertThrows(ValidationException.class,
                () -> funcionarioService.cadastrar("Maria Souza", LocalDate.now(), null, true));
    }

    @Test
    void naoDeveCadastrarComNomeNulo() {
        assertThrows(ValidationException.class,
                () -> funcionarioService.cadastrar(null, LocalDate.now(), new BigDecimal("2000"), true));
    }

    @Test
    void naoDeveCadastrarComDataDeAdmissaoNula() {
        assertThrows(ValidationException.class,
                () -> funcionarioService.cadastrar("Maria Souza", null, new BigDecimal("2000"), true));
    }

    @Test
    void naoDeveAtualizarSemSelecionarUmId() {
        assertThrows(ValidationException.class,
                () -> funcionarioService.atualizar(null, "Maria Souza", LocalDate.now(),
                        new BigDecimal("2000"), true));
    }

    @Test
    void naoDeveExcluirSemSelecionarUmId() {
        assertThrows(ValidationException.class, () -> funcionarioService.excluir(null));
    }

    @Test
    void deveAtualizarFuncionarioExistente() {
        Funcionario original = funcionarioService.cadastrar(
                "Maria Souza", LocalDate.of(2024, 1, 10), new BigDecimal("3500.00"), true);

        funcionarioService.atualizar(original.getId(), "Maria Souza Silva",
                LocalDate.of(2024, 2, 1), new BigDecimal("4000.00"), false);

        Funcionario atualizado = funcionarioService.listarTodos().get(0);
        assertEquals("Maria Souza Silva", atualizado.getNome());
        assertEquals(new BigDecimal("4000.00"), atualizado.getSalario());
        assertEquals(false, atualizado.isStatus());
        assertEquals(1, funcionarioService.listarTodos().size());
    }

    @Test
    void deveExcluirFuncionarioExistente() {
        Funcionario funcionario = funcionarioService.cadastrar(
                "Maria Souza", LocalDate.now(), new BigDecimal("3500.00"), true);

        funcionarioService.excluir(funcionario.getId());

        assertTrue(funcionarioService.listarTodos().isEmpty());
    }

    @Test
    void deveCadastrarComDataDeAdmissaoIgualAHoje() {
        Funcionario funcionario = funcionarioService.cadastrar(
                "Maria Souza", LocalDate.now(), new BigDecimal("2000"), true);
        assertEquals(LocalDate.now(), funcionario.getDataAdmissao());
    }

    @Test
    void deveCadastrarComSalarioMinimoPositivo() {
        Funcionario funcionario = funcionarioService.cadastrar(
                "Maria Souza", LocalDate.now(), new BigDecimal("0.01"), true);
        assertEquals(new BigDecimal("0.01"), funcionario.getSalario());
    }

    @Test
    void naoDeveCadastrarComSalarioNegativo() {
        assertThrows(ValidationException.class,
                () -> funcionarioService.cadastrar("Maria Souza", LocalDate.now(),
                        new BigDecimal("-100"), true));
    }
}
