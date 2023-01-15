package com.itech.api.controller;

import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itech.api.bl.service.GoogleDriveService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
@RequestMapping("itech/api/")
public class MediaLoaderController {

    @Autowired
    GoogleDriveService driveService;
    
    @GetMapping(value="drive/{pid}/drivefile/video/{id}")
    public ResponseEntity<?> streammingDriveFileVideo(@PathVariable Integer pid, @PathVariable String id,
            @Nullable@RequestParam String access_token,@Nullable@RequestParam String contentRange,
            @Nullable@RequestParam String range,HttpServletRequest request){
        
        return this.driveService.steamingDrivefileVideo(pid,id,range,access_token);
    }
    
}
