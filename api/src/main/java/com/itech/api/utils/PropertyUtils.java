package com.itech.api.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itech.api.form.GoogleClientForm;

public class PropertyUtils {

    public static final String RESOURCES_PATH = PropertyUtils.class.getResource("/").getPath();
    public static final String CLIENT_SERCET_JSON = RESOURCES_PATH.concat("itech-google-client.json");
    
    @SuppressWarnings("unchecked")
    public static GoogleClientForm getGoogleClientData(){
        try {
            Map<String, Object> web = new ObjectMapper().readValue(new File(CLIENT_SERCET_JSON), Map.class);
            Map<String,Object> map = (Map<String,Object>) web.get("web");
            GoogleClientForm form = new GoogleClientForm();
            map.forEach((k,v)->{
                switch(k) {
                    case "client_id": form.setClientId((String)v);break;
                    case "project_id": form.setProjectId((String)v);break;
                    case "auth_uri": form.setAuthUri((String)v);break;
                    case "token_uri": form.setTokenUri((String)v);break;
                    case "auth_provider_x509_cert_url": form.setProvider((String)v);break;
                    case "client_secret": form.setClientsecret((String)v);break;
                    case "redirect_uris": form.setRedirectUris((List<String>)v);break;
                }
            });
            return form;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
}
