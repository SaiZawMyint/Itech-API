package com.itech.api.bl.service;

import com.itech.api.form.SheetForm;
import com.itech.api.form.SpreadsheetForm;

import jakarta.servlet.http.HttpServletResponse;

public interface SpreadsheetService {

    public Object getSpreadSheetDocumentation();

    public Object getSpreadsheetData(Integer pid,String spreadsheetId, String accessToken);

    public Object updateSpreadsheet(Integer pid,String spreadsheetId, SpreadsheetForm form, String accessToken);
    
    public Object createSpreadSheet(Integer pid,SpreadsheetForm form, String accessToken);

    public Object addNewSheet(Integer pid,String spreadsheetId, SheetForm form, String accessToken);

    public Object getSheets(Integer pid,String spreadsheetId, String name, Integer id, String accessToken);

    public Object getSheet(Integer pid,String spreadsheetId, Integer sheetId, SheetForm form, String accessToken);

    public Object updateSheet(Integer pid,String spreadsheetId, Integer sheetId, SheetForm form, String accessToken);

    public Object deleteRowsRequest(Integer pid,String spreadsheetId, Integer sheetId, Integer start, Integer end, String accessToken);

    public Object deleteColumnsRequest(Integer pid,String spreadsheetId, Integer sheetId, Integer start, Integer end, String accessToken);

    public Object deleteSheet(Integer pid,String spreadsheetId, Integer sheetId, String accessToken);

    public Object getSpreadsheets(Integer pid, String access_token);

    public Object importSpreadsheet(Integer pid, SpreadsheetForm form, String access_token);
    
    public String getAccessToken(Integer pid);

    public Object downloadSheet(Integer pid,String spreadsheetId, Integer sheetId, String access_token, HttpServletResponse response);

}
