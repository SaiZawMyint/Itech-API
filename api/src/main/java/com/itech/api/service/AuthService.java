package com.itech.api.service;

import org.springframework.http.HttpHeaders;

public interface AuthService {

    public Object requestCode();
    
    public Object authorize(HttpHeaders header, String code);
    
}
