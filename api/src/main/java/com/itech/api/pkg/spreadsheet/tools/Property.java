package com.itech.api.pkg.spreadsheet.tools;

import java.util.List;

import lombok.Data;

@Data
public class Property {

    private List<String> scope;
    private int port;
    private String callBack;
    private String storeTokenPath;
    private String clientSecretPath;
    private String accessType;

}
