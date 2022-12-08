package com.itech.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itech.api.bl.service.AuthService;
import com.itech.api.form.AuthRequestForm;
import com.itech.api.form.UserForm;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/itech/api/auth/")
@CrossOrigin
public class AuthController {
    @Autowired
    private AuthService auth;
   
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody@Valid AuthRequestForm request) {
        return (ResponseEntity<?>) this.auth.loginUser(request);
    }
    
    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody@Valid UserForm form){
        return (ResponseEntity<?>) this.auth.registerUser(form);
    }
}
