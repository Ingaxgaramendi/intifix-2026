package com.intifix.shared.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncryptionUtils {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String encriptarTexto(String textoPlano) {
        return encoder.encode(textoPlano);
    }

    public static boolean verificarMatch(String textoPlano, String textoEncriptado) {
        return encoder.matches(textoPlano, textoEncriptado);
    }
}
