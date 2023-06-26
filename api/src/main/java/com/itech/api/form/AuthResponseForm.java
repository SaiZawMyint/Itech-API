package com.itech.api.form;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseForm {
    private String email;
    private String accessToken;
}
