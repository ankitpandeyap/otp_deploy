package com.robspecs.otp.entity;

import com.robspecs.otp.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "users", indexes = { @Index(name = "email_idx", columnList = "email", unique = true) })
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;


	private Role role;

	private boolean enabled; // After OTP verification

	// Constructors
	public User() {
	}

	public User(String email, String password, Role role, boolean enabled) {
		this.email = email;
		this.password = password;
		this.role = role;
		this.enabled = enabled;
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}



	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRoles() {
		return role;
	}

	public void setRoles(Role role) {
		this.role = role;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
