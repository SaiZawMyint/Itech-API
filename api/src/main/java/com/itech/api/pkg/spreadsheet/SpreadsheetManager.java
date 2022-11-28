package com.itech.api.pkg.spreadsheet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AddSheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.UpdateSpreadsheetPropertiesRequest;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.itech.api.form.SheetForm;
import com.itech.api.form.SpreadsheetForm;
import com.itech.api.pkg.google.GoogleConnection;
import com.itech.api.pkg.spreadsheet.tools.Property;
import com.itech.api.pkg.toots.SpreadsheetResolver;
import com.itech.api.response.SheetResponse;
import com.itech.api.response.SpreadsheetResponse;

public class SpreadsheetManager {

    private static final JacksonFactory FACTORY = JacksonFactory.getDefaultInstance();

    public Sheets spreadSheets;
    public Property prop;

    public SpreadsheetManager(Property props) throws IOException, GeneralSecurityException {
        this.spreadSheets = this.getSheetService(props);
    }

    public SpreadsheetManager() throws IOException, GeneralSecurityException {
        this.spreadSheets = this.getSheetService(this.defaultProps());
    }

    public Map<Object, Object> getSpreadSheetData(String sheetId, String range) throws IOException {
        ValueRange value = this.spreadSheets.spreadsheets().values().get(sheetId, range).execute();
        List<List<Object>> response = value.getValues();
        if (response == null) {
            return null;
        }
        Map<Object, Object> data = new HashMap<>();
        data.put("total", response.size());
        data.put("data", response);
        return data;
    }

