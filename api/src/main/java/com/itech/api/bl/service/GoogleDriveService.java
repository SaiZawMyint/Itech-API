package com.itech.api.bl.service;

import org.springframework.http.ResponseEntity;

import com.itech.api.form.DriveFolderForm;

import jakarta.servlet.http.HttpServletResponse;

public interface GoogleDriveService {

    Object getDriveFiles(Integer pid, String access_token);

    ResponseEntity<? extends Object> getDriveInfo(Integer pid, String access_token);

    ResponseEntity<? extends Object> createFolder(Integer pid, DriveFolderForm form, String access_token);

    ResponseEntity<? extends Object> getDriveFolders(Integer pid, String access_token);

    ResponseEntity<? extends Object> getDriveFile(Integer pid, String id, Boolean files, String access_token);

    ResponseEntity<? extends Object> importFolder(Integer pid, DriveFolderForm form, String access_token);

    ResponseEntity<?> downloadDriveFile(Integer pid, String id, String access_token, HttpServletResponse response);

    ResponseEntity<?> getDriveFileInformation(Integer pid, String id, String access_token);

    ResponseEntity<?> steamingDriveFileVideo(Integer pid, String id, String contentRange, String access_token);

    ResponseEntity<?> photoViewer(Integer pid, String id, String accessToken);

    ResponseEntity<?> deleteDriveProject(Integer pid, String id);

    ResponseEntity<?> getAllAccessibleFolders(Integer pid, String accessToken, String nextPageToken);
}
