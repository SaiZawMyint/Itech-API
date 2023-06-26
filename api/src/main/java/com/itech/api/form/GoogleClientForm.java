package com.itech.api.form;

import com.itech.api.persistence.entity.Project;
import com.itech.api.pkg.google.form.GoogleCredentialForm;
import com.itech.api.utils.CommonUtils;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class GoogleClientForm extends GoogleCredentialForm{
   
    public GoogleClientForm(Project p) {
        this.clientId = p.getClientId();
        this.clientsecret = p.getClientSecret();
        this.projectId = p.getProjectId();
        this.authUri = p.getAuthURI();
        this.tokenUri = p.getTokenURI();
        this.provider = p.getAuthProvider();
        this.redirectUris = CommonUtils.convertStringTolist(p.getRedirectURIs(), ",");
    }

}
