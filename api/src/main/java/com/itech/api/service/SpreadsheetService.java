package com.itech.api.service;

import com.itech.api.form.SheetForm;
import com.itech.api.form.SpreadsheetForm;

public interface SpreadsheetService {

    public Object getSpreadSheetDocumentation();

    public Object getSpreadsheetData(String spreadsheetId, SpreadsheetForm sheetForm);

    public Object createSpreadSheet(SpreadsheetForm form);

    public Object addNewSheet(String spreadsheetId, SheetForm form);

    public Object getSheets(String spreadsheetId, String name, Integer id);

    public Object getSheet(String spreadsheetId, Integer sheetId, SheetForm form);

}
