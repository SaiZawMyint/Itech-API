package com.itech.api.controller;

import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itech.api.bl.service.AuthService;
import com.itech.api.form.AuthRequestForm;
import com.itech.api.form.CodeForm;
import com.itech.api.form.UserForm;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/itech/api/auth/")
@CrossOrigin
public class AuthController {
    @Autowired
    private AuthService auth;
    
    @GetMapping("/request/code/{id}")
    public ResponseEntity<?> getCode(@RequestParam String service,@PathVariable Integer id,@Nullable@RequestParam String scopes,@Nullable@RequestParam String u_token) throws URISyntaxException{
        return (ResponseEntity<?>) this.auth.requestServiceCode(service,id,scopes,u_token);
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody@Valid AuthRequestForm request) {
        return (ResponseEntity<?>) this.auth.loginUser(request);
    }
    
    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody@Valid UserForm form){
        return (ResponseEntity<?>) this.auth.registerUser(form);
    }
    
    @PostMapping("/authorize/{id}")
    public ResponseEntity<?> authorizeRequest(@PathVariable Integer id,@RequestHeader(name = "service") String service,@RequestBody CodeForm code) throws URISyntaxException{
        return (ResponseEntity<?>) this.auth.authorize(id,service,code.getCode());
    }
    
    @GetMapping("/code")
    public String responseCode(@RequestParam String code) {
        return (String) this.auth.sendCode(code);
    }
}
