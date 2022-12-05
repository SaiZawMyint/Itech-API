package com.itech.api.repository;
import org.springframework.data.repository.CrudRepository;

import com.itech.api.persistence.entity.User;

public interface UserRepository extends CrudRepository<User, Integer>{

    User findByEmail(String email);
    
    User findByUsername(String username);
    
}
