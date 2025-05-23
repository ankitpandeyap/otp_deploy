package com.robspecs.otp.security;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.robspecs.otp.dto.LoginRequestDTO;
import com.robspecs.otp.util.JWTUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

	private final AuthenticationManager authenticationManager;
	private final JWTUtil jwtUtil;

	public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		if (!request.getServletPath().equals("/api/auth/login")) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			LoginRequestDTO loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequestDTO.class);

			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
					loginRequest.getUsername(), loginRequest.getPassword());

			Authentication authResult = authenticationManager.authenticate(authToken);
             
			UserDetails userDetails = (UserDetails) authResult.getPrincipal();
			if(userDetails.isEnabled() == false) throw new Exception("Profile not verified Re-Verfiy Profile");
			 
			
			if (authResult.isAuthenticated()) {

				String token = jwtUtil.generateToken(authResult.getName(), 15); // 15min
				response.setHeader("Authorization", "Bearer " + token);

				String refreshToken = jwtUtil.generateToken(authResult.getName(), 7 * 24 * 60);
				Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
				refreshCookie.setHttpOnly(true);
				refreshCookie.setSecure(false);
				refreshCookie.setPath("/"); // Required for cookie to be sent to all endpoints
				refreshCookie.setMaxAge(7 * 24 * 60 * 60);
				response.addCookie(refreshCookie);
				response.setContentType("application/json");
				response.getWriter().write("{\"message\":\"Login successful\"}");
			
				
			}

		} catch (Exception e) {
			request.setAttribute("custom-error", e.getMessage());
			request.setAttribute("custom-exception", e.getClass().getName());
			throw new BadCredentialsException(e.getLocalizedMessage());
		}
	}
}
