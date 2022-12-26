package com.itech.api.bl.service;

import org.springframework.http.ResponseEntity;

public interface GoogleDriveService {

    public Object getDriveFiles(Integer pid, String access_token);

    public ResponseEntity<? extends Object> getDriveInfo(Integer pid, String access_token);

}
