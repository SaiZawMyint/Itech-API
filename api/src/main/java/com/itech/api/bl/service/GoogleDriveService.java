package com.itech.api.bl.service;

import org.springframework.http.ResponseEntity;

import com.itech.api.form.DriveFolderForm;

public interface GoogleDriveService {

    public Object getDriveFiles(Integer pid, String access_token);

    public ResponseEntity<? extends Object> getDriveInfo(Integer pid, String access_token);

    public ResponseEntity<? extends Object> createFolder(Integer pid, DriveFolderForm form, String access_token);

    public ResponseEntity<? extends Object> getDriveFolders(Integer pid, String access_token);

    public ResponseEntity<? extends Object> getDriveFile(Integer pid, String id, Boolean files, String access_token);

    public ResponseEntity<? extends Object> importFolder(Integer pid, DriveFolderForm form, String access_token);
    
}
