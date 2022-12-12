package com.itech.api.persistence.dto;

import java.util.List;

import com.itech.api.persistence.entity.Project;
import com.itech.api.utils.CommonUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectDTO {
    private String client_id;
    private String project_id;
    private String auth_uri;
    private String token_uri;
    private String auth_provider_x509_cert_url;
    private String client_secret;
    private List<String> redirect_uris;
    
    public ProjectDTO(Project p) {
        this.client_id = p.getClientId();
        this.client_secret = p.getClientSecret();
        this.project_id = p.getProjectId();
        this.auth_uri = p.getAuthURI();
        this.auth_provider_x509_cert_url = p.getAuthProvider();
        this.redirect_uris = CommonUtils.convertStringTolist(p.getRedirectURIs(), ",");
        this.token_uri = p.getTokenURI();
    }
}
