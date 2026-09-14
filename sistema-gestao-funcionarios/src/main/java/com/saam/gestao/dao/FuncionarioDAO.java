package com.saam.gestao.dao;

import com.saam.gestao.config.ConnectionFactory;
import com.saam.gestao.exception.DatabaseException;
import com.saam.gestao.model.Funcionario;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Acesso a dados da tabela "funcionarios" via JDBC puro (SQL nativo, sem ORM).
 */
public class FuncionarioDAO {

    private static final String SQL_INSERT =
            "INSERT INTO funcionarios (nome, data_admissao, salario, status, data_cadastro) "
            + "VALUES (?, ?, ?, ?, ?) RETURNING id";

    private static final String SQL_UPDATE =
            "UPDATE funcionarios SET nome = ?, data_admissao = ?, salario = ?, status = ? WHERE id = ?";

    private static final String SQL_DELETE = "DELETE FROM funcionarios WHERE id = ?";

    private static final String SQL_FIND_ALL =
            "SELECT id, nome, data_admissao, salario, status, data_cadastro "
            + "FROM funcionarios ORDER BY nome";

    public Funcionario cadastrar(Funcionario funcionario) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {

            ps.setString(1, funcionario.getNome());
            ps.setDate(2, Date.valueOf(funcionario.getDataAdmissao()));
            ps.setBigDecimal(3, funcionario.getSalario());
            ps.setBoolean(4, funcionario.isStatus());
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    funcionario.setId(rs.getLong("id"));
                }
            }
            return funcionario;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao cadastrar funcionario no banco de dados: " + e.getMessage(), e);
        }
    }

    public void atualizar(Funcionario funcionario) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, funcionario.getNome());
            ps.setDate(2, Date.valueOf(funcionario.getDataAdmissao()));
            ps.setBigDecimal(3, funcionario.getSalario());
            ps.setBoolean(4, funcionario.isStatus());
            ps.setLong(5, funcionario.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao atualizar funcionario: " + e.getMessage(), e);
        }
    }

    public void excluir(Long id) {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao excluir funcionario: " + e.getMessage(), e);
        }
    }

    public List<Funcionario> listarTodos() {
        List<Funcionario> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Funcionario(
                        rs.getLong("id"),
                        rs.getString("nome"),
                        rs.getDate("data_admissao").toLocalDate(),
                        rs.getBigDecimal("salario"),
                        rs.getBoolean("status"),
                        rs.getTimestamp("data_cadastro").toLocalDateTime()));
            }
            return lista;
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao listar funcionarios: " + e.getMessage(), e);
        }
    }
}
