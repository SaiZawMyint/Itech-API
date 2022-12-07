package com.itech.api.persistence.dao;

import com.itech.api.persistence.entity.User;

public interface UserDAO {

    public User getUserByEmail(String email);
    
}
