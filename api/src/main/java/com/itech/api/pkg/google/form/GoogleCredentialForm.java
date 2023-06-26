package com.itech.api.pkg.google.form;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoogleCredentialForm {

    protected String clientId;
    protected String clientsecret;
    protected String projectId;
    protected String authUri;
    protected String tokenUri;
    protected String provider;
    protected List<String> redirectUris;
    
}
