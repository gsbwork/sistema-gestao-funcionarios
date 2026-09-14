package com.saam.gestao.service;

import com.saam.gestao.dao.UsuarioDAO;
import com.saam.gestao.exception.ValidationException;
import com.saam.gestao.model.Usuario;
import com.saam.gestao.util.HashUtil;
import com.saam.gestao.util.PasswordPolicy;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Regras de negocio das telas de Cadastro e Login: validacao de campos,
 * verificacao de duplicidade de e-mail e autenticacao via hash SHA-256.
 */
public class AuthService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final UsuarioDAO usuarioDAO;

    public AuthService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public AuthService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public Usuario cadastrar(String nome, String email, String senha) {
        validarNome(nome);
        validarEmail(email);
        validarSenha(senha);

        String emailNormalizado = email.trim().toLowerCase();
        if (usuarioDAO.existePorEmail(emailNormalizado)) {
            throw new ValidationException("Ja existe um usuario cadastrado com este e-mail.");
        }

        String senhaHash = HashUtil.sha256(senha);
        Usuario usuario = new Usuario(nome.trim(), emailNormalizado, senhaHash);
        return usuarioDAO.cadastrar(usuario);
    }

    public Usuario autenticar(String email, String senha) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Informe o e-mail.");
        }
        if (senha == null || senha.isEmpty()) {
            throw new ValidationException("Informe a senha.");
        }

        Optional<Usuario> usuarioOpt = usuarioDAO.buscarPorEmail(email.trim().toLowerCase());
        if (usuarioOpt.isEmpty() || !HashUtil.matches(senha, usuarioOpt.get().getSenhaHash())) {
            throw new ValidationException("E-mail ou senha invalidos.");
        }
        return usuarioOpt.get();
    }

    private void validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new ValidationException("O campo Nome e obrigatorio.");
        }
        if (nome.trim().length() < 3) {
            throw new ValidationException("O nome deve ter ao menos 3 caracteres.");
        }
    }

    private void validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("O campo E-mail e obrigatorio.");
        }
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("Informe um e-mail em formato valido.");
        }
    }

    private void validarSenha(String senha) {
        if (senha == null || senha.isEmpty()) {
            throw new ValidationException("O campo Senha e obrigatorio.");
        }
        if (!PasswordPolicy.isValida(senha)) {
            throw new ValidationException("A senha deve ter ao menos " + PasswordPolicy.TAMANHO_MINIMO
                    + " caracteres, incluindo letra maiuscula, minuscula, numero e caractere especial.");
        }
    }
}
