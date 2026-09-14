package com.saam.gestao.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Geracao e verificacao de hash SHA-256 para senhas.
 * Requisito do edital: "A senha deve ser criptografada utilizando o
 * algoritmo SHA256 antes de ser armazenada no banco de dados."
 */
public final class HashUtil {

    private static final String ALGORITHM = "SHA-256";

    private HashUtil() {
    }

    public static String sha256(String texto) {
        if (texto == null) {
            throw new IllegalArgumentException("Texto para hash nao pode ser nulo.");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            byte[] hashBytes = digest.digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hashBytes.length * 2);
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo " + ALGORITHM + " indisponivel na JVM.", e);
        }
    }

    public static boolean matches(String senhaPlana, String hashArmazenado) {
        if (senhaPlana == null || hashArmazenado == null) {
            return false;
        }
        return sha256(senhaPlana).equalsIgnoreCase(hashArmazenado);
    }
}
