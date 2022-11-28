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
import com.itech.api.pkg.toots.SpreadsheetResolver;
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

    @SuppressWarnings("unchecked")
    @Override
    public Object getSpreadsheetData(String spreadsheetId, SpreadsheetForm sheetForm) {
        if (spreadsheetId == null)
            Response.send(ResponseCode.REQUIRED, false);

        String sheetName = sheetForm == null ? null : sheetForm.getName();
        String sheetRange = sheetForm == null ? null : sheetForm.getRange();

        Map<String, Object> sheetResolve = (Map<String, Object>) new SpreadsheetResolver(spreadsheetId, sheetName,
                sheetRange).resolve().get();

        try {
            Object obj = new SpreadsheetManager().getSpreadSheetData(spreadsheetId, (String) sheetResolve.get("range"));
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
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            Object message = e instanceof GoogleJsonResponseException
                    ? Exception.parseGoogleException((GoogleJsonResponseException) e)
                    : e.getMessage();
            return Response.send(ResponseCode.ERROR, false, message);
        }
        return null;
    }

}
