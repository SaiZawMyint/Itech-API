package com.itech.api.form.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenInfoResponse {

    @JsonInclude(Include.NON_NULL)
    private String issued_to;
    
    @JsonInclude(Include.NON_NULL)
    private String audience;
    
    @JsonInclude(Include.NON_NULL)
    private String scope;
    
    @JsonInclude(Include.NON_NULL)
    private Long expires_in;
    
    @JsonInclude(Include.NON_NULL)
    private String access_type;
    
}
