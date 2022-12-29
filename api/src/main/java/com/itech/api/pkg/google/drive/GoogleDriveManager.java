package com.itech.api.pkg.google.drive;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.About;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.itech.api.form.DriveFolderForm;
import com.itech.api.form.response.drive.DriveInfoResponse;
import com.itech.api.form.response.drive.FileResponse;
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

    public Map<String,Object> getDriveFileInfo(String fileId, Boolean files) throws IOException {
        Map<String, Object> response = new HashMap<>();
        response.put("file", this.driveService.files().get(fileId).execute());
        if(files!=null && files) {
            FileList lists = this.getDriveFolderFiles(fileId);
            List<File> filesLists = lists.getFiles();
            List<FileResponse> responseFiles = new ArrayList<>();
            for(File f:filesLists) {
                FileResponse fr = new FileResponse(f);
                fr.setType(this.getFileType(f.getMimeType()));
                responseFiles.add(fr);
            }
            response.put("children", responseFiles);
        }
        return response;
    }
    
    public FileList getDriveFolderFiles(String fileId) throws IOException{
        return this.driveService.files().list().setQ("'"+fileId+"' in parents").execute();
    }
    
    private Drive getDriveService() throws IOException, GeneralSecurityException, AuthException {
        return (Drive) this.getService();
    }

    @Override
    public Object getService() throws IOException, GeneralSecurityException, AuthException {
        return new Drive.Builder(GoogleNetHttpTransport.newTrustedTransport(), FACTORY, this.getCredential())
                .setApplicationName("Google Sheet API").build();
    }

    private String getFileType(String mimeType) {
        
        switch(mimeType){
        
        case "application/vnd.android.package-archive":{
            return "archived";
        }
        case "application/vnd.google-apps.audio":{
            return "audio";
        }
        case "application/vnd.google-apps.drawing":{
            return "drawing";
        }
        case "application/vnd.google-apps.drive-sdk":{
            return "sdk";
        }
        case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":{
            return "xlsx";
        }
        case "application/vnd.google-apps.file":{
            return "drive-mime";
        }
        case "application/vnd.google-apps.folder":{
            return "drive-folder";
        }
        case "application/vnd.google-apps.form":{
            return "drive-mime";
        }
        case "application/vnd.google-apps.fusiontable":{
            return "drive-mime";
        }
        case "application/vnd.google-apps.document":{
            return "docx";
        }
        case "application/vnd.google-apps.jam":{
            return "drive-mime";
        }
        case "application/vnd.google-apps.map":{
            return "drive-map";
        }
        case "application/pdf":{
            return "pdf";
        }
        case "application/vnd.google-apps.photo":{
            return "image";
        }
        case "application/vnd.google-apps.presentation":{
            return "presentation";
        }
        case "application/vnd.google-apps.script":{
            return "script";
        }
        case "application/vnd.google-apps.shortcut":{
            return "shortcut";
        }
        case "application/vnd.google-apps.site":{
            return "site";
        }
        case "application/vnd.google-apps.spreadsheet":{
            return "xlsx";
        }
        case "text/plain":{
            return "txt";
        }
        case "application/vnd.google-apps.unknown":{
            return "unknown";
        }
        case "application/vnd.google-apps.video":{
            return "video";
        }
        case "application/x-zip-compressed":{
            return "zip";
        }
        default:
            return "unknown";
        }
    }
}
