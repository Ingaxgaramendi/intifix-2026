package com.intifix.modules.chat.exception;

/**
 * Excepción base del módulo chat. Lleva un código de error estable para que el
 * handler y los clientes puedan reaccionar sin parsear mensajes.
 */
public abstract class ChatException extends RuntimeException {

    private final String codigo;

    protected ChatException(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