    public static Object getAPIDocumentation() throws StreamWriteException, DatabindException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream in = SpreadsheetManager.class.getResourceAsStream("/google-sheet-api-doc.json");
        String result = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
        return mapper.readValue(result, Map.class);
    }

    @SuppressWarnings("unchecked")
    public Object createSpreadSheet(SpreadsheetForm sheetform) throws IOException {
        Spreadsheet spreadsheet = new Spreadsheet()
                .setProperties(new SpreadsheetProperties().setTitle(sheetform.getName()));
        spreadsheet = this.spreadSheets.spreadsheets().create(spreadsheet).setFields("spreadsheetId").execute();

        String range = sheetform.getRange() == null ? "A1" : sheetform.getRange();
        sheetform.setRange(range);
        Map<String, String> sheetResolver = (Map<String, String>) new SpreadsheetResolver(
                spreadsheet.getSpreadsheetId(), sheetform.getSheetName(), sheetform.getRange()).resolve().get();

        if (sheetform.getValues() != null && sheetform.getValues().size() > 0)
            updateSheet(spreadsheet.getSpreadsheetId(), sheetResolver.get("range"), sheetform.getValues());

        sheetform.setSheetId(spreadsheet.getSpreadsheetId());

        return new SpreadsheetResponse(sheetform);
    }

    public Object updateSpreadSheet(SpreadsheetForm form, String options) throws IOException {

        List<ValueRange> data = new ArrayList<>();
        data.add(new ValueRange().setValues(form.getValues()).setRange("A1"));

        Spreadsheet spreadsheet = this.spreadSheets.spreadsheets().get(form.getSheetId()).execute();

        // rename spreadsheet process
        if (form.getName() != null)
            renameSpreadsheet(spreadsheet, form);

        BatchUpdateValuesRequest request = new BatchUpdateValuesRequest();
        request.setValueInputOption(options);
        request.setData(data);

        BatchUpdateValuesResponse response = this.spreadSheets.spreadsheets().values()
                .batchUpdate(form.getSheetId(), request).execute();

        Map<Object, Object> result = new HashMap<>();
        result.put("spreadSheet", new SpreadsheetResponse(form));
        result.put("record", response);
        return result;
    }

    @SuppressWarnings("unchecked")
    public Object addNewSheet(String sheetId, SheetForm form) throws IOException {

        Request sheetRequest = new Request()
                .setAddSheet(new AddSheetRequest().setProperties(new SheetProperties().setTitle(form.getName())));

        List<Request> requestList = new ArrayList<>();
        requestList.add(sheetRequest);

        BatchUpdateSpreadsheetRequest updateRequest = new BatchUpdateSpreadsheetRequest();
        updateRequest.setRequests(requestList);
        BatchUpdateSpreadsheetResponse updateResponse = this.spreadSheets.spreadsheets()
                .batchUpdate(sheetId, updateRequest).execute();
        Map<String, Object> data = new HashMap<>();
        form.setSheetId(updateResponse.getReplies().get(0).getAddSheet().getProperties().getSheetId());
        data.put("request", new SheetResponse(form));
        data.put("sheetRecord", updateResponse);

        // add instance values
        if (form.getValues() != null && form.getValues().size() > 0) {
            Map<String, String> resolver = (Map<String, String>) new SpreadsheetResolver(sheetId, form.getName(),
                    form.getRange()).resolve().get();
            UpdateValuesResponse updateVresponse = updateSheet(sheetId, resolver.get("range"), form.getValues());
            data.put("valuesRecord", updateVresponse);
        }
        data.put("sheet", getSheetByName(this.spreadSheets.spreadsheets().get(sheetId).execute(), form.getName()));
        return data;
    }

    public List<Sheet> getSheets(String spreadsheetId, String name, Integer id) throws IOException {
        List<Sheet> sheets = this.spreadSheets.spreadsheets().get(spreadsheetId).execute().getSheets();

        if (name == null && id == null) {
            return sheets;
        }
        return sheets.stream().filter(sheet -> (name != null && sheet.getProperties().getTitle().equals(name))
                || (id != null && sheet.getProperties().getSheetId().equals(id))).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public Object getSheet(String spreadsheetId, Integer sheetId, SheetForm form) throws IOException {
        List<Sheet> sheets = this.getSheets(spreadsheetId, form.getName(), sheetId);
        if (sheets.size() == 0)
            return null;
        Sheet sheet = sheets.get(0);
        SheetResponse response = new SheetResponse(sheet);
        // values
        if (form.getRange() != null) {
            Map<String, String> resolve = (Map<String, String>) new SpreadsheetResolver(
                    sheet.getProperties().getSheetId() + "", sheet.getProperties().getTitle(), form.getRange())
                            .resolve().get();
            String range = resolve.get("range");
            List<List<Object>> values = (List<List<Object>>) this.getSpreadSheetData(spreadsheetId, range).get("data");
            response.setValues(values);
            response.setRange(range);
        }
        return response;
    }

    public Object updateSheet(String spreadsheetId, Integer sheetId, SheetForm form) {
        return null;
    }
    
    @SuppressWarnings("unchecked")
    private UpdateValuesResponse updateSheet(String sheetid, String range, Object values) throws IOException {
        ValueRange valueRange = new ValueRange().setValues((List<List<Object>>) values);
        return this.spreadSheets.spreadsheets().values().update(sheetid, range, valueRange).setValueInputOption("RAW")
                .execute();
    }

    private boolean renameSpreadsheet(Spreadsheet spreadsheet, SpreadsheetForm form) throws IOException {
        String title = spreadsheet.getProperties().getTitle();
        if (form.getSheetName().equals(title)) {
            return false;
        } else {
            UpdateSpreadsheetPropertiesRequest request = new UpdateSpreadsheetPropertiesRequest().setFields("Title")
                    .setProperties(new SpreadsheetProperties().setTitle(form.getName()));
            List<Request> list = new ArrayList<>();
            list.add(new Request().setUpdateSpreadsheetProperties(request));
            BatchUpdateSpreadsheetRequest updateRequest = new BatchUpdateSpreadsheetRequest().setRequests(list);
            this.spreadSheets.spreadsheets().batchUpdate(form.getSheetId(), updateRequest).execute();
            return true;
        }
    }

    private Sheet getSheetByName(Spreadsheet spreadsheet, String name) {
        Sheet sheet = null;
        List<Sheet> sheets = spreadsheet.getSheets();
        for (Sheet s : sheets) {
            if (s.getProperties().getTitle().equals(name)) {
                sheet = s;
                break;
            }
        }
        return sheet;
    }

    private Property defaultProps() {
        Property prop = new Property();
        prop.setClientSecretPath("/itech-google-client.json");
        prop.setPort(9090);
        prop.setCallBack("/google/");
        prop.setStoreTokenPath("itech/token");
        prop.setAccessType("offline");
        prop.setScope(Collections.singletonList(SheetsScopes.SPREADSHEETS));
        return prop;
    }

    private Sheets getSheetService(Property props) throws IOException, GeneralSecurityException {
        Credential credential = GoogleConnection.connect(props);
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), FACTORY, credential)
                .setApplicationName("Google Sheet API").build();
    }

}
