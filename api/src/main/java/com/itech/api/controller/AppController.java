package com.itech.api.controller;

import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itech.api.bl.service.AuthService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/itech/api")
public class AppController {

    @Autowired
    HttpSession session;
    @Autowired
    AuthService authService;
    
    
    @GetMapping("/auth/request/code/{id}")
    public ResponseEntity<?> getCode(@RequestHeader(name = "service") String service,@PathVariable Integer id,@RequestParam String scopes) throws URISyntaxException{
        return (ResponseEntity<?>) this.authService.requestServiceCode(service,id,scopes);
    }
    
    @GetMapping("/auth/authorize")
    public ResponseEntity<?> authorizeRequest(@RequestHeader(name = "service") String service,@RequestParam String code) throws URISyntaxException{
        return (ResponseEntity<?>) this.authService.authorize(service,code);
    }
    
    @GetMapping("/auth/code")
    public String responseCode(@RequestParam String code) {
        this.session.setAttribute("code", code);
        return (String) this.authService.sendCode(code);
    }
    
}
