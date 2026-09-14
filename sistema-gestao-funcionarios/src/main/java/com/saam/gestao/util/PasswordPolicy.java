package com.saam.gestao.util;

import java.util.regex.Pattern;

/**
 * Regras de complexidade de senha, compartilhadas entre a validacao de
 * negocio (AuthService) e o checklist visual exibido na tela de cadastro.
 */
public final class PasswordPolicy {

    public static final int TAMANHO_MINIMO = 6;

    private static final Pattern ESPECIAL = Pattern.compile("[^A-Za-z0-9]");

    private PasswordPolicy() {
    }

    public static boolean temTamanhoMinimo(String senha) {
        return senha != null && senha.length() >= TAMANHO_MINIMO;
    }

    public static boolean temMaiuscula(String senha) {
        return senha != null && senha.chars().anyMatch(Character::isUpperCase);
    }

    public static boolean temMinuscula(String senha) {
        return senha != null && senha.chars().anyMatch(Character::isLowerCase);
    }

    public static boolean temNumero(String senha) {
        return senha != null && senha.chars().anyMatch(Character::isDigit);
    }

    public static boolean temCaractereEspecial(String senha) {
        return senha != null && ESPECIAL.matcher(senha).find();
    }

    public static boolean isValida(String senha) {
        return temTamanhoMinimo(senha) && temMaiuscula(senha) && temMinuscula(senha)
                && temNumero(senha) && temCaractereEspecial(senha);
    }
}
