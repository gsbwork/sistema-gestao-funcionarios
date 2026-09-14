package com.saam.gestao.exception;

/**
 * Excecao lancada quando ocorre falha de conexao com o banco de dados
 * ou erro na execucao de uma consulta/comando SQL.
 */
public class DatabaseException extends RuntimeException {

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
