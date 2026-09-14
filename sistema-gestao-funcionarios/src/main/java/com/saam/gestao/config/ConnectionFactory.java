package com.saam.gestao.config;

import com.saam.gestao.exception.DatabaseException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Responsavel por abrir conexoes JDBC nativas com o PostgreSQL,
 * lendo os parametros de db.properties (classpath).
 */
public final class ConnectionFactory {

    private static final String CONFIG_FILE = "db.properties";
    private static String host = "localhost";
    private static int port = 5432;
    private static String database = "saam_gestao";
    private static String user = "postgres";
    private static String password = "postgres";
    private static boolean ssl = false;

    static {
        carregarPropriedades();
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new DatabaseException("Driver JDBC do PostgreSQL nao encontrado no classpath.", e);
        }
    }

    private ConnectionFactory() {
    }

    private static void carregarPropriedades() {
        Properties properties = new Properties();
        try (InputStream is = ConnectionFactory.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (is != null) {
                properties.load(is);
                host = properties.getProperty("db.host", host);
                port = Integer.parseInt(properties.getProperty("db.port", String.valueOf(port)));
                database = properties.getProperty("db.name", database);
                user = properties.getProperty("db.user", user);
                password = properties.getProperty("db.password", password);
                ssl = Boolean.parseBoolean(properties.getProperty("db.ssl", "false"));
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("[AVISO] Nao foi possivel ler " + CONFIG_FILE + ", usando valores padrao.");
        }
    }

    public static String getJdbcUrl() {
        return String.format("jdbc:postgresql://%s:%d/%s?ssl=%b", host, port, database, ssl);
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(getJdbcUrl(), user, password);
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Falha ao conectar ao PostgreSQL em " + getJdbcUrl() + ". "
                    + "Verifique se o servico do banco esta ativo e se usuario/senha estao corretos.", e);
        }
    }
}
