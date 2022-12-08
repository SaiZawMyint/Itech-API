package com.itech.api.respositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itech.api.persistence.entity.Project;

public interface ProjectRepo extends JpaRepository<Project, Integer>{
    
}
