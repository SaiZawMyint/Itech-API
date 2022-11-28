package com.itech.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
public class Response {

    @JsonInclude(Include.NON_NULL)
    private Integer total;
    @JsonInclude(Include.NON_NULL)
    private Object changes;
    @JsonInclude(Include.NON_NULL)
    private Object rowsEffects;
    @JsonInclude(Include.NON_NULL)
    private Object columnsEffects;
    
}
