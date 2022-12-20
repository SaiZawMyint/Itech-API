package com.itech.api.pkg.drive;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.itech.api.persistence.dto.ProjectDTO;
import com.itech.api.persistence.dto.TokenDTO;
import com.itech.api.pkg.google.GoogleConnection;
import com.itech.api.pkg.spreadsheet.tools.Property;
import com.itech.api.pkg.tools.exceptions.AuthException;
import com.itech.api.pkg.toots.errors.Exception;

public class DriveManager {


    private static final JacksonFactory FACTORY = JacksonFactory.getDefaultInstance();

    public Sheets Drives;
    public Property prop;
    
    private Object exceptions;
    private Throwable e;
    private String token;

    public DriveManager(Property props) throws IOException, GeneralSecurityException, AuthException {
        this.Drives = this.getService(props);
    }

    public DriveManager(String token,TokenDTO tokenDTO, ProjectDTO project) throws IOException, GeneralSecurityException, AuthException {
        this.token = token;
        this.Drives = this.getService(this.defaultProps(tokenDTO,project));
    }

    public DriveManager(Throwable e) {
        this.e = e;
    }
    
    private Property defaultProps(TokenDTO tokenResource, ProjectDTO project) {
        Property prop = new Property();
        prop.setToken(this.token);
        prop.setTokenResource(tokenResource);
        prop.setProject(project);
        return prop;
    }
    
    public Object getException() {
        this.exceptions = this.e instanceof GoogleJsonResponseException
                ? Exception.parseGoogleException((GoogleJsonResponseException) e)
                : this.e instanceof AuthException ? 
                        ((AuthException) this.e).toJson() :
                            this.e.getMessage();
        return this.exceptions;
    }
    
    private Sheets getService(Property props) throws IOException, GeneralSecurityException, AuthException {
        Credential credential = GoogleConnection.connect(props);
        
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), FACTORY, credential)
                .setApplicationName("Google Drive API").build();
    }
}
