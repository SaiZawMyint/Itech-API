package com.itech.api.form;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserForm {

    final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    private Integer id;
    
    @NotNull
    private String username;

    @NotNull
    private String email;

    @NotNull
    @Size(min = 6)
    private String password;

    private String profile;

    private boolean emailVerified;
    
    @NotNull
    private Integer role;
    
    public String getPassword() {
        return passwordEncoder.encode(this.password);
    }
}
