package com.itech.api.respositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itech.api.persistence.entity.Services;

public interface ServiceRepo extends JpaRepository<Services, Integer>{

}
