package com.robspecs.otp.security;

import java.io.IOException;
import java.util.List;

import javax.security.sasl.AuthenticationException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.robspecs.otp.exception.JWTBlackListedTokenException;
import com.robspecs.otp.service.TokenBlacklistService;
import com.robspecs.otp.util.JWTUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTRefreshFilter extends OncePerRequestFilter {

	private final AuthenticationManager authenticationManager;
	private final JWTUtil jwtUtil;
	private final CustomUserDetailsService customUserDetailsService;
	private final TokenBlacklistService tokenService;

	public JWTRefreshFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil,
			CustomUserDetailsService customUserDetailsService, TokenBlacklistService tokenService) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
		this.customUserDetailsService = customUserDetailsService;
		this.tokenService = tokenService;
	}
  
	private static final List<String> PUBLIC_URLS = List.of("/api/auth/login", "/api/auth/signup",
			"/api/auth/register", "/api/auth/otp/verify", "/api/auth/otp/request");
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		Boolean isExpiredToken = (Boolean) request.getAttribute("expiredToken");// null can be evaluated top false
		boolean isRefreshRequest = request.getServletPath().equals("/api/auth/refresh");
		boolean isAuthenticated = SecurityContextHolder.getContext().getAuthentication() != null;


		String path = request.getRequestURI();

		if (PUBLIC_URLS.contains(path)) {
			filterChain.doFilter(request, response);
			return;
		}
		
		// If token is NOT expired AND user is authenticated OR it's NOT a refresh call
		if (((isExpiredToken == null || !isExpiredToken) && isAuthenticated) || isRefreshRequest) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			String usernameFromAccessToken = (String) request.getAttribute("expiredTokenUsername");

			String refreshToken = extractJwtFromRequest(request);
			if (refreshToken == null) {
				throw new AuthenticationException("Refresh Token is Invalid or not present");
			}

			if (tokenService.isBlacklisted(refreshToken)) {
				// scope for improvement
				throw new JWTBlackListedTokenException("Acess token is Blacklisted");
			}
			String usernameFromRefreshToken = jwtUtil.validateAndExtractUsername(refreshToken);
			if (!usernameFromAccessToken.equals(usernameFromRefreshToken)) {
				throw new AuthenticationException("Refresh Token is Invalid or not present");
			}

			UserDetails userDetails = customUserDetailsService.loadUserByUsername(usernameFromRefreshToken);
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
					userDetails.getAuthorities());

			String newAccessToken = jwtUtil.generateToken(userDetails.getUsername(), 15);

			response.setHeader("Authorization", "Bearer " + newAccessToken);

			SecurityContextHolder.getContext().setAuthentication(authToken);

			filterChain.doFilter(request, response);

		} catch (Exception e) {
			request.setAttribute("custom-error", "Refresh Token Invalid or Expired: " + e.getMessage());
			request.setAttribute("custom-exception", "JWTRefreshTokenException");
			throw new BadCredentialsException("Refresh token failure");
		}
	}

	private String extractJwtFromRequest(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return null;
		}
		String refreshToken = null;
		for (Cookie cookie : cookies) {
			if ("refreshToken".equals(cookie.getName())) {
				refreshToken = cookie.getValue();
				break;
			}
		}
		return refreshToken;
	}

}
