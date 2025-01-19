package com.BookFlow.bookflow.services;

import com.BookFlow.bookflow.dto.AuthenticationRequest;
import com.BookFlow.bookflow.dto.AuthenticationResponse;
import com.BookFlow.bookflow.dto.UserDTO;
import com.BookFlow.bookflow.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
@Slf4j

@Service
public class AuthenticationService {

    private final UserRepo userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationService(UserRepo userRepository, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse login(UserDTO userDTO) {
        try {
            log.info("Attempting authentication for user: {}", userDTO.getUserName());



            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userDTO.getUserName(),
                            userDTO.getUserPassword()
                    )
            );

            log.debug("Authentication successful for user: {}", userDTO.getUserName());

            var user = userRepository.findByUsername(userDTO.getUserName())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            log.debug("Generating tokens for user: {}", userDTO.getUserName());
            var jwtToken = jwtService.generateAccessToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);

            String userRole = user.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse(null);



            log.info("Login successful for user: {}", userDTO.getUserName());
            return AuthenticationResponse.builder()
                    .authenticationToken(jwtToken)
                    .refreshToken(refreshToken)
                    .user_role(userRole)
                    .build();

        } catch (BadCredentialsException e) {
            log.error("Authentication failed - Bad credentials for user: {}", userDTO.getUserName());
            throw new BadCredentialsException("Invalid username or password");

        } catch (UsernameNotFoundException e) {
            log.error("Authentication failed - User not found: {}", userDTO.getUserName());
            throw new UsernameNotFoundException("User not found");

        } catch (Exception e) {
            log.error("Authentication failed - Unexpected error for user: {}", userDTO.getUserName(), e);
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }

    public AuthenticationResponse refreshToken(String refreshToken) {

        if (!jwtService.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        var user = userRepository.findByUsername(jwtService.extractUserName(refreshToken))
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        var jwtToken = jwtService.generateAccessToken(user);
        var newRefreshToken = jwtService.generateRefreshToken(user);

        String userRole = user.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse(null);

        return AuthenticationResponse.builder()
                .authenticationToken(jwtToken)
                .refreshToken(newRefreshToken)
                .user_role(userRole)
                .build();
    }





}
