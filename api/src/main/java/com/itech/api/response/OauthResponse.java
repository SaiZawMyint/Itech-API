package com.itech.api.response;

import lombok.Data;

@Data
public class OauthResponse {
    private String access_token;
    private String refresh_token;
    private String expires_in;
    private String scope;
    private String token_type;
    private String id_token;
}
