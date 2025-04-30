package com.BookFlow.bookflow.services;

import com.BookFlow.bookflow.dto.AuthenticationRequest;
import com.BookFlow.bookflow.dto.AuthenticationResponse;
import com.BookFlow.bookflow.dto.UserDTO;
import com.BookFlow.bookflow.model.User;
import com.BookFlow.bookflow.model.VerificationToken;
import com.BookFlow.bookflow.repository.UserRepo;
import com.BookFlow.bookflow.repository.company.VerificationTokenRepo;
import com.BookFlow.bookflow.utils.customException.AccountBlockException;
import com.BookFlow.bookflow.utils.customException.UserNotVerifiedException;
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
import java.util.Optional;

@Slf4j

@Service
public class AuthenticationService {

    private final UserRepo userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final VerificationTokenRepo verificationTokenRepo;

    @Autowired
    public AuthenticationService(UserRepo userRepository, JwtService jwtService, AuthenticationManager authenticationManager, VerificationTokenRepo verificationTokenRepo) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.verificationTokenRepo = verificationTokenRepo;
    }

    public AuthenticationResponse login(UserDTO userDTO) {
        try {
            log.info("Attempting authentication for user: {}", userDTO.getUserName());

            var user = userRepository.findByUsername(userDTO.getUserName())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            var userCompany= user.getCompany_id().isEnabled();
            System.out.println("cmp is "+userCompany);

            if (!userCompany){
                throw new AccountBlockException("This company is blocked.");
            }


            Optional<VerificationToken> verificationToken = verificationTokenRepo.findByUserId(user.getUser_id());


            if (verificationToken.isEmpty() || !verificationToken.get().isUsed()) {
                throw new UserNotVerifiedException("Please verify your account before logging in");
            } else if (!user.is_enabled()) {
                throw new AccountBlockException("User Account is Blocked please contact our team");
            }



            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userDTO.getUserName(),
                            userDTO.getUserPassword()
                    )
            );

            log.debug("Authentication successful for user: {}", userDTO.getUserName());

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
        } catch (UserNotVerifiedException e) {
            log.error("Authentication failed - User not verified: {}", userDTO.getUserName());
            throw e;
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
