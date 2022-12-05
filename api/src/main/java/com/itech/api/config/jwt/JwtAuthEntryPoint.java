package com.itech.api.config.jwt;

import java.io.IOException;
import java.io.Serializable;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthEntryPoint implements AuthenticationEntryPoint, Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 5817717204712841086L;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Unauthorized");
        
    }

}
