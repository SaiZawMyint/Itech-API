package com.itech.api.persistence.dto;

import java.util.Date;

import com.itech.api.persistence.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Integer id;
    
    private String username;
    
    private String email;
    
    private String password;
    
    private boolean emailVerified;
    
    private boolean delFlag;
    
    private Date createdAt;
    
    private Date updatedAt;
    
    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.emailVerified = user.isEmailVerified();
        this.delFlag = user.isDelFlag();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }
    
}
