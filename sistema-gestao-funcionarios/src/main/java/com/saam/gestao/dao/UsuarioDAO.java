package com.saam.gestao.dao;

import com.saam.gestao.config.ConnectionFactory;
import com.saam.gestao.exception.DatabaseException;
import com.saam.gestao.model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

/**
 * Acesso a dados da tabela "usuarios" via JDBC puro (SQL nativo, sem ORM).
 */
public class UsuarioDAO {

    private static final String SQL_INSERT =
            "INSERT INTO usuarios (nome, email, senha_hash, data_cadastro) VALUES (?, ?, ?, ?) RETURNING id";

    private static final String SQL_FIND_BY_EMAIL =
            "SELECT id, nome, email, senha_hash, data_cadastro FROM usuarios WHERE LOWER(email) = LOWER(?)";

    private static final String SQL_EXISTS_BY_EMAIL =
            "SELECT 1 FROM usuarios WHERE LOWER(email) = LOWER(?)";

    public Usuario cadastrar(Usuario usuario) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {

            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getSenhaHash());
            ps.setTimestamp(4, Timestamp.valueOf(java.time.LocalDateTime.now()));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario.setId(rs.getLong("id"));
                }
            }
            return usuario;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao cadastrar usuario no banco de dados: " + e.getMessage(), e);
        }
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_EMAIL)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Usuario(
                            rs.getLong("id"),
                            rs.getString("nome"),
                            rs.getString("email"),
                            rs.getString("senha_hash"),
                            rs.getTimestamp("data_cadastro").toLocalDateTime()));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao consultar usuario por e-mail: " + e.getMessage(), e);
        }
    }

    public boolean existePorEmail(String email) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_EXISTS_BY_EMAIL)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao verificar duplicidade de e-mail: " + e.getMessage(), e);
        }
    }
}
