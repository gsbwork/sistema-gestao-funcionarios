package com.saam.gestao.config;

import com.saam.gestao.exception.DatabaseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Executa o script schema.sql (DDL nativo) na inicializacao da aplicacao,
 * garantindo que as tabelas existam antes de qualquer tela ser exibida.
 */
public final class DatabaseInitializer {

    private static final String SCHEMA_FILE = "schema.sql";

    private DatabaseInitializer() {
    }

    public static void initializeDatabase() {
        String script = lerScript();
        if (script == null || script.isBlank()) {
            throw new DatabaseException("Arquivo " + SCHEMA_FILE + " nao encontrado ou vazio.");
        }

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement()) {

            for (String comando : script.split(";")) {
                String sql = comando.trim();
                if (!sql.isEmpty()) {
                    stmt.execute(sql);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erro ao criar/verificar as tabelas no PostgreSQL: " + e.getMessage(), e);
        }
    }

    private static String lerScript() {
        try (InputStream is = DatabaseInitializer.class.getClassLoader().getResourceAsStream(SCHEMA_FILE)) {
            if (is == null) {
                return null;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (IOException e) {
            throw new DatabaseException("Erro ao ler " + SCHEMA_FILE + ": " + e.getMessage(), e);
        }
    }
}
