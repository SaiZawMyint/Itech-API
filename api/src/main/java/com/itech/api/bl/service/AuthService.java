package com.itech.api.bl.service;

import org.springframework.http.HttpHeaders;

import com.itech.api.form.AuthRequestForm;
import com.itech.api.form.UserForm;
import com.itech.api.persistence.entity.User;

import jakarta.validation.Valid;

public interface AuthService {
    
    public Object loginUser(AuthRequestForm form);

    public Object requestServiceCode();
    
    public Object authorize(HttpHeaders header, String code);

    public Object registerUser(@Valid UserForm form);
    
    public User getLoggedUser();
    
}
