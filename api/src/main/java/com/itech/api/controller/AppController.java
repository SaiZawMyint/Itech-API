package com.itech.api.controller;

import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itech.api.pkg.tools.Response;
import com.itech.api.pkg.tools.enums.ResponseCode;
import com.itech.api.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/itech/api")
public class AppController {

    @Autowired
    HttpSession session;
    @Autowired
    AuthService authService;
    
    @GetMapping("/auth/request/code")
    public ResponseEntity<?> getCode() throws URISyntaxException{
        return (ResponseEntity<?>) this.authService.requestCode();
    }
    
    @GetMapping("/authorize")
    public ResponseEntity<?> authorizeRequest(HttpServletRequest request,@RequestHeader HttpHeaders header) throws URISyntaxException{
        String code = (String) request.getParameter("code");
        if(code == null) return Response.send(ResponseCode.REQUIRED, false,"Authorizaion code is required!");
        return (ResponseEntity<?>) this.authService.authorize(header,code);
    }
    
    @GetMapping("/auth/code")
    public String responseCode(HttpServletRequest request) {
        this.session.setAttribute("code", request.getParameter("code"));
        return "code: "+this.session.getAttribute("code");
    }
    
}
