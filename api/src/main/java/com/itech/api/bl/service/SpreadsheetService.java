package com.itech.api.bl.service;

import com.itech.api.form.SheetForm;
import com.itech.api.form.SpreadsheetForm;

public interface SpreadsheetService {

    public Object getSpreadSheetDocumentation();

    public Object getSpreadsheetData(String pid,String spreadsheetId, String accessToken);

    public Object updateSpreadsheet(String pid,String spreadsheetId, SpreadsheetForm form, String accessToken);
    
    public Object createSpreadSheet(String pid,SpreadsheetForm form, String accessToken);

    public Object addNewSheet(String pid,String spreadsheetId, SheetForm form, String accessToken);

    public Object getSheets(String pid,String spreadsheetId, String name, Integer id, String accessToken);

    public Object getSheet(String pid,String spreadsheetId, Integer sheetId, SheetForm form, String accessToken);

    public Object updateSheet(String pid,String spreadsheetId, Integer sheetId, SheetForm form, String accessToken);

    public Object deleteRowsRequest(String pid,String spreadsheetId, Integer sheetId, Integer start, Integer end, String accessToken);

    public Object deleteColumnsRequest(String pid,String spreadsheetId, Integer sheetId, Integer start, Integer end, String accessToken);

    public Object deleteSheet(String pid,String spreadsheetId, Integer sheetId, String accessToken);

}
