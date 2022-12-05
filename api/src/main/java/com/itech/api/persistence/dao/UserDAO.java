package com.itech.api.persistence.dao;

import java.util.Date;

import com.itech.api.persistence.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDAO {

    private Integer id;

    private String username;

    private String email;

    private String password;


    private String profile;

    private boolean emailVerified;

    private boolean delFlag;

    private Date createdAt;

    private Date updatedAt;

    public UserDAO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.profile = user.getProfile();
        this.emailVerified = user.isEmailVerified();
        this.delFlag = user.isDelFlag();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }

}
