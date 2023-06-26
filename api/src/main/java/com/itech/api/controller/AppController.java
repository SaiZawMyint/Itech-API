package com.itech.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itech.api.bl.service.AuthService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/itech/api")
@CrossOrigin
public class AppController {

    @Autowired
    HttpSession session;
    @Autowired
    AuthService authService;
    
//    @GetMapping("/auth/request/code/{id}")
//    public ResponseEntity<?> getCode(@RequestParam String service,@PathVariable Integer id,@Nullable@RequestParam String scopes) throws URISyntaxException{
//        return (ResponseEntity<?>) this.authService.requestServiceCode(service,id,scopes);
//    }
    
   
    
}
