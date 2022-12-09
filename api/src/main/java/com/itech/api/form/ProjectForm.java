package com.itech.api.form;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ProjectForm {

    private String name;
    
    private String clientId;
    
    private String clientSecret;
    
    private String projectId;
    
    private String authURI;
    
    private String tokenURI;
    
    private String authProvider;
    
    private List<String> redirectURIs;
}
