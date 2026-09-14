package com.saam.gestao.service;

import com.saam.gestao.dao.UsuarioDAO;
import com.saam.gestao.exception.ValidationException;
import com.saam.gestao.model.Usuario;
import com.saam.gestao.util.HashUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testes de AuthService usando um UsuarioDAO em memoria (sem acessar o PostgreSQL),
 * cobrindo as regras de validacao, duplicidade e autenticacao por hash.
 */
class AuthServiceTest {

    /** DAO fake em memoria, usado apenas nos testes para isolar a regra de negocio do banco real. */
    static class UsuarioDAOEmMemoria extends UsuarioDAO {
        private final Map<String, Usuario> base = new HashMap<>();
        private long proximoId = 1L;

        @Override
        public Usuario cadastrar(Usuario usuario) {
            usuario.setId(proximoId++);
            base.put(usuario.getEmail(), usuario);
            return usuario;
        }

        @Override
        public Optional<Usuario> buscarPorEmail(String email) {
            return Optional.ofNullable(base.get(email));
        }

        @Override
        public boolean existePorEmail(String email) {
            return base.containsKey(email);
        }
    }

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(new UsuarioDAOEmMemoria());
    }

    private static final String SENHA_VALIDA = "Senha@123";

    @Test
    void deveCadastrarUsuarioComSenhaCriptografada() {
        Usuario usuario = authService.cadastrar("Joao da Silva", "joao@empresa.com", SENHA_VALIDA);

        assertEquals("joao@empresa.com", usuario.getEmail());
        assertEquals(64, usuario.getSenhaHash().length());
        assertEquals(HashUtil.sha256(SENHA_VALIDA), usuario.getSenhaHash());
    }

    @Test
    void naoDeveCadastrarComEmailDuplicado() {
        authService.cadastrar("Joao da Silva", "joao@empresa.com", SENHA_VALIDA);

        assertThrows(ValidationException.class,
                () -> authService.cadastrar("Outro Joao", "joao@empresa.com", "Outra@123"));
    }

    @Test
    void naoDeveCadastrarComEmailInvalido() {
        assertThrows(ValidationException.class,
                () -> authService.cadastrar("Joao da Silva", "email-invalido", SENHA_VALIDA));
    }

    @Test
    void naoDeveCadastrarComSenhaCurta() {
        assertThrows(ValidationException.class,
                () -> authService.cadastrar("Joao da Silva", "joao@empresa.com", "A1@"));
    }

    @Test
    void naoDeveCadastrarComSenhaSemLetraMaiuscula() {
        assertThrows(ValidationException.class,
                () -> authService.cadastrar("Joao da Silva", "joao@empresa.com", "senha@123"));
    }

    @Test
    void naoDeveCadastrarComSenhaSemLetraMinuscula() {
        assertThrows(ValidationException.class,
                () -> authService.cadastrar("Joao da Silva", "joao@empresa.com", "SENHA@123"));
    }

    @Test
    void naoDeveCadastrarComSenhaSemNumero() {
        assertThrows(ValidationException.class,
                () -> authService.cadastrar("Joao da Silva", "joao@empresa.com", "Senha@abc"));
    }

    @Test
    void naoDeveCadastrarComSenhaSemCaractereEspecial() {
        assertThrows(ValidationException.class,
                () -> authService.cadastrar("Joao da Silva", "joao@empresa.com", "Senha123"));
    }

    @Test
    void deveAutenticarComCredenciaisCorretas() {
        authService.cadastrar("Joao da Silva", "joao@empresa.com", SENHA_VALIDA);

        Usuario autenticado = authService.autenticar("joao@empresa.com", SENHA_VALIDA);

        assertEquals("joao@empresa.com", autenticado.getEmail());
    }

    @Test
    void naoDeveAutenticarComSenhaIncorreta() {
        authService.cadastrar("Joao da Silva", "joao@empresa.com", SENHA_VALIDA);

        assertThrows(ValidationException.class,
                () -> authService.autenticar("joao@empresa.com", "SenhaErrada@1"));
    }

    @Test
    void naoDeveAutenticarUsuarioInexistente() {
        assertThrows(ValidationException.class,
                () -> authService.autenticar("naoexiste@empresa.com", SENHA_VALIDA));
    }

    @Test
    void naoDeveAutenticarComEmailVazio() {
        assertThrows(ValidationException.class, () -> authService.autenticar("", SENHA_VALIDA));
    }

    @Test
    void naoDeveAutenticarComSenhaVazia() {
        authService.cadastrar("Joao da Silva", "joao@empresa.com", SENHA_VALIDA);
        assertThrows(ValidationException.class, () -> authService.autenticar("joao@empresa.com", ""));
    }

    @Test
    void naoDeveCadastrarComNomeApenasEspacos() {
        assertThrows(ValidationException.class,
                () -> authService.cadastrar("   ", "joao@empresa.com", SENHA_VALIDA));
    }

    @Test
    void duplicidadeDeEmailDeveSerCaseInsensitive() {
        authService.cadastrar("Joao da Silva", "joao@empresa.com", SENHA_VALIDA);

        assertThrows(ValidationException.class,
                () -> authService.cadastrar("Outro Joao", "JOAO@EMPRESA.COM", "Outra@123"));
    }

    @Test
    void deveAutenticarComEmailEmCaixaDiferenteDoCadastro() {
        authService.cadastrar("Joao da Silva", "joao@empresa.com", SENHA_VALIDA);

        Usuario autenticado = authService.autenticar("JOAO@empresa.com", SENHA_VALIDA);

        assertEquals("joao@empresa.com", autenticado.getEmail());
    }

    @Test
    void naoDeveCadastrarComNomeNulo() {
        assertThrows(ValidationException.class,
                () -> authService.cadastrar(null, "joao@empresa.com", SENHA_VALIDA));
    }

    @Test
    void naoDeveCadastrarComEmailNulo() {
        assertThrows(ValidationException.class,
                () -> authService.cadastrar("Joao da Silva", null, SENHA_VALIDA));
    }

    @Test
    void naoDeveCadastrarComSenhaNula() {
        assertThrows(ValidationException.class,
                () -> authService.cadastrar("Joao da Silva", "joao@empresa.com", null));
    }

    @Test
    void naoDeveCadastrarComNomeDeDoisCaracteres() {
        assertThrows(ValidationException.class,
                () -> authService.cadastrar("Jo", "joao@empresa.com", SENHA_VALIDA));
    }

    @Test
    void deveCadastrarComNomeDeExatamenteTresCaracteres() {
        Usuario usuario = authService.cadastrar("Joa", "joao@empresa.com", SENHA_VALIDA);
        assertEquals("Joa", usuario.getNome());
    }
}
