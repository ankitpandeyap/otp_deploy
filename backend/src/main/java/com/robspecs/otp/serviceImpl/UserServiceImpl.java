package com.robspecs.otp.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.robspecs.otp.dto.RegistrationDTO;
import com.robspecs.otp.entity.User;
import com.robspecs.otp.enums.Role;
import com.robspecs.otp.repository.UserRepository;
import com.robspecs.otp.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, StringRedisTemplate redisTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public User registerNewUser(RegistrationDTO currDTO) {
        String email = currDTO.getEmail();

        // If email already exists and is enabled, reject registration
        if (userRepository.existsByEmail(email)) {
            User existingUser = userRepository.findByEmail(email).orElse(null);
            if (existingUser != null && existingUser.isEnabled()) {
                throw new RuntimeException("Email already registered!");
            }
        }

        // Check for role (default = CONSUMER if not ADMIN)
        Role role = currDTO.getRole().equalsIgnoreCase("ADMIN") ? Role.ADMIN : Role.CONSUMER;

        // Create and save user
        User user = new User();
        user.setName(currDTO.getName());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(currDTO.getPassword())); // Always encode passwords!
        user.setRole(role);
        user.setEnabled(true);

        // Delete OTP from Redis after successful registration
        redisTemplate.delete(email);

        return userRepository.save(user);
    }
}