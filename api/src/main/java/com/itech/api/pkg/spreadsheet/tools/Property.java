package com.itech.api.pkg.spreadsheet.tools;

import java.util.List;

import com.itech.api.persistence.dto.TokenDTO;

import lombok.Data;

@Data
public class Property {
    private List<String> scope;
    private int port;
    private String callBack;
    private String storeTokenPath;
    private String clientSecretPath;
    private String accessType;
    private String token;
    private TokenDTO tokenResource;
}
