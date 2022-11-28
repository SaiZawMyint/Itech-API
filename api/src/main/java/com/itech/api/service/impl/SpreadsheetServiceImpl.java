package com.itech.api.service.impl;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.sheets.v4.model.Sheet;
import com.itech.api.form.SheetForm;
import com.itech.api.form.SpreadsheetForm;
import com.itech.api.pkg.spreadsheet.SpreadsheetManager;
import com.itech.api.pkg.toots.Response;
import com.itech.api.pkg.toots.enums.ResponseCode;
import com.itech.api.pkg.toots.errors.Exception;
import com.itech.api.service.SpreadsheetService;

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
    public Object getSpreadsheetData(String spreadsheetId) {
        if (spreadsheetId == null)
            return Response.send(ResponseCode.REQUIRED, false);

        try {
            Object obj = new SpreadsheetManager().getSpreadSheetData(spreadsheetId);
            return obj == null ? Response.send(ResponseCode.EMPTY, true)
                    : Response.send(obj, ResponseCode.SUCCESS, true);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            Object message = e instanceof GoogleJsonResponseException
                    ? Exception.parseGoogleException((GoogleJsonResponseException) e)
                    : e.getMessage();
            return Response.send(ResponseCode.ERROR, false, message);
        }
    }


    @Override
    public Object updateSpreadsheet(String spreadsheetId, SpreadsheetForm form) {
        if (spreadsheetId == null)
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet id is required");
        if(form == null || (form!=null && form.getName() == null ))
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet name is required");
        
        try {
            Object obj = new SpreadsheetManager().updateSpreadsheet(spreadsheetId,form);
            return obj == null ? Response.send(obj, ResponseCode.EMPTY_CONTENT, true, "No changes.")
                    : Response.send(obj, ResponseCode.UPDATE_SUCCESS, true);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            Object message = e instanceof GoogleJsonResponseException
                    ? Exception.parseGoogleException((GoogleJsonResponseException) e)
                    : e.getMessage();
            return Response.send(ResponseCode.ERROR, false, message);
        }
        
    }
    
    @Override
    public Object createSpreadSheet(SpreadsheetForm form) {
        if (form == null || (form != null && form.getName() == null))
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet name is required");
        try {
            Object data = new SpreadsheetManager().createSpreadSheet(form);
            return Response.send(data, ResponseCode.SUCCESS, true);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            Object message = e instanceof GoogleJsonResponseException
                    ? Exception.parseGoogleException((GoogleJsonResponseException) e)
                    : e.getMessage();
            return Response.send(ResponseCode.ERROR, false, message);
        }
    }

    @Override
    public Object addNewSheet(String spreadsheetId, SheetForm form) {
        if (spreadsheetId == null)
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet id is required");
        if (form == null || (form != null && form.getName() == null))
            return Response.send(ResponseCode.REQUIRED, false, "Sheet name is required");
        try {
            Object data = new SpreadsheetManager().addNewSheet(spreadsheetId, form);
            return Response.send(data, ResponseCode.UPDATE_SUCCESS, true);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            Object message = e instanceof GoogleJsonResponseException
                    ? Exception.parseGoogleException((GoogleJsonResponseException) e)
                    : e.getMessage();
            return Response.send(ResponseCode.ERROR, false, message);
        }
    }

    @Override
    public Object getSheets(String spreadsheetId, String name, Integer id) {
        try {
            List<Sheet> data = new SpreadsheetManager().getSheets(spreadsheetId, name, id);
            if (data.size() == 0) {
                return Response.send(ResponseCode.EMPTY, true);
            }
            Map<String, Object> response = new HashMap<>();
            response.put("total", data.size());
            response.put("data", data);
            return Response.send(response, ResponseCode.UPDATE_SUCCESS, true);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            Object message = e instanceof GoogleJsonResponseException
                    ? Exception.parseGoogleException((GoogleJsonResponseException) e)
                    : e.getMessage();
            return Response.send(ResponseCode.ERROR, false, message);
        }
    }

    @Override
    public Object getSheet(String spreadsheetId, Integer sheetId, SheetForm form) {
        if (spreadsheetId == null || sheetId == null)
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet id or sheet id is required!");
        form = form == null ? new SheetForm() : form;
        try {
            Object data = new SpreadsheetManager().getSheet(spreadsheetId, sheetId, form);
            if (data == null)
                return Response.send(ResponseCode.EMPTY, true);
            return Response.send(data, ResponseCode.SUCCESS, true);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            Object message = e instanceof GoogleJsonResponseException
                    ? Exception.parseGoogleException((GoogleJsonResponseException) e)
                    : e.getMessage();
            return Response.send(ResponseCode.ERROR, false, message);
        }
    }

    @Override
    public Object updateSheet(String spreadsheetId, Integer sheetId, SheetForm form) {
        try {
            Object data = new SpreadsheetManager().updateSheet(spreadsheetId,sheetId,form);
            return Response.send(data, ResponseCode.UPDATE_SUCCESS, true);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            Object message = e instanceof GoogleJsonResponseException
                    ? Exception.parseGoogleException((GoogleJsonResponseException) e)
                    : e.getMessage();
            return Response.send(ResponseCode.ERROR, false, message);
        }
    }

    @Override
    public Object deleteRowsRequest(String spreadsheetId, Integer sheetId, Integer start, Integer end) {
        if (spreadsheetId == null)
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet id is required");
        if (sheetId == null)
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet id is required");
        
        start = start == null? 0 : start;
        end = end == null ? 1 : end;
        
        if(start >= end)
            return Response.send(ResponseCode.ERROR, false, "Start index must greater than end index!");
        
        try {
            Object data = new SpreadsheetManager().deleteRowsRequest(spreadsheetId,sheetId,start,end);
            return Response.send(data, ResponseCode.DELETE, true);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            Object message = e instanceof GoogleJsonResponseException
                    ? Exception.parseGoogleException((GoogleJsonResponseException) e)
                    : e.getMessage();
            return Response.send(ResponseCode.ERROR, false, message);
        }
    }

    @Override
    public Object deleteColumnsRequest(String spreadsheetId, Integer sheetId, Integer start, Integer end) {
        if (spreadsheetId == null)
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet id is required");
        if (sheetId == null)
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet id is required");
        
        start = start == null? 0 : start;
        end = end == null ? 1 : end;
        
        if(start >= end)
            return Response.send(ResponseCode.ERROR, false, "Start index must greater than end index!");
        
        try {
            Object data = new SpreadsheetManager().deleteColumnsRequest(spreadsheetId,sheetId,start,end);
            return Response.send(data, ResponseCode.DELETE, true);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            Object message = e instanceof GoogleJsonResponseException
                    ? Exception.parseGoogleException((GoogleJsonResponseException) e)
                    : e.getMessage();
            return Response.send(ResponseCode.ERROR, false, message);
        }
    }

    @Override
    public Object deleteSheet(String spreadsheetId, Integer sheetId) {
        if (spreadsheetId == null)
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet id is required");
        if (sheetId == null)
            return Response.send(ResponseCode.REQUIRED, false, "Spreadsheet id is required");
        
        try {
            Object data = new SpreadsheetManager().deleteSheet(spreadsheetId,sheetId);
            return Response.send(data, ResponseCode.DELETE, true);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            Object message = e instanceof GoogleJsonResponseException
                    ? Exception.parseGoogleException((GoogleJsonResponseException) e)
                    : e.getMessage();
            return Response.send(ResponseCode.ERROR, false, message);
        }
    }

}
