package com.itech.api.pkg.spreadsheet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AddSheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetResponse;
import com.google.api.services.sheets.v4.model.DeleteDimensionRequest;
import com.google.api.services.sheets.v4.model.DeleteSheetRequest;
import com.google.api.services.sheets.v4.model.DimensionRange;
import com.google.api.services.sheets.v4.model.Request;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.SheetProperties;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.UpdateSheetPropertiesRequest;
import com.google.api.services.sheets.v4.model.UpdateSpreadsheetPropertiesRequest;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.itech.api.form.SheetForm;
import com.itech.api.form.SpreadsheetForm;
import com.itech.api.persistence.dto.ProjectDTO;
import com.itech.api.persistence.dto.TokenDTO;
import com.itech.api.pkg.google.GoogleConnection;
import com.itech.api.pkg.spreadsheet.tools.Property;
import com.itech.api.pkg.tools.SpreadsheetResolver;
import com.itech.api.pkg.tools.exceptions.AuthException;
import com.itech.api.pkg.toots.errors.Exception;
import com.itech.api.response.SheetResponse;
import com.itech.api.response.SpreadsheetResponse;

import lombok.Getter;

@Getter
public class SpreadsheetManager {

    private static final JacksonFactory FACTORY = JacksonFactory.getDefaultInstance();

    public Sheets spreadSheets;
    public Property prop;

    private static final String ROWS = "ROWS";
    private static final String COLUMNS = "COLUMNS";
    
    private Object exceptions;
    private Throwable e;
    private String token;
    private TokenDTO tokenRes;

    public SpreadsheetManager(Property props) throws IOException, GeneralSecurityException, AuthException {
        this.spreadSheets = this.getSheetService(props);
    }

    public SpreadsheetManager(String token,TokenDTO tokenDTO, ProjectDTO project) throws IOException, GeneralSecurityException, AuthException {
        this.token = token;
        this.spreadSheets = this.getSheetService(this.defaultProps(tokenDTO,project));
    }

    public SpreadsheetResponse getSpreadSheetData(String sheetId) throws IOException {
        Spreadsheet sheet = this.spreadSheets.spreadsheets().get(sheetId).execute();
        return new SpreadsheetResponse(sheet);
    }

    public SpreadsheetManager(Throwable e) {
        this.e = e;
    }
    
    public Object getException() {
        this.exceptions = this.e instanceof GoogleJsonResponseException
                ? Exception.parseGoogleException((GoogleJsonResponseException) e)
                : this.e instanceof AuthException ? 
                        ((AuthException) this.e).toJson() :
                            this.e.getMessage();
        return this.exceptions;
    }
    
