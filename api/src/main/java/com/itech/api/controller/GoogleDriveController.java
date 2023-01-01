package com.itech.api.controller;

import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itech.api.bl.service.GoogleDriveService;
import com.itech.api.form.DriveFolderForm;

@RestController
@RequestMapping("/itech/api/drive")
@CrossOrigin
public class GoogleDriveController {

    @Autowired
    GoogleDriveService googleDriveService;
    
    @SuppressWarnings("unchecked")
    @GetMapping("/{pid}/files")
    public ResponseEntity<?> getDriveFiles(@PathVariable Integer pid,@Nullable @RequestParam String access_token){
        return (ResponseEntity<? extends Object>) this.googleDriveService.getDriveFiles(pid,access_token);
    }
    
    @GetMapping("/{pid}/info")
    public ResponseEntity<?> getDriveInfo(@PathVariable Integer pid,@Nullable @RequestParam String access_token){
        return (ResponseEntity<? extends Object>) this.googleDriveService.getDriveInfo(pid,access_token);
    }
    
    @GetMapping("/{pid}/folders")
    public ResponseEntity<?> getDriveFoldersInfo(@PathVariable Integer pid,@Nullable @RequestParam String access_token){
        return (ResponseEntity<? extends Object>) this.googleDriveService.getDriveFolders(pid,access_token);
    }
    
    @PostMapping("/{pid}/folders/create")
    public ResponseEntity<?> createFolder(@PathVariable Integer pid,@RequestBody DriveFolderForm form,@Nullable @RequestParam String access_token){
        return (ResponseEntity<? extends Object>) this.googleDriveService.createFolder(pid,form, access_token);
    }
    

    @PostMapping("/{pid}/folders/import")
    public ResponseEntity<?> importFolder(@PathVariable Integer pid,@RequestBody DriveFolderForm form,@Nullable @RequestParam String access_token){
        return (ResponseEntity<? extends Object>) this.googleDriveService.importFolder(pid,form, access_token);
    }
    
    @GetMapping("/{pid}/drivefile/{id}")
    public ResponseEntity<?> getDriveFile(@PathVariable Integer pid, @PathVariable String id,@Nullable@RequestParam Boolean files,@Nullable String access_token){
        return (ResponseEntity<? extends Object>) this.googleDriveService.getDriveFile(pid,id,files,access_token);
    }
    
    @GetMapping("/{pid}/drivefile/{id}/download")
    public ResponseEntity<?> downloadDriveFile(@PathVariable Integer pid, @PathVariable String id,
            /* HttpServletResponse response, */@Nullable String access_token){
        return this.googleDriveService.downloadDriveFile(pid,id,access_token, null);
    }
}
