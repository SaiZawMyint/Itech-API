package com.itech.api.form.response;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.itech.api.persistence.entity.User;

import lombok.Data;

@Data
public class UserResponse implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -3485520234825306776L;

    @JsonInclude(Include.NON_NULL)
    private Integer id;

    @JsonInclude(Include.NON_NULL)
    private String username;

    @JsonInclude(Include.NON_NULL)
    private String email;

    @JsonInclude(Include.NON_NULL)
    private String password;
    
    @JsonInclude(Include.NON_NULL)
    private String profile;

    @JsonInclude(Include.NON_NULL)
    private boolean emailVerified;
    
    @JsonInclude(Include.NON_NULL)
    private Date createdAt;

    @JsonInclude(Include.NON_NULL)
    private Date updatedAt;
    
    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.profile = user.getProfile();
        this.emailVerified = user.isEmailVerified();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }

}