    public static Object getAPIDocumentation() throws StreamWriteException, DatabindException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream in = SpreadsheetManager.class.getResourceAsStream("/google-sheet-api-doc.json");
        String result = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
        return mapper.readValue(result, Map.class);
    }

    public Object createSpreadSheet(SpreadsheetForm sheetform) throws IOException {
        Spreadsheet content = new Spreadsheet()
                .setProperties(new SpreadsheetProperties().setTitle(sheetform.getName()));
        Spreadsheet spreadsheet = this.spreadSheets.spreadsheets().create(content).setFields("spreadsheetId").execute();
        Spreadsheet createdSpreadsheet = this.spreadSheets.spreadsheets().get(spreadsheet.getSpreadsheetId()).execute();
        return new SpreadsheetResponse(createdSpreadsheet);
    }

    public Object updateSpreadsheet(String spreadsheetId, SpreadsheetForm form) throws IOException {
        Spreadsheet spreadsheet = this.spreadSheets.spreadsheets().get(spreadsheetId).execute();
        if (this.renameSpreadsheet(spreadsheet, spreadsheetId, form.getName())) {
            return new SpreadsheetResponse(spreadsheetId, form.getName(), spreadsheet.getSpreadsheetUrl());
        }
        return null;
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
        data.put("sheet", new SheetResponse(getSheetByName(this.spreadSheets.spreadsheets().get(sheetId).execute(), form.getName())));
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
            if (this.getSheetData(spreadsheetId, range) != null) {
                List<List<Object>> values = this.getSheetData(spreadsheetId, range);
                response.setTotal(values.size());
                response.setValues(values);
            }
            response.setRange(range);
        }
        return response;
    }

    @SuppressWarnings("unchecked")
    public Object updateSheet(String spreadsheetId, Integer sheetId, SheetForm form) throws IOException {
        SheetResponse selectedSheet = (SheetResponse) this.getSheet(spreadsheetId, sheetId, new SheetForm());
        List<Request> reqlist = new ArrayList<>();
        // rename process
        if (form.getName() != null && !form.getName().equals(selectedSheet.getName())) {
            UpdateSheetPropertiesRequest renameRequest = new UpdateSheetPropertiesRequest().setFields("Title")
                    .setProperties(new SheetProperties().setSheetId(sheetId).setTitle(form.getName()));
            reqlist.add(new Request().setUpdateSheetProperties(renameRequest));
            BatchUpdateSpreadsheetResponse response = this.spreadSheets.spreadsheets()
                    .batchUpdate(spreadsheetId, new BatchUpdateSpreadsheetRequest().setRequests(reqlist)).execute();
            selectedSheet.setChanges(response);
            selectedSheet.setName(form.getName());
        }
        // value update process
        if (form.getValues() != null && form.getValues().size() > 0) {
            Map<String, String> resolver = (Map<String, String>) new SpreadsheetResolver(
                    selectedSheet.getSheetId() + "", selectedSheet.getName(), form.getRange()).resolve().get();
            Object value = this.updateSheet(spreadsheetId, resolver.get("range"), form.getValues());
            selectedSheet.setRowsEffects(value);
            selectedSheet.setValues(form.getValues());
        }

        return selectedSheet;
    }

    public Object deleteRowsRequest(String spreadsheetId, Integer sheetId, Integer start, Integer end)
            throws IOException {
        SheetResponse selectedSheet = (SheetResponse) this.getSheet(spreadsheetId, sheetId, new SheetForm());
        BatchUpdateSpreadsheetResponse response = this.deleteDimensionOperations(spreadsheetId, sheetId, start, end,
                ROWS);
        if (response.getReplies().size() > 0 && !response.getReplies().get(0).isEmpty())
            selectedSheet.setChanges(response);
        selectedSheet.setRowsEffects(end - start);
        return selectedSheet;
    }

    public Object deleteColumnsRequest(String spreadsheetId, Integer sheetId, Integer start, Integer end)
            throws IOException {
        SheetResponse selectedSheet = (SheetResponse) this.getSheet(spreadsheetId, sheetId, new SheetForm());
        BatchUpdateSpreadsheetResponse response = this.deleteDimensionOperations(spreadsheetId, sheetId, start, end,
                COLUMNS);
        if (response.getReplies().size() > 0 && !response.getReplies().get(0).isEmpty())
            selectedSheet.setChanges(response);
        selectedSheet.setColumnsEffects(end - start);
        return selectedSheet;
    }
    
    public Object deleteSheet(String spreadsheetId, Integer sheetId) throws IOException {
        BatchUpdateSpreadsheetRequest deleteRequest = new BatchUpdateSpreadsheetRequest();
        List<Request> requestList = new ArrayList<>();
        requestList.add(new Request().setDeleteSheet(new DeleteSheetRequest().setSheetId(sheetId)));
        deleteRequest.setRequests(requestList);
        BatchUpdateSpreadsheetResponse response = this.spreadSheets.spreadsheets().batchUpdate(spreadsheetId, deleteRequest).execute();
        SheetResponse sheet = new SheetResponse();
        sheet.setSheetId(sheetId);
        sheet.setTotal(1);
        if (response.getReplies().size() > 0 && !response.getReplies().get(0).isEmpty())
            sheet.setChanges(response);
        return sheet;
    }
    
    private List<List<Object>> getSheetData(String spreadsheetId, String range) throws IOException {
        ValueRange value = this.spreadSheets.spreadsheets().values().get(spreadsheetId, range).execute();
        List<List<Object>> response = value.getValues();
        if (response == null) {
            return null;
        }
        return response;
    }

    private BatchUpdateSpreadsheetResponse deleteDimensionOperations(String spreadsheetId, Integer sheetId,
            Integer start, Integer end, String dimesion) throws IOException {
        BatchUpdateSpreadsheetRequest deleteRequest = new BatchUpdateSpreadsheetRequest();
        List<Request> requestlist = new ArrayList<>();
        requestlist.add(new Request().setDeleteDimension(new DeleteDimensionRequest().setRange(new DimensionRange()
                .setSheetId(sheetId).setDimension(dimesion).setStartIndex(start).setEndIndex(end))));
        BatchUpdateSpreadsheetResponse response = this.spreadSheets.spreadsheets()
                .batchUpdate(spreadsheetId, deleteRequest.setRequests(requestlist)).execute();

        return response;
    }

    @SuppressWarnings("unchecked")
    private UpdateValuesResponse updateSheet(String sheetid, String range, Object values) throws IOException {
        ValueRange valueRange = new ValueRange().setValues((List<List<Object>>) values);
        return this.spreadSheets.spreadsheets().values().update(sheetid, range, valueRange).setValueInputOption("RAW")
                .execute();
    }

    private boolean renameSpreadsheet(Spreadsheet spreadsheet, String spreadsheetId, String newName)
            throws IOException {
        String title = spreadsheet.getProperties().getTitle();
        if (newName.equals(title)) {
            return false;
        } else {
            UpdateSpreadsheetPropertiesRequest request = new UpdateSpreadsheetPropertiesRequest().setFields("Title")
                    .setProperties(new SpreadsheetProperties().setTitle(newName));
            List<Request> list = new ArrayList<>();
            list.add(new Request().setUpdateSpreadsheetProperties(request));
            BatchUpdateSpreadsheetRequest updateRequest = new BatchUpdateSpreadsheetRequest().setRequests(list);
            this.spreadSheets.spreadsheets().batchUpdate(spreadsheetId, updateRequest).execute();
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

    private Property defaultProps(TokenDTO tokenResource, ProjectDTO project) {
        Property prop = new Property();
        prop.setToken(this.token);
        prop.setTokenResource(tokenResource);
        prop.setProject(project);
        return prop;
    }

    private Sheets getSheetService(Property props) throws IOException, GeneralSecurityException, AuthException {
        Credential credential = GoogleConnection.connect(props);
        
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), FACTORY, credential)
                .setApplicationName("Google Sheet API").build();
    }

}
