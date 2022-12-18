package com.itech.api.bl.service;

import com.itech.api.form.AuthRequestForm;
import com.itech.api.form.UserForm;
import com.itech.api.persistence.entity.User;

import jakarta.validation.Valid;

public interface AuthService {
    
    public Object loginUser(AuthRequestForm form);

    public Object requestServiceCode(String service, Integer projectId, String scopes, String u_token);
    
    public Object authorize(Integer id, String service, String code);

    public Object registerUser(@Valid UserForm form);
    
    public User getLoggedUser(String token);

    public Object sendCode(String code);

    public Object status(Integer id, String access_token);
    
}
