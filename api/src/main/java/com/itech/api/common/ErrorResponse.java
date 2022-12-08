package com.itech.api.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
public class ErrorResponse {

    @JsonInclude(Include.NON_NULL)
    private String error;
    @JsonInclude(Include.NON_NULL)
    private Integer code;
    
}
