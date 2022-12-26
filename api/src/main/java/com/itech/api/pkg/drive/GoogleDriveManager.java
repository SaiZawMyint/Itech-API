package com.itech.api.pkg.drive;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.About;
import com.itech.api.form.response.drive.DriveInfoResponse;
import com.itech.api.persistence.dto.ProjectDTO;
import com.itech.api.persistence.dto.TokenDTO;
import com.itech.api.pkg.google.GoogleCredentialManager;
import com.itech.api.pkg.tools.exceptions.AuthException;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleDriveManager extends GoogleCredentialManager{

    /**
     * 
     */
    private static final long serialVersionUID = -285632212848906201L;
    
    private static final JacksonFactory FACTORY = JacksonFactory.getDefaultInstance();
    
    private Drive driveService;

    public GoogleDriveManager(String token, TokenDTO tokenDTO, ProjectDTO project)
            throws IOException, GeneralSecurityException, AuthException {
        super(token, tokenDTO, project);
        this.driveService = getDriveService();
    }

    public GoogleDriveManager(Throwable e) {
        super(e);
    }
    
    public Object getDriveFiles() throws IOException {
        return this.driveService.files().list().setQ("mimeType='application/vnd.google-apps.folder'").setFields("nextPageToken, files(id,name,kind)").execute();
    }
    
    public Object getDriveInfo() throws IOException  {
        About about = this.driveService.about().get().setFields("user,storageQuota").execute();
        DriveInfoResponse response = new DriveInfoResponse(about);
        System.out.println(response);
        return response;
    }

    private Drive getDriveService() throws IOException, GeneralSecurityException, AuthException {
        return (Drive) this.getService();
    }

    @Override
    public Object getService() throws IOException, GeneralSecurityException, AuthException {
        return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), FACTORY, this.getCredential())
                .setApplicationName("Google Sheet API").build();
    }

}
