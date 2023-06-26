package com.itech.api.form;

import java.util.List;

import com.itech.api.persistence.entity.Project;
import com.itech.api.utils.CommonUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectForm {

    private String name;
    
    private String serviceType;
    
    private String clientId;
    
    private String clientSecret;
    
    private String projectId;
    
    private String authURI;
    
    private String tokenURI;
    
    private String authProvider;
    
    private List<String> redirectURIs;
    
    public ProjectForm(Project p) {
        this.name = p.getName();
        this.serviceType = p.getServiceType();
        this.clientId = p.getClientId();
        this.clientSecret = p.getClientSecret();
        this.projectId = p.getProjectId();
        this.authURI = p.getAuthURI();
        this.authProvider = p.getAuthProvider();
        this.redirectURIs = CommonUtils.convertStringTolist(p.getRedirectURIs(), ",");
        this.tokenURI = p.getTokenURI();
    }
}
