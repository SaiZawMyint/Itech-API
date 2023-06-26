package com.itech.api.persistence.dto;

import java.util.List;

import com.itech.api.persistence.entity.Token;
import com.itech.api.utils.CommonUtils;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class TokenDTO {

    private String access_token;

    private String refresh_token;

    private Long expires_in;

    private List<String> scope;

    private String token_type;

    private String id_token;
    
    public TokenDTO(Token token) {
        this.access_token = token.getAccessToken();
        this.refresh_token = token.getRefreshToken();
        this.expires_in = token.getExpiresIn();
        this.scope = CommonUtils.convertStringTolist(token.getScope(), ",") ;
        this.token_type = token.getTokenType();
        this.id_token = token.getIdToken();
    }
}
