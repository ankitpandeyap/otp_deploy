package com.robspecs.otp.exception;

import org.springframework.security.core.AuthenticationException;

public class JWTBlackListedTokenException extends AuthenticationException {

	public JWTBlackListedTokenException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}

}
