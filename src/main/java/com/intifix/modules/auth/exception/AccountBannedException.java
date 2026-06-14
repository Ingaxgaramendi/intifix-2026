package com.intifix.modules.auth.exception;

public class AccountBannedException extends AuthException {

    public AccountBannedException(String message) {
        super(message, "ACCOUNT_BANNED");
    }

    public static AccountBannedException defaultMessage() {
        return new AccountBannedException("La cuenta ha sido baneada permanentemente. No es posible acceder.");
    }
}
