package com.itech.api.respositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itech.api.persistence.entity.Token;

public interface TokenRepository extends JpaRepository<Token, Integer>{

}
