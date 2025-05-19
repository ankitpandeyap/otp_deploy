package com.robspecs.otp.exception;

import org.springframework.security.core.AuthenticationException;

public class JWTTokenUsernameNotFoundException extends AuthenticationException {

	/**
	 *
	 */
	private static final long serialVersionUID = -5291113858596169152L;

	public JWTTokenUsernameNotFoundException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}

}
