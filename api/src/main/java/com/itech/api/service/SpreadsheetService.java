package com.itech.api.service;

import com.itech.api.form.SheetForm;
import com.itech.api.form.SpreadsheetForm;

public interface SpreadsheetService {

    public Object getSpreadSheetDocumentation();

    public Object getSpreadsheetData(String spreadsheetId, String accessToken);

    public Object updateSpreadsheet(String spreadsheetId, SpreadsheetForm form, String accessToken);
    
    public Object createSpreadSheet(SpreadsheetForm form, String accessToken);

    public Object addNewSheet(String spreadsheetId, SheetForm form, String accessToken);

    public Object getSheets(String spreadsheetId, String name, Integer id, String accessToken);

    public Object getSheet(String spreadsheetId, Integer sheetId, SheetForm form, String accessToken);

    public Object updateSheet(String spreadsheetId, Integer sheetId, SheetForm form, String accessToken);

    public Object deleteRowsRequest(String spreadsheetId, Integer sheetId, Integer start, Integer end, String accessToken);

    public Object deleteColumnsRequest(String spreadsheetId, Integer sheetId, Integer start, Integer end, String accessToken);

    public Object deleteSheet(String spreadsheetId, Integer sheetId, String accessToken);

}
