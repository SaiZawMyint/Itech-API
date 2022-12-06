package com.itech.api.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itech.api.form.AuthForm;
import com.itech.api.form.AuthResponseForm;
import com.itech.api.jwt.JwtUtil;
import com.itech.api.persistence.entity.User;

@RestController
@RequestMapping("/itech/api")
public class AuthController {
    @Autowired AuthenticationManager authManager;
    @Autowired JwtUtil jwtUtil;

    @PostMapping("/auth/login") 
    public ResponseEntity<?> login(@RequestBody AuthForm request) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword())
            );
             
            User user = (User) authentication.getPrincipal();
            String accessToken = jwtUtil.generateAccessToken(user);
            AuthResponseForm response = new AuthResponseForm(user.getEmail(), accessToken);
             
            return ResponseEntity.ok().body(response);
             
        } catch (BadCredentialsException ex) {
            ex.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
    
}
