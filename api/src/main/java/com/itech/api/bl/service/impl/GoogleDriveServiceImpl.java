package com.itech.api.bl.service.impl;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.drive.model.File;
import com.itech.api.bl.service.GoogleDriveService;
import com.itech.api.bl.service.ProjectService;
import com.itech.api.form.DriveFolderForm;
import com.itech.api.form.response.ServiceRespose;
import com.itech.api.form.response.drive.DownloadResponse;
import com.itech.api.persistence.dto.ProjectDTO;
import com.itech.api.persistence.dto.TokenDTO;
import com.itech.api.persistence.entity.Project;
import com.itech.api.persistence.entity.Services;
import com.itech.api.persistence.entity.Token;
import com.itech.api.pkg.google.drive.GoogleDriveManager;
import com.itech.api.pkg.google.drive.enums.DriveMIMEType;
import com.itech.api.pkg.tools.Response;
import com.itech.api.pkg.tools.enums.ResponseCode;
import com.itech.api.pkg.tools.exceptions.AuthException;
import com.itech.api.pkg.toots.errors.Exception;
import com.itech.api.respositories.ProjectRepo;
import com.itech.api.respositories.ServiceRepo;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@Transactional
@Service
public class GoogleDriveServiceImpl implements GoogleDriveService {

    @Autowired
    ProjectService projectService;
    
    @Autowired
    ProjectRepo projectRepo;
    
    @Autowired
    ServiceRepo serviceRepo;

