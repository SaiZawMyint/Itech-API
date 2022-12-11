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
    private Integer id;

    private String accessToken;

    private String refreshToken;

    private Long expiresIn;

    private List<String> scope;

    private String tokenType;

    private String idToken;
    
    public TokenDTO(Token token) {
        this.id = token.getId();
        this.accessToken = token.getAccessToken();
        this.refreshToken = token.getRefreshToken();
        this.expiresIn = token.getExpiresIn();
        this.scope = CommonUtils.convertStringTolist(token.getScope(), ",") ;
        this.tokenType = token.getTokenType();
        this.idToken = token.getIdToken();
    }
}
