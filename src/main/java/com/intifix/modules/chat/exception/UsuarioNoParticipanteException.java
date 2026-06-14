package com.intifix.modules.chat.exception;

/**
 * Se lanza cuando un usuario intenta acceder u operar sobre una conversación de
 * la que no es participante (defensa anti-IDOR). Se mapea a 403.
 */
public class UsuarioNoParticipanteException extends ChatException {

    private static final String CODIGO = "USUARIO_NO_PARTICIPANTE";

    public UsuarioNoParticipanteException(String mensaje) {
        super(CODIGO, mensaje);
    }

    public static UsuarioNoParticipanteException porDefecto() {
        return new UsuarioNoParticipanteException("No tiene acceso a esta conversación");
    }
}
