package com.itech.api.bl.service.impl;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.sheets.v4.model.Sheet;
import com.itech.api.bl.service.SpreadsheetService;
import com.itech.api.form.ProjectForm;
import com.itech.api.form.SheetForm;
import com.itech.api.form.SpreadsheetForm;
import com.itech.api.form.response.ServiceRespose;
import com.itech.api.persistence.dto.TokenDTO;
import com.itech.api.persistence.entity.Project;
import com.itech.api.persistence.entity.Services;
import com.itech.api.persistence.entity.Token;
import com.itech.api.pkg.spreadsheet.SpreadsheetManager;
import com.itech.api.pkg.tools.Response;
import com.itech.api.pkg.tools.enums.ResponseCode;
import com.itech.api.pkg.tools.exceptions.AuthException;
import com.itech.api.pkg.toots.errors.Exception;
import com.itech.api.response.SpreadsheetResponse;
import com.itech.api.respositories.ProjectRepo;
import com.itech.api.respositories.ServiceRepo;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class SpreadsheetServiceImpl implements SpreadsheetService {
    
    @Autowired
    private ServiceRepo serviceRepo;
    
    @Autowired
    private ProjectRepo projectRepo;
    
    @Override
    public Object getSpreadSheetDocumentation() {
        try {
            Object data = SpreadsheetManager.getAPIDocumentation();
            return Response.send(data, ResponseCode.SUCCESS, true);
        } catch (IOException e) {
            return Response.send(ResponseCode.ERROR, false, e);
        }
    }

    @Override
    public Object getSpreadsheetData(Integer pid,String spreadsheetId, String accessToken) {
        if (spreadsheetId == null)
            return Response.send(ResponseCode.REQUIRED, false);
        System.out.println("is : => "+this.validateProject(pid));
        if(!this.validateProject(pid)) return Response.send(ResponseCode.ERROR, false,"Invalid project!");
        
        accessToken = accessToken == null ? this.getAccessToken(pid) : accessToken;
        Project proj = this.projectRepo.getById(pid);
        SpreadsheetManager manager = this.getSheetManger(accessToken, this.getTokenResources(pid),new ProjectForm(proj));
        if (manager.getE() != null) {
            return Response.send(ResponseCode.ERROR, false, manager.getException());
        } else {
            Object data;
            try {
                data = manager.getSpreadSheetData(spreadsheetId);
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

    @Override
    public Object updateSpreadsheet(Integer pid,String spreadsheetId, SpreadsheetForm form, String accessToken) {
        if (spreadsheetId == null)
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet id is required");
        if (form == null || (form != null && form.getName() == null))
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet name is required");
        if(!this.validateProject(pid)) return Response.send(ResponseCode.ERROR, false,"Invalid project!");
        
        Project proj = this.projectRepo.getById(pid);
        SpreadsheetManager manager = this.getSheetManger(accessToken, this.getTokenResources(pid),new ProjectForm(proj));
        if (manager.getE() != null) {
            return Response.send(ResponseCode.ERROR, false, manager.getException());
        } else {
            Object data;
            try {
                data = manager.updateSpreadsheet(spreadsheetId, form);
                return data == null ? Response.send(data, ResponseCode.EMPTY_CONTENT, true, "No changes.")
                        : Response.send(data, ResponseCode.UPDATE_SUCCESS, true);
            } catch (IOException e) {
                e.printStackTrace();
                Object message = e instanceof GoogleJsonResponseException
                        ? Exception.parseGoogleException((GoogleJsonResponseException) e)
                        : e.getMessage();
                return Response.send(ResponseCode.ERROR, false, message);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public Object createSpreadSheet(Integer pid,SpreadsheetForm form, String accessToken) {
        if (form == null || (form != null && form.getName() == null))
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet name is required");
        accessToken = accessToken == null ? this.getAccessToken(pid) : accessToken;
        if(!this.validateProject(pid)) return Response.send(ResponseCode.ERROR, false,"Invalid project!");
        Project proj = this.projectRepo.getById(pid);
        SpreadsheetManager manager = this.getSheetManger(accessToken, this.getTokenResources(pid),new ProjectForm(proj));
        if (manager.getE() != null) {
            return Response.send(ResponseCode.ERROR, false, manager.getException());
        } else {
            Object data;
            try {
                Project project = this.projectRepo.getById(pid);
                Services service = new Services();
                service.setName(form.getName());
                service.setType("SPREADSHEET");
                data = manager.createSpreadSheet(form);
                service.setRefId(((SpreadsheetResponse) data).getSpreadsheetId());
                service.setLink(((SpreadsheetResponse) data).getUrl());
                service.setProject(project);
                this.serviceRepo.save(service);
                return Response.send(data, ResponseCode.SUCCESS, true);
            } catch (IOException e) {
                e.printStackTrace();
                Object message = e instanceof GoogleJsonResponseException
                        ? Exception.parseGoogleException((GoogleJsonResponseException) e)
                        : e.getMessage();
                return Response.send(ResponseCode.ERROR, false, message);
            }
        }
        
    }

    @Override
    public Object addNewSheet(Integer pid,String spreadsheetId, SheetForm form, String accessToken) {
        if (spreadsheetId == null)
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet id is required");
        if (form == null || (form != null && form.getName() == null))
            return Response.send(ResponseCode.REQUIRED, false, "Sheet name is required");
        if(!this.validateProject(pid)) return Response.send(ResponseCode.ERROR, false,"Invalid project!");
        Project project = this.projectRepo.getById(pid);
        SpreadsheetManager manager = this.getSheetManger(accessToken, this.getTokenResources(pid),new ProjectForm(project));
        if (manager.getE() != null) {
            return Response.send(ResponseCode.ERROR, false, manager.getException());
        } else {
            Object data;
            try {
                data = manager.addNewSheet(spreadsheetId, form);
                return Response.send(data, ResponseCode.UPDATE_SUCCESS, true);
            } catch (IOException e) {
                e.printStackTrace();
                Object message = e instanceof GoogleJsonResponseException
                        ? Exception.parseGoogleException((GoogleJsonResponseException) e)
                        : e.getMessage();
                return Response.send(ResponseCode.ERROR, false, message);
            }
        }
    }

    @Override
    public Object getSheets(Integer pid,String spreadsheetId, String name, Integer id, String accessToken) {
        if(!this.validateProject(pid)) return Response.send(ResponseCode.ERROR, false,"Invalid project!");
        Project project = this.projectRepo.getById(pid);
        SpreadsheetManager manager = this.getSheetManger(accessToken, this.getTokenResources(pid),new ProjectForm(project));
        if (manager.getE() != null) {
            return Response.send(ResponseCode.ERROR, false, manager.getException());
        } else {
            List<Sheet> data;
            try {
                data = manager.getSheets(spreadsheetId, name, id);
                if (data.size() == 0) {
                    return Response.send(ResponseCode.EMPTY, true);
                }
                Map<String, Object> response = new HashMap<>();
                response.put("total", data.size());
                response.put("data", data);
                return Response.send(response, ResponseCode.SUCCESS, true);
            } catch (IOException e) {
                e.printStackTrace();
                Object message = e instanceof GoogleJsonResponseException
                        ? Exception.parseGoogleException((GoogleJsonResponseException) e)
                        : e.getMessage();
                return Response.send(ResponseCode.ERROR, false, message);
            }
        }
        
    }

    @Override
    public Object getSheet(Integer pid,String spreadsheetId, Integer sheetId, SheetForm form, String accessToken) {
        if (spreadsheetId == null || sheetId == null)
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet id or sheet id is required!");
        form = form == null ? new SheetForm() : form;
        if(!this.validateProject(pid)) return Response.send(ResponseCode.ERROR, false,"Invalid project!");
        Project project = this.projectRepo.getById(pid);
        SpreadsheetManager manager = this.getSheetManger(accessToken, this.getTokenResources(pid),new ProjectForm(project));
        if (manager.getE() != null) {
            return Response.send(ResponseCode.ERROR, false, manager.getException());
        } else {
            Object data;
            try {
                data = manager.getSheet(spreadsheetId, sheetId, form);
                if (data == null)
                    return Response.send(ResponseCode.EMPTY, true);
                return Response.send(data, ResponseCode.SUCCESS, true);
            } catch (IOException e) {
                e.printStackTrace();
                Object message = e instanceof GoogleJsonResponseException
                        ? Exception.parseGoogleException((GoogleJsonResponseException) e)
                        : e.getMessage();
                return Response.send(ResponseCode.ERROR, false, message);
            }
        }
    }

    @Override
    public Object updateSheet(Integer pid,String spreadsheetId, Integer sheetId, SheetForm form, String accessToken) {
        if(!this.validateProject(pid)) return Response.send(ResponseCode.ERROR, false,"Invalid project!");
        Project project = this.projectRepo.getById(pid);
        SpreadsheetManager manager = this.getSheetManger(accessToken, this.getTokenResources(pid),new ProjectForm(project));
        if (manager.getE() != null) {
            return Response.send(ResponseCode.ERROR, false, manager.getException());
        } else {
            Object data;
            try {
                data = manager.updateSheet(spreadsheetId, sheetId, form);
                return Response.send(data, ResponseCode.UPDATE_SUCCESS, true);
            } catch (IOException e) {
                e.printStackTrace();
                Object message = e instanceof GoogleJsonResponseException
                        ? Exception.parseGoogleException((GoogleJsonResponseException) e)
                        : e.getMessage();
                return Response.send(ResponseCode.ERROR, false, message);
            }
        }
    }

    @Override
    public Object deleteRowsRequest(Integer pid,String spreadsheetId, Integer sheetId, Integer start, Integer end, String accessToken) {
        if (spreadsheetId == null)
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet id is required");
        if (sheetId == null)
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet id is required");
        if(!this.validateProject(pid)) return Response.send(ResponseCode.ERROR, false,"Invalid project!");
        start = start == null ? 0 : start;
        end = end == null ? 1 : end;

        if (start >= end)
            return Response.send(ResponseCode.ERROR, false, "Start index must greater than end index!");
        Project project = this.projectRepo.getById(pid);
        SpreadsheetManager manager = this.getSheetManger(accessToken, this.getTokenResources(pid),new ProjectForm(project));
        if (manager.getE() != null) {
            return Response.send(ResponseCode.ERROR, false, manager.getException());
        } else {
            Object data;
            try {
                data = manager.deleteRowsRequest(spreadsheetId, sheetId, start, end);
                return Response.send(data, ResponseCode.DELETE, true);
            } catch (IOException e) {
                e.printStackTrace();
                Object message = e instanceof GoogleJsonResponseException
                        ? Exception.parseGoogleException((GoogleJsonResponseException) e)
                        : e.getMessage();
                return Response.send(ResponseCode.ERROR, false, message);
            }
        }
        
    }

    @Override
    public Object deleteColumnsRequest(Integer pid,String spreadsheetId, Integer sheetId, Integer start, Integer end, String accessToken) {
        if (spreadsheetId == null)
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet id is required");
        if (sheetId == null)
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet id is required");
        if(!this.validateProject(pid)) return Response.send(ResponseCode.ERROR, false,"Invalid project!");
        start = start == null ? 0 : start;
        end = end == null ? 1 : end;

        if (start >= end)
            return Response.send(ResponseCode.ERROR, false, "Start index must greater than end index!");
        Project project = this.projectRepo.getById(pid);
        SpreadsheetManager manager = this.getSheetManger(accessToken, this.getTokenResources(pid),new ProjectForm(project));
        if (manager.getE() != null) {
            return Response.send(ResponseCode.ERROR, false, manager.getException());
        } else {
            Object data;
            try {
                data = manager.deleteColumnsRequest(spreadsheetId, sheetId, start, end);
                return Response.send(data, ResponseCode.DELETE, true);
            } catch (IOException e) {
                e.printStackTrace();
                Object message = e instanceof GoogleJsonResponseException
                        ? Exception.parseGoogleException((GoogleJsonResponseException) e)
                        : e.getMessage();
                return Response.send(ResponseCode.ERROR, false, message);
            }
        }
        
    }

    @SuppressWarnings("deprecation")
    @Override
    public Object deleteSheet(Integer pid,String spreadsheetId, Integer sheetId, String accessToken) {
        if (spreadsheetId == null)
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet id is required");
        if (sheetId == null)
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet id is required");
        if(!this.validateProject(pid)) return Response.send(ResponseCode.ERROR, false,"Invalid project!");
        Project project = this.projectRepo.getById(pid);
        SpreadsheetManager manager = this.getSheetManger(accessToken, this.getTokenResources(pid),new ProjectForm(project));
        if (manager.getE() != null) {
            return Response.send(ResponseCode.ERROR, false, manager.getException());
        } else {
            Object data;
            try {
                data = manager.deleteSheet(spreadsheetId, sheetId);
                return Response.send(data, ResponseCode.DELETE, true);
            } catch (IOException e) {
                Object message = e instanceof GoogleJsonResponseException
                        ? Exception.parseGoogleException((GoogleJsonResponseException) e)
                        : e.getMessage();
                return Response.send(ResponseCode.ERROR, false, message);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public Object getSpreadsheets(Integer pid, String access_token) {
        if(!this.validateProject(pid)) return Response.send(ResponseCode.ERROR, false,"Invalid project!");
        Project project = this.projectRepo.getById(pid);
        List<ServiceRespose> data = new ArrayList<>();
        for(Services s:project.getServices()) {
            if(s.getType().equalsIgnoreCase("SPREADSHEET")) {
                data.add(new ServiceRespose(s));
            }
        }
        if(data.size() == 0) return Response.send(ResponseCode.EMPTY, true);
        
        return Response.send(data, ResponseCode.SUCCESS, true);
    }

    @SuppressWarnings("deprecation")
    @Override
    public Object deleteSpreadsheet(Integer pid, String spreadsheetId, String accessToken) {
        if (spreadsheetId == null)
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet id is required");
        if(!this.validateProject(pid)) return Response.send(ResponseCode.ERROR, false,"Invalid project!");
        accessToken = accessToken == null ? this.getAccessToken(pid) : accessToken;
        Project project = this.projectRepo.getById(pid);
        Services service = null;
        if(project.getServices().size() > 0) {
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

    private SpreadsheetManager getSheetManger(String token, TokenDTO tokenDTO,ProjectForm form) {
        try {
            return new SpreadsheetManager(token,tokenDTO,form);
        } catch (IOException | GeneralSecurityException | AuthException e) {
            e.printStackTrace();
            return new SpreadsheetManager(e);
        }
    }

    @SuppressWarnings("deprecation")
    private TokenDTO getTokenResources(Integer pid) {
        Project p = this.projectRepo.getById(pid);
        Token t = p.getToken();
        return new TokenDTO(t);
    }
    
    @SuppressWarnings("deprecation")
    private String getAccessToken(Integer pid) {
        Project p = this.projectRepo.getById(pid);
        if(p == null ) return null;
        if(p.getToken() == null) return null;
        return p.getToken().getAccessToken();
    }

    private boolean validateProject(Integer pid) {
        try {
            Optional<Project> project = this.projectRepo.findById(pid);
            return !project.isEmpty();
        }catch(EntityNotFoundException e) {
            return false;
        }
    }
}
