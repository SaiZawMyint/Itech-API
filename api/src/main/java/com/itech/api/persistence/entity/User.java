package com.itech.api.persistence.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "user")
@Data
public class User {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column
    private String username;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private Integer type;

    @Column
    private String profile;

    @Column(name = "email_verified")
    private boolean emailVerified;

    @Column(name = "del_flag")
    private boolean delFlag;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;
}
