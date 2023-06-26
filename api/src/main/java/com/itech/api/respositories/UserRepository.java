package com.itech.api.respositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itech.api.persistence.entity.User;

public interface UserRepository extends JpaRepository<User, Integer>{

    Optional<User> findByEmail(String email);
    
}
