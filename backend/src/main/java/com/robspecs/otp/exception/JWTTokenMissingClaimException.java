package com.robspecs.otp.exception;

import org.springframework.security.core.AuthenticationException;

public class JWTTokenMissingClaimException extends AuthenticationException {

	/**
	 *
	 */
	private static final long serialVersionUID = -163346082498648199L;

	public JWTTokenMissingClaimException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}

}
