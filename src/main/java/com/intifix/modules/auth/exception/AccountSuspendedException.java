package com.intifix.modules.auth.exception;

public class AccountSuspendedException extends AuthException {

    public AccountSuspendedException(String message) {
        super(message, "ACCOUNT_SUSPENDED");
    }

    public static AccountSuspendedException defaultMessage() {
        return new AccountSuspendedException("La cuenta ha sido suspendida temporalmente. Contacte al soporte.");
    }
}
