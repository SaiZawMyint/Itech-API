package com.itech.api.form;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ProjectForm {

    @NotNull
    private String name;
    
    @NotNull
    private String clientId;
    
    @NotNull
    private String clientSecret;
    
    @NotNull
    private String projectId;
    
    @NotNull
    private String authURI;
    
    @NotNull
    private String tokenURI;
    
    @NotNull
    private String authProvider;
    
    @NotNull
    private List<String> redirectURIs;
}
