package com.itech.api.form;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthRequestForm {

    @NotNull
    private String email;
    @NotNull
    @Size(min = 6)
    private String password;
    
}
