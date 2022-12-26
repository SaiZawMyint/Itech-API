package com.itech.api.bl.service.impl;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.itech.api.bl.service.GoogleDriveService;
import com.itech.api.bl.service.ProjectService;
import com.itech.api.persistence.dto.ProjectDTO;
import com.itech.api.persistence.dto.TokenDTO;
import com.itech.api.persistence.entity.Project;
import com.itech.api.pkg.drive.GoogleDriveManager;
import com.itech.api.pkg.tools.Response;
import com.itech.api.pkg.tools.enums.ResponseCode;
import com.itech.api.pkg.tools.exceptions.AuthException;
import com.itech.api.pkg.toots.errors.Exception;

import jakarta.transaction.Transactional;

@Service
public class GoogleDriveServiceImpl implements GoogleDriveService {

    @Autowired
    ProjectService projectService;

    @Transactional
    @Override
    public Object getDriveFiles(Integer pid, String access_token) {

        access_token = access_token == null ? this.getAccessTokenByPId(pid) : access_token;

        ProjectDTO project = new ProjectDTO(this.projectService.getProjectData(pid));
        GoogleDriveManager manager = this.getGoogleDriveManager(access_token,
                this.projectService.getTokenResources(pid), project);
        if (manager.getE() != null) {
            return Response.send(ResponseCode.UNAUTHORIZED, false, manager.getException());
        } else {
            Object data;
            try {
                data = manager.getDriveFiles();
                return data == null ? Response.send(ResponseCode.EMPTY, true)
                        : Response.send(data, ResponseCode.SUCCESS, true);
            } catch (IOException e) {
                e.printStackTrace();
                Object message = e instanceof GoogleJsonResponseException
                        ? Exception.parseGoogleException((GoogleJsonResponseException) e)
                        : e.getMessage();
                return Response.send(ResponseCode.ERROR, false, message);
            }
        }

    }

    @Transactional
    @Override
    public ResponseEntity<? extends Object> getDriveInfo(Integer pid, String access_token) {
        access_token = access_token == null ? this.getAccessTokenByPId(pid) : access_token;

        ProjectDTO project = new ProjectDTO(this.projectService.getProjectData(pid));
        GoogleDriveManager manager = this.getGoogleDriveManager(access_token,
                this.projectService.getTokenResources(pid), project);
        if (manager.getE() != null) {
            return Response.send(ResponseCode.UNAUTHORIZED, false, manager.getException());
        } else {
            Object data;
            try {
                data = manager.getDriveInfo();
                return data == null ? Response.send(ResponseCode.EMPTY, true)
                        : Response.send(data, ResponseCode.SUCCESS, true);
            } catch (IOException e) {
                e.printStackTrace();
                Object message = e instanceof GoogleJsonResponseException
                        ? Exception.parseGoogleException((GoogleJsonResponseException) e)
                        : e.getMessage();
                return Response.send(ResponseCode.ERROR, false, message);
            }
        }

    }

    private String getAccessTokenByPId(Integer pid) {
        Project p = this.projectService.getProjectData(pid);
        if (p.getToken() == null)
            return null;
        return p.getToken().getAccessToken();
    }

    private GoogleDriveManager getGoogleDriveManager(String token, TokenDTO tokenDTO, ProjectDTO project) {
        try {
            return new GoogleDriveManager(token, tokenDTO, project);
        } catch (IOException | GeneralSecurityException | AuthException e) {
            e.printStackTrace();
            return new GoogleDriveManager(e);
        }
    }
}
