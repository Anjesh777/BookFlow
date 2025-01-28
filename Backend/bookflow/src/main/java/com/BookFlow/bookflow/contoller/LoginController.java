package com.BookFlow.bookflow.contoller;

import com.BookFlow.bookflow.dto.AuthenticationResponse;
import com.BookFlow.bookflow.dto.UserDTO;
import com.BookFlow.bookflow.services.AuthenticationService;
import com.BookFlow.bookflow.utils.customException.UserNotVerifiedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("all")
public class LoginController {

    private final AuthenticationService authenticationService;
    public LoginController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserDTO userDTO) {
        Map<String, String> response = new HashMap<>();

        try {


            AuthenticationResponse authResponse = authenticationService.login(userDTO);

            response.put("status", "success");
            response.put("message", "Login successful");
            response.put("accessToken", authResponse.getAuthenticationToken());
            response.put("refreshToken", authResponse.getRefreshToken());
            response.put("role",authResponse.getUser_role());

            return ResponseEntity.ok().body(response);
        }
        catch (UserNotVerifiedException e) {
            response.put("status", "error");
            response.put("message", "Please verify your account before logging in");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);

        }
        catch (BadCredentialsException | UsernameNotFoundException e) {
            response.put("status", "error");
            response.put("message", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        catch (Exception e) {
            log.error("Login failed", e);
            response.put("status", "error");
            response.put("message", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestHeader("Refresh-Token") String refreshToken) {
        Map<String, String> response = new HashMap<>();

        try {
            AuthenticationResponse authResponse = authenticationService.refreshToken(refreshToken);

            response.put("status", "success");
            response.put("message", "Token refreshed successfully");
            response.put("accessToken", authResponse.getAuthenticationToken());
            response.put("refreshToken", authResponse.getRefreshToken());

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            log.error("Token refresh failed", e);
            response.put("status", "error");
            response.put("message", "Invalid refresh token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

    }







}
