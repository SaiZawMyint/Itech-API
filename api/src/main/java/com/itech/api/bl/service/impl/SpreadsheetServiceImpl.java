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
import com.itech.api.form.SheetForm;
import com.itech.api.form.SpreadsheetForm;
import com.itech.api.form.response.ServiceRespose;
import com.itech.api.persistence.dto.ProjectDTO;
import com.itech.api.persistence.dto.TokenDTO;
import com.itech.api.persistence.entity.Project;
import com.itech.api.persistence.entity.Services;
import com.itech.api.persistence.entity.Token;
import com.itech.api.pkg.spreadsheet.SpreadsheetManager;
import com.itech.api.pkg.tools.Response;
import com.itech.api.pkg.tools.enums.ResponseCode;
import com.itech.api.pkg.tools.exceptions.AuthException;
import com.itech.api.pkg.toots.errors.Exception;
import com.itech.api.pkg.webclient.HttpRestClient;
import com.itech.api.response.SheetResponse;
import com.itech.api.response.SpreadsheetResponse;
import com.itech.api.respositories.ProjectRepo;
import com.itech.api.respositories.ServiceRepo;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
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

    @SuppressWarnings("deprecation")
    @Override
    public Object getSpreadsheetData(Integer pid,String spreadsheetId, String accessToken) {
        if (spreadsheetId == null)
            return Response.send(ResponseCode.REQUIRED, false);
        if(!this.validateProject(pid)) return Response.send(ResponseCode.ERROR, false,"Cannat authorize with client!");
        
        accessToken = accessToken == null ? this.getAccessTokenByPId(pid) : accessToken;
        Project proj = this.projectRepo.getById(pid);
        SpreadsheetManager manager = this.getSheetManger(accessToken, this.getTokenResources(pid),new ProjectDTO(proj));
        if (manager.getE() != null) {
            return Response.send(ResponseCode.UNAUTHORIZED, false, manager.getException());
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

    @SuppressWarnings("deprecation")
    @Override
    public Object updateSpreadsheet(Integer pid,String spreadsheetId, SpreadsheetForm form, String accessToken) {
        if (spreadsheetId == null)
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet id is required");
        if (form == null || (form != null && form.getName() == null))
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet name is required");
        if(!this.validateProject(pid)) return Response.send(ResponseCode.ERROR, false,"Invalid project!");
        
        Project proj = this.projectRepo.getById(pid);
        accessToken = accessToken == null ? this.getAccessTokenByPId(pid) : accessToken;
        SpreadsheetManager manager = this.getSheetManger(accessToken, this.getTokenResources(pid),new ProjectDTO(proj));
        if (manager.getE() != null) {
            return Response.send(ResponseCode.UNAUTHORIZED, false, manager.getException());
        } else {
            Object data;
            try {
                data = manager.updateSpreadsheet(spreadsheetId, form);
                Services s = this.serviceRepo.getByRefId(spreadsheetId);
                if(form.getName() !=null) {
                    s.setName(form.getName());
                    this.serviceRepo.save(s);
                }
                return data == null ? Response.send(new ServiceRespose(s), ResponseCode.EMPTY_CONTENT, true, "No changes.")
                        : Response.send(new ServiceRespose(s), ResponseCode.UPDATE_SUCCESS, true);
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
        accessToken = accessToken == null ? this.getAccessTokenByPId(pid) : accessToken;
        if(!this.validateProject(pid)) return Response.send(ResponseCode.REQUIRED_AUTH, false,"Invalid project!");
        Project proj = this.projectRepo.getById(pid);
        SpreadsheetManager manager = this.getSheetManger(accessToken, this.getTokenResources(pid),new ProjectDTO(proj));
        if (manager.getE() != null) {
            return Response.send(ResponseCode.UNAUTHORIZED, false, manager.getException());
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
                return Response.send(new ServiceRespose(service), ResponseCode.SPREADSHEET_CREATED, true);
                
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
    public Object addNewSheet(Integer pid,String spreadsheetId, SheetForm form, String accessToken) {
        if (spreadsheetId == null)
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet id is required");
        if (form == null || (form != null && form.getName() == null))
            return Response.send(ResponseCode.REQUIRED, false, "Sheet name is required");
        if(!this.validateProject(pid)) return Response.send(ResponseCode.ERROR, false,"Invalid project!");
        Project project = this.projectRepo.getById(pid);
        accessToken = accessToken == null ? this.getAccessTokenByPId(pid) : accessToken;
        SpreadsheetManager manager = this.getSheetManger(accessToken, this.getTokenResources(pid),new ProjectDTO(project));
        if (manager.getE() != null) {
            return Response.send(ResponseCode.UNAUTHORIZED, false, manager.getException());
        } else {
            Object data;
            try {
                data = manager.addNewSheet(spreadsheetId, form);
                return Response.send(data, ResponseCode.SHEET_CREATED, true);
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
    public Object getSheets(Integer pid,String spreadsheetId, String name, Integer id, String accessToken) {
        if(!this.validateProject(pid)) return Response.send(ResponseCode.ERROR, false,"Invalid project!");
        Project project = this.projectRepo.getById(pid);
        accessToken = accessToken == null ? this.getAccessTokenByPId(pid) : accessToken;
        SpreadsheetManager manager = this.getSheetManger(accessToken, this.getTokenResources(pid),new ProjectDTO(project));
        if (manager.getE() != null) {
            return Response.send(ResponseCode.UNAUTHORIZED, false, manager.getException());
        } else {
            List<Sheet> data;
            try {
                data = manager.getSheets(spreadsheetId, name, id);
                List<SheetResponse> responseList = new ArrayList<>();
                for(Sheet s:data) {
                    responseList.add(new SheetResponse(s));
                }
                if (responseList.size() == 0) {
                    return Response.send(ResponseCode.EMPTY, true);
                }
                Map<String, Object> response = new HashMap<>();
                response.put("total", responseList.size());
                response.put("data", responseList);
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

    @SuppressWarnings("deprecation")
    @Override
    public Object getSheet(Integer pid,String spreadsheetId, Integer sheetId, SheetForm form, String accessToken) {
        if (spreadsheetId == null || sheetId == null)
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet id or sheet id is required!");
        form = form == null ? new SheetForm() : form;
        if(!this.validateProject(pid)) return Response.send(ResponseCode.ERROR, false,"Invalid project!");
        Project project = this.projectRepo.getById(pid);
        accessToken = accessToken == null ? this.getAccessTokenByPId(pid) : accessToken;
        SpreadsheetManager manager = this.getSheetManger(accessToken, this.getTokenResources(pid),new ProjectDTO(project));
        if (manager.getE() != null) {
            return Response.send(ResponseCode.UNAUTHORIZED, false, manager.getException());
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

    @SuppressWarnings("deprecation")
    @Override
    public Object updateSheet(Integer pid,String spreadsheetId, Integer sheetId, SheetForm form, String accessToken) {
        if(!this.validateProject(pid)) return Response.send(ResponseCode.ERROR, false,"Invalid project!");
        Project project = this.projectRepo.getById(pid);
        accessToken = accessToken == null ? this.getAccessTokenByPId(pid) : accessToken;
        SpreadsheetManager manager = this.getSheetManger(accessToken, this.getTokenResources(pid),new ProjectDTO(project));
        if (manager.getE() != null) {
            return Response.send(ResponseCode.UNAUTHORIZED, false, manager.getException());
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

    @SuppressWarnings("deprecation")
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
        accessToken = accessToken == null ? this.getAccessTokenByPId(pid) : accessToken;
        SpreadsheetManager manager = this.getSheetManger(accessToken, this.getTokenResources(pid),new ProjectDTO(project));
        if (manager.getE() != null) {
            return Response.send(ResponseCode.UNAUTHORIZED, false, manager.getException());
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

    @SuppressWarnings("deprecation")
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
        accessToken = accessToken == null ? this.getAccessTokenByPId(pid) : accessToken;
        SpreadsheetManager manager = this.getSheetManger(accessToken, this.getTokenResources(pid),new ProjectDTO(project));
        if (manager.getE() != null) {
            return Response.send(ResponseCode.UNAUTHORIZED, false, manager.getException());
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
        accessToken = accessToken == null ? this.getAccessTokenByPId(pid) : accessToken;
        SpreadsheetManager manager = this.getSheetManger(accessToken, this.getTokenResources(pid),new ProjectDTO(project));
        if (manager.getE() != null) {
            return Response.send(ResponseCode.UNAUTHORIZED, false, manager.getException());
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
        if(!this.validateProject(pid)) return Response.send(ResponseCode.REQUIRED_AUTH, false,"Invalid project!");
        Project project = this.projectRepo.getById(pid);
        if(project.getToken() == null) {
            return Response.send(ResponseCode.REQUIRED_AUTH, false,"Invalid credential!");
        }
//        System.out.println(project.getToken().getExpiresIn());
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
    public Object deleteSpreadsheet(Integer pid, String spreadsheetId,Boolean includeResource, String accessToken) {
        if (spreadsheetId == null)
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet id is required");
        if(!this.validateProject(pid)) return Response.send(ResponseCode.ERROR, false,"Invalid project!");
        accessToken = accessToken == null ? this.getAccessTokenByPId(pid) : accessToken;
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

    @SuppressWarnings("deprecation")
    @Override
    public Object importSpreadsheet(Integer pid, SpreadsheetForm form, String access_token) {
        if (form == null || form.getSpreadsheetId() == null)
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet id is required");
        if(!this.validateProject(pid)) return Response.send(ResponseCode.ERROR, false,"Invalid project!");
        
        access_token = access_token == null ? this.getAccessTokenByPId(pid) : access_token;
        Project project = this.projectRepo.getById(pid);
        SpreadsheetManager manager = this.getSheetManger(access_token, this.getTokenResources(pid),new ProjectDTO(project));
        if (manager.getE() != null) {
            return Response.send(ResponseCode.UNAUTHORIZED, false, manager.getException());
        } else {
            Object data;
            try {
                data = manager.getSpreadSheetData(form.getSpreadsheetId());
                if(!this.isProjectExist(pid, form.getSpreadsheetId())) {
                    Services s = new Services();
                    s.setName(((SpreadsheetResponse) data).getName());
                    s.setRefId(((SpreadsheetResponse) data).getSpreadsheetId());
                    s.setLink(((SpreadsheetResponse) data).getUrl());
                    s.setType("SPREADSHEET");
                    s.setProject(project);
                    this.serviceRepo.save(s);
                    return Response.send(new ServiceRespose(s), ResponseCode.SPREADSHEET_IMPORT, true);
                }
                return Response.send("Spreadsheet already exist!", ResponseCode.EMPTY, true);
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
    public String getAccessToken(Integer pid) {
        return this.getAccessTokenByPId(pid);
    }

    private SpreadsheetManager getSheetManger(String token, TokenDTO tokenDTO,ProjectDTO project) {
        try {
            return new SpreadsheetManager(token,tokenDTO,project);
        } catch (IOException | GeneralSecurityException | AuthException e) {
            e.printStackTrace();
            return new SpreadsheetManager(e);
        }
    }

    @SuppressWarnings("deprecation")
    private TokenDTO getTokenResources(Integer pid) {
        Project p = this.projectRepo.getById(pid);
        Token t = p.getToken();
        if(t == null) return null;
        return new TokenDTO(t);
    }
    
    @SuppressWarnings("deprecation")
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

    @SuppressWarnings("deprecation")
    private boolean isProjectExist(Integer pid,String refId) {
        boolean exist = false;
        Project project = this.projectRepo.getById(pid);
        for(Services s:project.getServices()) {
            if(s.getRefId().equals(refId)) {
                exist = true;
                break;
            }
        }
        return exist;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Object downloadSheet(Integer pid,String spreadsheetId, Integer sheetId, String access_token, HttpServletResponse response) {
        StringBuffer downloadURI = new StringBuffer();
        downloadURI
        .append("https://docs.google.com/spreadsheets/d/")
        .append(spreadsheetId)
        .append("/gviz/tq?tqx=out:csv&sheet=");
        
        Project project = this.projectRepo.getById(pid);
        access_token = access_token == null ? this.getAccessTokenByPId(pid) : access_token;
        SpreadsheetManager manager = this.getSheetManger(access_token, this.getTokenResources(pid),new ProjectDTO(project));
        if (manager.getE() != null) {
            return Response.send(ResponseCode.UNAUTHORIZED, false, manager.getException());
        } else {
            SheetResponse data;
            try {
                data = (SheetResponse) manager.getSheet(spreadsheetId, sheetId, new SheetForm());
                HttpRestClient downloadclient = new HttpRestClient(downloadURI.toString());
                
                downloadclient.download(data.getName());
                
                return Response.send("Success", ResponseCode.DOWNLOAD, true);
            } catch (IOException e) {
                e.printStackTrace();
                Object message = e instanceof GoogleJsonResponseException
                        ? Exception.parseGoogleException((GoogleJsonResponseException) e)
                        : e.getMessage();
                return Response.send(ResponseCode.ERROR, false, message);
            }
        }
        
        
    }
}
