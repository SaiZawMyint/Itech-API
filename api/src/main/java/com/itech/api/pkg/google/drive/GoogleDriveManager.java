package com.itech.api.pkg.google.drive;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.About;
import com.google.api.services.drive.model.File;
import com.itech.api.form.DriveFolderForm;
import com.itech.api.form.response.drive.DriveInfoResponse;
import com.itech.api.persistence.dto.ProjectDTO;
import com.itech.api.persistence.dto.TokenDTO;
import com.itech.api.pkg.google.GoogleCredentialManager;
import com.itech.api.pkg.google.drive.enums.DriveMIMEType;
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
    
    public File createDriveFolder(DriveFolderForm form) throws IOException {
        File file = new File();
        file.setName(form.getName());
        if(form.getDescription() != null)
            file.setDescription(form.getDescription());
        if(form.getParents() != null && form.getParents().size() > 0)
            file.setParents(form.getParents());
        
        file.setMimeType(DriveMIMEType.FOLDER.toString());
        File created = this.driveService.files().create(file).setFields("id").execute();
        return created;
    }
    
    public Object getDriveInfo() throws IOException  {
        About about = this.driveService.about().get().setFields("user,storageQuota").execute();
        DriveInfoResponse response = new DriveInfoResponse(about);
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
