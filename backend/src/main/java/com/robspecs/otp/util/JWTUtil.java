package com.robspecs.otp.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.IncorrectClaimException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MissingClaimException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTUtil {

	private final String secretKey; // Make it final and initialize via constructor
	private final Key key;

	// Use constructor injection
	public JWTUtil(@Value("${app.jwt.secret}") String secret) {
		this.secretKey = secret;
		this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	// Generate JWT Token
	public String generateToken(String username, long expiryMinutes) {
		return Jwts.builder().setSubject(username).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + expiryMinutes * 60 * 1000)) // in milliseconds
				.signWith(key, SignatureAlgorithm.HS256).compact();
	}

	public String validateAndExtractUsername(String token) {
		try {
			return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
		} catch (MissingClaimException | IncorrectClaimException | IllegalArgumentException e) {
			throw e;

		}
	}

}
