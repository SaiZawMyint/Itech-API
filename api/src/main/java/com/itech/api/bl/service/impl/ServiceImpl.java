package com.itech.api.bl.service.impl;

import com.itech.api.bl.service.Service;
import com.itech.api.persistence.dto.TokenDTO;
import com.itech.api.persistence.entity.Project;
import com.itech.api.persistence.entity.Services;
import com.itech.api.persistence.entity.Token;
import com.itech.api.pkg.tools.Response;
import com.itech.api.pkg.tools.enums.ResponseCode;
import com.itech.api.respositories.ProjectRepo;
import com.itech.api.respositories.ServiceRepo;
import com.itech.api.utils.CommonUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
@Transactional
public class ServiceImpl implements Service {
    @Autowired
    ProjectRepo projectRepo;
    @Autowired
    ServiceRepo serviceRepo;

    @Value("${app.api.services}")
    String services;

    @Override
    public Object deleteProjectService(Integer pid, String spreadsheetId, Boolean includeResource, String serviceType, String access_token) {
        if(serviceType == null || serviceType.isEmpty()) return Response.send(ResponseCode.REQUIRED, false, "Service type is required");
        if(!services.contains(serviceType)) return Response.send(ResponseCode.BAD_REQUEST, false, "Invalid service type!");

        String serviceName = this.capitalizeServiceName(serviceType);

        if (spreadsheetId == null)
            return Response.send(ResponseCode.REQUIRED, false, serviceName + " id is required");
        if(!this.validateProject(pid)) return Response.send(ResponseCode.ERROR, false,"Invalid project!");
        access_token = access_token == null ? this.getAccessTokenByPId(pid) : access_token;
        Project project = this.projectRepo.getById(pid);
        Services service = null;
        if(!project.getServices().isEmpty()) {
            for(Services s: project.getServices()) {
                if(s.getRefId().equals(spreadsheetId)) {
                    service = s;
                    break;
                }
            }
        }
        this.serviceRepo.delete(service);
        return Response.send(ResponseCode.DELETE, true);
    }
    private String getAccessTokenByPId(Integer pid) {
        Project p = this.projectRepo.getById(pid);
        if(p == null ) return null;
        if(p.getToken() == null) return null;
        return p.getToken().getAccessToken();
    }

    private boolean validateProject(Integer pid) {
        try {
            Optional<Project> project = this.projectRepo.findById(pid);
            return !project.isEmpty() && this.getTokenResources(pid) != null;
        }catch(EntityNotFoundException e) {
            return false;
        }
    }
    private TokenDTO getTokenResources(Integer pid) {
        Project p = this.projectRepo.getById(pid);
        Token t = p.getToken();
        if(t == null) return null;
        return new TokenDTO(t);
    }
    private String capitalizeServiceName(String service){
        return service.toLowerCase().substring(0,1).toLowerCase() + service.substring(1);
    }
}
