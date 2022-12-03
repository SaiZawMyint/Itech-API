package com.itech.api.pkg.tools.exceptions;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itech.api.pkg.tools.Response;
import com.itech.api.pkg.tools.enums.ResponseCode;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class AuthException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -3186767276848661848L;

    public AuthException() {
        super();
    }

    public AuthException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthException(String message) {
        super(message);
    }

    public AuthException(Throwable cause) {
        super(cause);
    }

    public AuthException(Map<String, Object> causes) throws JsonProcessingException {
        super(exceptionCauses(causes));
    }

    public AuthException(ResponseCode code, @NonNull Object referCauses) {
        super(exceptionResponse(code, referCauses));
    }

    private static String exceptionCauses(Map<String, Object> causes) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(causes);
    }
    
    private static String exceptionResponse(ResponseCode code,Object referCauses) {
        return Response.send(code, false, referCauses).toString();
    }
    
    public Object toJson() {
        Object bodyString = null;
        try {
            bodyString = new ObjectMapper().readValue(super.getMessage(), Map.class);
        }catch(Exception e) {
            bodyString = e;
        }
        return bodyString;
    }
}