    @SuppressWarnings("unchecked")
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
                Object message = e.getMessage();
                if(e instanceof GoogleJsonResponseException) {
                    message = Exception.parseGoogleException((GoogleJsonResponseException) e);
                    Integer status = (Integer) ((Map<Object, Object>) message).get("statusCode");
                    String errorMsg = (String) ((Map<Object, Object>) message).get("message");
                    return Response.send(status, false, errorMsg, message);
                }
                return Response.send(ResponseCode.ERROR, false, message);
            }
        }

    }

    @SuppressWarnings("unchecked")
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
                Object message = e.getMessage();
                if(e instanceof GoogleJsonResponseException) {
                    message = Exception.parseGoogleException((GoogleJsonResponseException) e);
                    Integer status = (Integer) ((Map<Object, Object>) message).get("statusCode");
                    String errorMsg = (String) ((Map<Object, Object>) message).get("message");
                    return Response.send(status, false, errorMsg, message);
                }
                return Response.send(ResponseCode.ERROR, false, message);
            }
        }

    }

    @SuppressWarnings({ "deprecation", "unchecked" })
    @Override
    public ResponseEntity<? extends Object> createFolder(Integer pid, DriveFolderForm form, String access_token) {
        if(form == null) Response.send(ResponseCode.REQUIRED, false,"Body query is required!");
        access_token = access_token == null ? this.getAccessTokenByPId(pid) : access_token;
        ProjectDTO project = new ProjectDTO(this.projectService.getProjectData(pid));
        GoogleDriveManager manager = this.getGoogleDriveManager(access_token,
                this.projectService.getTokenResources(pid), project);
        if (manager.getE() != null) {
            return Response.send(ResponseCode.UNAUTHORIZED, false, manager.getException());
        } else {
            File data;
            try {
                data = manager.createDriveFolder(form);
                Project proj = this.projectRepo.getById(pid);
                Services service = new Services();
                service.setName(form.getName());
                service.setType("DRIVE");
                service.setRefId(data.getId());
                service.setLink(this.createDriveFolderLink(data.getId()));
                service.setProject(proj);
                this.serviceRepo.save(service);
                return data == null ? Response.send(ResponseCode.EMPTY, true)
                        : Response.send(new ServiceRespose(service), ResponseCode.SUCCESS, true);
            } catch (IOException e) {
                e.printStackTrace();
                Object message = e.getMessage();
                if(e instanceof GoogleJsonResponseException) {
                    message = Exception.parseGoogleException((GoogleJsonResponseException) e);
                    Integer status = (Integer) ((Map<Object, Object>) message).get("statusCode");
                    String errorMsg = (String) ((Map<Object, Object>) message).get("message");
                    return Response.send(status, false, errorMsg, message);
                }
                return Response.send(ResponseCode.ERROR, false, message);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public ResponseEntity<? extends Object> getDriveFolders(Integer pid, String access_token) {
        if(!this.validateProject(pid)) return Response.send(ResponseCode.REQUIRED_AUTH, false,"Invalid project!");
        Project project = this.projectRepo.getById(pid);
        if(project.getToken() == null) {
            return Response.send(ResponseCode.REQUIRED_AUTH, false,"Invalid credential!");
        }
        List<ServiceRespose> data = new ArrayList<>();
        for(Services s:project.getServices()) {
            if(s.getType().equalsIgnoreCase("DRIVE")) {
                data.add(new ServiceRespose(s));
            }
        }
        if(data.size() == 0) return Response.send(ResponseCode.EMPTY, true);
        
        return Response.send(data, ResponseCode.SUCCESS, true);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ResponseEntity<? extends Object> getDriveFile(Integer pid, String id, Boolean files, String access_token) {
        if(pid == null) Response.send(ResponseCode.REQUIRED, false,"Project id is required!");
        if(!this.validateProject(pid)) return Response.send(ResponseCode.ERROR, false,"Invalid project!");
        
        access_token = access_token == null ? this.getAccessTokenByPId(pid) : access_token;
        ProjectDTO project = new ProjectDTO(this.projectService.getProjectData(pid));
        GoogleDriveManager manager = this.getGoogleDriveManager(access_token,
                this.projectService.getTokenResources(pid), project);
        if (manager.getE() != null) {
            return Response.send(ResponseCode.UNAUTHORIZED, false, manager.getException());
        } else {
            Object data;
            try {
                data = manager.getDriveFileInfo(id,files);
                return data == null ? Response.send(ResponseCode.EMPTY, true)
                        : Response.send(data, ResponseCode.SUCCESS, true);
            } catch (IOException e) {
                e.printStackTrace();
                Object message = e.getMessage();
                if(e instanceof GoogleJsonResponseException) {
                    message = Exception.parseGoogleException((GoogleJsonResponseException) e);
                    Integer status = (Integer) ((Map<Object, Object>) message).get("statusCode");
                    String errorMsg = (String) ((Map<Object, Object>) message).get("message");
                    return Response.send(status, false, errorMsg, message);
                }
                return Response.send(ResponseCode.ERROR, false, message);
            }
        }
    }

    @SuppressWarnings({ "deprecation", "unchecked" })
    @Override
    public ResponseEntity<? extends Object> importFolder(Integer pid, DriveFolderForm form, String access_token) {
        if(form == null) Response.send(ResponseCode.REQUIRED, false,"Body query is required!");
        if(form.getId() == null) Response.send(ResponseCode.REQUIRED, false,"Folder id is required!");
        
        access_token = access_token == null ? this.getAccessTokenByPId(pid) : access_token;
        ProjectDTO project = new ProjectDTO(this.projectService.getProjectData(pid));
        GoogleDriveManager manager = this.getGoogleDriveManager(access_token,
                this.projectService.getTokenResources(pid), project);
        if (manager.getE() != null) {
            return Response.send(ResponseCode.UNAUTHORIZED, false, manager.getException());
        } else {
            Map<String, Object> filedata;
            try {
                filedata = manager.getDriveFileInfo(form.getId(),null);
                File data = (File) filedata.get("file");
                if(!this.isFolder(data.getMimeType())) {
                    return Response.send(ResponseCode.BAD_REQUEST, false,"Sorry, this is not a drive folder!");
                }else {
                    Project proj = this.projectRepo.getById(pid);
                    Services service = new Services();
                    service.setName(data.getName());
                    service.setType("DRIVE");
                    service.setRefId(data.getId());
                    service.setLink(this.createDriveFolderLink(data.getId()));
                    service.setProject(proj);
                    this.serviceRepo.save(service);
                    return Response.send(new ServiceRespose(service), ResponseCode.DRIVE_FOLDER_IMPORT, true);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Object message = e.getMessage();
                if(e instanceof GoogleJsonResponseException) {
                    message = Exception.parseGoogleException((GoogleJsonResponseException) e);
                    Integer status = (Integer) ((Map<Object, Object>) message).get("statusCode");
                    String errorMsg = (String) ((Map<Object, Object>) message).get("message");
                    return Response.send(status, false, errorMsg, message);
                }
                return Response.send(ResponseCode.ERROR, false, message);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public ResponseEntity<?> downloadDriveFile(Integer pid, String id, String access_token, HttpServletResponse responses) {
        if(pid == null) Response.send(ResponseCode.REQUIRED, false,"Project id is required!");
        if(!this.validateProject(pid)) return Response.send(ResponseCode.ERROR, false,"Invalid project!");
        if(id == null) Response.send(ResponseCode.REQUIRED, false,"File id is required!");
        
        access_token = access_token == null ? this.getAccessTokenByPId(pid) : access_token;

        ProjectDTO project = new ProjectDTO(this.projectService.getProjectData(pid));
        GoogleDriveManager manager = this.getGoogleDriveManager(access_token,
                this.projectService.getTokenResources(pid), project);
        if (manager.getE() != null) {
            return Response.send(ResponseCode.UNAUTHORIZED, false, manager.getException());
        } else {
            DownloadResponse data;
            try {
                data = manager.downloadDriveFile(id);
                return data == null ? Response.send(ResponseCode.EMPTY, true)
                        : Response.send(data, ResponseCode.DOWNLOAD, true);
            } catch (IOException e) {
                e.printStackTrace();
                Object message = e.getMessage();
                if(e instanceof GoogleJsonResponseException) {
                    message = Exception.parseGoogleException((GoogleJsonResponseException) e);
                    Integer status = (Integer) ((Map<Object, Object>) message).get("statusCode");
                    String errorMsg = (String) ((Map<Object, Object>) message).get("message");
                    return Response.send(status, false, errorMsg, message);
                }
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
    @SuppressWarnings("deprecation")
    private TokenDTO getTokenResources(Integer pid) {
        Project p = this.projectRepo.getById(pid);
        Token t = p.getToken();
        if(t == null) return null;
        return new TokenDTO(t);
    }
    private boolean validateProject(Integer pid) {
        try {
            Optional<Project> project = this.projectRepo.findById(pid);
            return !project.isEmpty() && this.getTokenResources(pid) != null;
        }catch(EntityNotFoundException e) {
            return false;
        }
    }
    
    private String createDriveFolderLink(String pathId) {
        StringBuffer uri = new StringBuffer("https://drive.google.com/drive/folders/");
        return uri.append(pathId).toString();
    }
    
    private boolean isFolder(String mimeType) {
        return DriveMIMEType.FOLDER.toString().equals(mimeType);
    }
}
