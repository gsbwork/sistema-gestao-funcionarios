package com.saam.gestao.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordPolicyTest {

    @Test
    void deveAceitarSenhaComTodosOsRequisitos() {
        assertTrue(PasswordPolicy.isValida("Senha@123"));
    }

    @Test
    void deveRejeitarSenhaCurta() {
        assertFalse(PasswordPolicy.isValida("A1@bc"));
    }

    @Test
    void deveRejeitarSemMaiuscula() {
        assertFalse(PasswordPolicy.isValida("senha@123"));
    }

    @Test
    void deveRejeitarSemMinuscula() {
        assertFalse(PasswordPolicy.isValida("SENHA@123"));
    }

    @Test
    void deveRejeitarSemNumero() {
        assertFalse(PasswordPolicy.isValida("Senha@abc"));
    }

    @Test
    void deveRejeitarSemCaractereEspecial() {
        assertFalse(PasswordPolicy.isValida("Senha123"));
    }

    @Test
    void deveRejeitarSenhaNula() {
        assertFalse(PasswordPolicy.isValida(null));
    }

    @Test
    void deveAceitarSenhaComExatamenteOTamanhoMinimo() {
        assertTrue(PasswordPolicy.isValida("Aa1@bc"));
        assertFalse(PasswordPolicy.isValida("Aa1@b"));
    }
}
