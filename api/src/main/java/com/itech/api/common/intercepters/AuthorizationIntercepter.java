package com.itech.api.common.intercepters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.itech.api.pkg.tools.pkg.jwt.JwtTokenUtil;

@PropertySource("classpath:app.properties")
public class AuthorizationIntercepter{

    @Autowired
    private Environment enviroment;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    
    
}
