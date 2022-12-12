package com.itech.api.pkg.toots.errors;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;

public class Exception {
	public static Object parseGoogleException(GoogleJsonResponseException e) {
        try {
            String jsonString = e.getDetails() != null ? e.getDetails().toPrettyString() : e.getMessage();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonString, Map.class);
        } catch (IOException e1) {
            e1.printStackTrace();
            return e.getMessage();
        }
    }
    
}
