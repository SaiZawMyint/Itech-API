package com.itech.api.bl.service;

import org.springframework.http.HttpHeaders;

public interface AuthService {

    public Object requestCode();
    
    public Object authorize(HttpHeaders header, String code);
    
}
