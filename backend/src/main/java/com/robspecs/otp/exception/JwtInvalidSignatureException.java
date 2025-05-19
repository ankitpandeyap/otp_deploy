package com.robspecs.otp.exception;

import org.springframework.security.core.AuthenticationException;

public class JwtInvalidSignatureException extends AuthenticationException {
    public JwtInvalidSignatureException(String msg) {
        super(msg);
    }
}