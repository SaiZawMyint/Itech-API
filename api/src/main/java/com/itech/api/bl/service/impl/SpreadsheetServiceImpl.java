package com.itech.api.bl.service.impl;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.sheets.v4.model.Sheet;
import com.itech.api.bl.service.SpreadsheetService;
import com.itech.api.form.SheetForm;
import com.itech.api.form.SpreadsheetForm;
import com.itech.api.persistence.dto.TokenDTO;
import com.itech.api.pkg.spreadsheet.SpreadsheetManager;
import com.itech.api.pkg.tools.Response;
import com.itech.api.pkg.tools.enums.ResponseCode;
import com.itech.api.pkg.tools.exceptions.AuthException;
import com.itech.api.pkg.toots.errors.Exception;

@Service
public class SpreadsheetServiceImpl implements SpreadsheetService {

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
    public Object getSpreadsheetData(String pid,String spreadsheetId, String accessToken) {
        if (spreadsheetId == null)
            return Response.send(ResponseCode.REQUIRED, false);

        SpreadsheetManager manager = this.getSheetManger(accessToken, null);
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
    public Object updateSpreadsheet(String pid,String spreadsheetId, SpreadsheetForm form, String accessToken) {
        if (spreadsheetId == null)
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet id is required");
        if (form == null || (form != null && form.getName() == null))
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet name is required");

        SpreadsheetManager manager = this.getSheetManger(accessToken, null);
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

    @Override
    public Object createSpreadSheet(String pid,SpreadsheetForm form, String accessToken) {
        if (form == null || (form != null && form.getName() == null))
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet name is required");
        
        SpreadsheetManager manager = this.getSheetManger(accessToken, null);
        if (manager.getE() != null) {
            return Response.send(ResponseCode.ERROR, false, manager.getException());
        } else {
            Object data;
            try {
                data = manager.createSpreadSheet(form);
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
    public Object addNewSheet(String pid,String spreadsheetId, SheetForm form, String accessToken) {
        if (spreadsheetId == null)
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet id is required");
        if (form == null || (form != null && form.getName() == null))
            return Response.send(ResponseCode.REQUIRED, false, "Sheet name is required");
        
        SpreadsheetManager manager = this.getSheetManger(accessToken, null);
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
    public Object getSheets(String pid,String spreadsheetId, String name, Integer id, String accessToken) {
        
        SpreadsheetManager manager = this.getSheetManger(accessToken, null);
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
    public Object getSheet(String pid,String spreadsheetId, Integer sheetId, SheetForm form, String accessToken) {
        if (spreadsheetId == null || sheetId == null)
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet id or sheet id is required!");
        form = form == null ? new SheetForm() : form;
        
        SpreadsheetManager manager = this.getSheetManger(accessToken, null);
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
    public Object updateSheet(String pid,String spreadsheetId, Integer sheetId, SheetForm form, String accessToken) {
        SpreadsheetManager manager = this.getSheetManger(accessToken, null);
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
    public Object deleteRowsRequest(String pid,String spreadsheetId, Integer sheetId, Integer start, Integer end, String accessToken) {
        if (spreadsheetId == null)
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet id is required");
        if (sheetId == null)
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet id is required");

        start = start == null ? 0 : start;
        end = end == null ? 1 : end;

        if (start >= end)
            return Response.send(ResponseCode.ERROR, false, "Start index must greater than end index!");

        SpreadsheetManager manager = this.getSheetManger(accessToken, null);
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
    public Object deleteColumnsRequest(String pid,String spreadsheetId, Integer sheetId, Integer start, Integer end, String accessToken) {
        if (spreadsheetId == null)
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet id is required");
        if (sheetId == null)
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet id is required");

        start = start == null ? 0 : start;
        end = end == null ? 1 : end;

        if (start >= end)
            return Response.send(ResponseCode.ERROR, false, "Start index must greater than end index!");

        SpreadsheetManager manager = this.getSheetManger(accessToken, null);
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

    @Override
    public Object deleteSheet(String pid,String spreadsheetId, Integer sheetId, String accessToken) {
        if (spreadsheetId == null)
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet id is required");
        if (sheetId == null)
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet id is required");

        SpreadsheetManager manager = this.getSheetManger(accessToken, null);
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

    private SpreadsheetManager getSheetManger(String token, TokenDTO tokenDTO) {
        try {
            return new SpreadsheetManager(token,tokenDTO);
        } catch (IOException | GeneralSecurityException | AuthException e) {
            e.printStackTrace();
            return new SpreadsheetManager(e);
        }
    }

}
