package com.itech.api.form;

import java.util.List;

import lombok.Data;

@Data
public class GoogleClientForm {
    private String clientId;
    private String clientsecret;
    private String projectId;
    private String authUri;
    private String tokenUri;
    private String provider;
    private List<String> redirectUris;
}
