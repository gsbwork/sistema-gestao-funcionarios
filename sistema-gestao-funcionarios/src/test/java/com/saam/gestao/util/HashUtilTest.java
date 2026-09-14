package com.saam.gestao.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HashUtilTest {

    @Test
    void deveGerarHashDe64CaracteresHexadecimais() {
        String hash = HashUtil.sha256("senha123");
        assertEquals(64, hash.length());
        assertTrue(hash.matches("[0-9a-f]+"));
    }

    @Test
    void mesmaEntradaDeveGerarSempreOMesmoHash() {
        assertEquals(HashUtil.sha256("abc123"), HashUtil.sha256("abc123"));
    }

    @Test
    void entradasDiferentesDevemGerarHashesDiferentes() {
        assertNotEquals(HashUtil.sha256("abc123"), HashUtil.sha256("abc124"));
    }

    @Test
    void matchesDeveConfirmarSenhaCorreta() {
        String hash = HashUtil.sha256("minhaSenha");
        assertTrue(HashUtil.matches("minhaSenha", hash));
    }

    @Test
    void matchesDeveRejeitarSenhaIncorreta() {
        String hash = HashUtil.sha256("minhaSenha");
        assertFalse(HashUtil.matches("senhaErrada", hash));
    }
}
