package com.robspecs.otp.exception;

import org.springframework.security.core.AuthenticationException;

public class JWTTokenInvalidClaimException extends AuthenticationException {

	/**
	 *
	 */
	private static final long serialVersionUID = 6892976905765857346L;

	public JWTTokenInvalidClaimException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}

}
