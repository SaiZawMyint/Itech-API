package com.itech.api.pkg.google;

import java.io.IOException;
import java.io.Serializable;
import java.security.GeneralSecurityException;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.itech.api.persistence.dto.ProjectDTO;
import com.itech.api.persistence.dto.TokenDTO;
import com.itech.api.pkg.spreadsheet.tools.Property;
import com.itech.api.pkg.tools.exceptions.AuthException;
import com.itech.api.pkg.toots.errors.Exception;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public abstract class GoogleCredentialManager implements Serializable {

    
    /**
     * 
     */
    private static final long serialVersionUID = -2801663923118303889L;
    
    private Object exceptions;
    private Throwable e;
    private String token;
    private TokenDTO tokenRes;
    private Credential credential;

    public GoogleCredentialManager(Property props) throws IOException, GeneralSecurityException, AuthException {
        this.credential = this.getGoogleCredential(props);
    }

    public GoogleCredentialManager(String token,TokenDTO tokenDTO, ProjectDTO project) throws IOException, GeneralSecurityException, AuthException {
        this.token = token;
        this.credential = this.getGoogleCredential(this.defaultProps(tokenDTO,project));
    }

    public GoogleCredentialManager(Throwable e) {
        this.e = e;
    }
    
    public Object getException() {
        this.exceptions = this.e instanceof GoogleJsonResponseException
                ? Exception.parseGoogleException((GoogleJsonResponseException) e)
                : this.e instanceof AuthException ? 
                        ((AuthException) this.e).toJson() :
                            this.e.getMessage();
        return this.exceptions;
    }
    
    private Property defaultProps(TokenDTO tokenResource, ProjectDTO project) {
        Property prop = new Property();
        prop.setToken(this.token);
        prop.setTokenResource(tokenResource);
        prop.setProject(project);
        return prop;
    }

    private Credential getGoogleCredential(Property props) throws IOException, GeneralSecurityException, AuthException {
        return GoogleConnection.connect(props);
    }
    
    public abstract Object getService() throws IOException, GeneralSecurityException, AuthException;
}
