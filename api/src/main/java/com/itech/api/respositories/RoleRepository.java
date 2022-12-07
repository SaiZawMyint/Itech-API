package com.itech.api.respositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itech.api.persistence.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer>{
    Role findByName(String name);
}
