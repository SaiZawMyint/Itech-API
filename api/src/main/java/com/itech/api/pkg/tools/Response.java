package com.itech.api.pkg.tools;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itech.api.pkg.tools.enums.ResponseCode;

public class Response {
    public static Entity<Object> send(ResponseCode code, boolean status) {
        return resolveResponse(code, code.getMessage(), null, status, null);
    }

    public static Entity<Object> send(Object data, ResponseCode code, boolean status) {
        return resolveResponse(code, code.getMessage(), data, status, null);
    }

    public static Entity<Object> send(Object data, ResponseCode code, boolean status,
            String message) {
        return resolveResponse(code, message, data, status, null);
    }

    public static Entity<Object> send(ResponseCode code, boolean status, Object error) {
        return resolveResponse(code, code.getMessage(), null, status, error);
    }

    static Entity<Object> resolveResponse(ResponseCode code, String message, Object data,
            boolean status, Object error) {
        Map<String, Object> response = new HashMap<>();
        response.put("ok", status);
        response.put("code", code);
        response.put("message", message);
        if (data != null)
            response.put("data", data);
        if (error != null)
            response.put("error", error);

        switch (code) {
            case EMPTY: {
                return new Entity<Object>(response, HttpStatus.NOT_FOUND);
            }
            case EMPTY_CONTENT: {
                return new Entity<Object>(response, HttpStatus.NO_CONTENT);
            }
            case DELETE: {
                return new Entity<Object>(response, HttpStatus.ACCEPTED);
            }
            case ERROR: {
                return new Entity<Object>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            case REQUIRED: {
                return new Entity<Object>(response, HttpStatus.BAD_REQUEST);
            }
            case SUCCESS: {
                return new Entity<Object>(response, HttpStatus.OK);
            }
            case UPDATE_SUCCESS: {
                return new Entity<Object>(response, HttpStatus.CREATED);
            }
            case UNAUTHORIZED: {
                return new Entity<Object>(response, HttpStatus.UNAUTHORIZED);
            }
            case BAD_REQUEST: {
                return new Entity<Object>(response, HttpStatus.BAD_REQUEST);
            }
            case REGIST_REQUEST_ACCEPT:{
                return new Entity<Object>(response, HttpStatus.CREATED);
            }
            case CREATED:{
                return new Entity<Object>(response, HttpStatus.CREATED);
            }
            case SPREADSHEET_CREATED:{
                return new Entity<Object>(response, HttpStatus.CREATED);
            }
            case SHEET_CREATED:{
                return new Entity<Object>(response, HttpStatus.CREATED);
            }
        default:
            break;
        }
        return null;
    }
    
    @SuppressWarnings("hiding")
    public
    static class Entity<Object> extends ResponseEntity<Object>{

        public Entity(HttpStatusCode status) {
            super(status);
        }
        public Entity(Object body, HttpStatus status) {
            super(body, status);
        }
        @Override
        public String toString() {
            String json = "";
            try {
                json = new ObjectMapper().writeValueAsString(super.getBody());
            }catch(Exception e) {
                json = "An unknow error occours!";
            }
            return json;
        }
    }

}
