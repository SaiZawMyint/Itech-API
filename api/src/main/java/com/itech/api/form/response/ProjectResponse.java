package com.itech.api.form.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.itech.api.persistence.entity.Project;
import com.itech.api.utils.CommonUtils;

import lombok.Data;

@Data
public class ProjectResponse {
    
    @JsonInclude(Include.NON_NULL)
    private Integer id;
    
    @JsonInclude(Include.NON_NULL)
    private String name;

    @JsonInclude(Include.NON_NULL)
    private String clientId;

    @JsonInclude(Include.NON_NULL)
    private String clientSecret;

    @JsonInclude(Include.NON_NULL)
    private String projectId;

    @JsonInclude(Include.NON_NULL)
    private String authURI;

    @JsonInclude(Include.NON_NULL)
    private String tokenURI;

    @JsonInclude(Include.NON_NULL)
    private String authProvider;

    @JsonInclude(Include.NON_NULL)
    private List<String> redirectURIs;
    
    public ProjectResponse(Project project) {
        this.name = project.getName();
        this.id = project.getId();
        this.clientId = project.getClientId();
        this.clientSecret = project.getClientSecret();
        this.projectId = project.getProjectId();
        this.authURI = project.getAuthURI();
        this.tokenURI = project.getTokenURI();
        this.authProvider = project.getAuthProvider();
        this.redirectURIs = CommonUtils.convertStringTolist(project.getRedirectURIs(), ",");
    }
    
}
