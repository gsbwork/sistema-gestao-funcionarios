package com.saam.gestao.exception;

/**
 * Excecao lancada quando um dado informado pelo usuario viola uma regra
 * de validacao de negocio (campo obrigatorio, formato invalido, duplicidade, etc).
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
