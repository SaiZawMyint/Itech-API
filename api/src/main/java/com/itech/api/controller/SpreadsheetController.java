package com.itech.api.controller;

import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.itech.api.bl.service.AuthService;
import com.itech.api.bl.service.SpreadsheetService;
import com.itech.api.form.SheetForm;
import com.itech.api.form.SpreadsheetForm;

@SuppressWarnings("unchecked")
@RestController
@RequestMapping("/itech/api/spreadsheet")
public class SpreadsheetController {

    @Autowired
    private SpreadsheetService spreadsheetService;
    @Autowired
    private AuthService authService;

    @GetMapping("/")
    @ResponseBody
    public ResponseEntity<? extends Object> spreadSheetHome() {
        return (ResponseEntity<? extends Object>) this.spreadsheetService.getSpreadSheetDocumentation();
    }

    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<? extends Object> createSpreadsheet(@Nullable @RequestBody SpreadsheetForm form,
            @Nullable @RequestParam String access_token) {
        return (ResponseEntity<? extends Object>) this.spreadsheetService.createSpreadSheet(form,access_token);
    }

    @GetMapping("/{spreadsheetId}")
    @ResponseBody
    public ResponseEntity<? extends Object> getSpreadsheet(@PathVariable String spreadsheetId,
            @Nullable @RequestParam String access_token) {
        return (ResponseEntity<? extends Object>) this.spreadsheetService.getSpreadsheetData(spreadsheetId,access_token);
    }

    @PostMapping("/{spreadsheetId}")
    @ResponseBody
    public ResponseEntity<? extends Object> updateSpreadsheet(@PathVariable String spreadsheetId,
            @Nullable @RequestBody SpreadsheetForm form, @Nullable @RequestParam String fields,
            @Nullable @RequestParam String access_token) {
        return (ResponseEntity<? extends Object>) this.spreadsheetService.updateSpreadsheet(spreadsheetId, form,access_token);
    }

    @PostMapping("/{spreadsheetId}/sheets")
    @ResponseBody
    public ResponseEntity<? extends Object> addNewSheet(@PathVariable String spreadsheetId,
            @Nullable @RequestBody SheetForm form, @Nullable @RequestParam String access_token) {
        return (ResponseEntity<? extends Object>) this.spreadsheetService.addNewSheet(spreadsheetId, form,access_token);
    }

    @GetMapping("/{spreadsheetId}/sheets")
    @ResponseBody
    public ResponseEntity<? extends Object> getSheets(@PathVariable String spreadsheetId,
            @Nullable @RequestParam String name, @Nullable @RequestParam Integer id,
            @Nullable @RequestParam String access_token) {
        return (ResponseEntity<? extends Object>) this.spreadsheetService.getSheets(spreadsheetId, name, id,access_token);
    }

    @GetMapping("/{spreadsheetId}/{sheetId}")
    @ResponseBody
    public ResponseEntity<? extends Object> getSheet(@PathVariable String spreadsheetId, @PathVariable Integer sheetId,
            @Nullable @ModelAttribute SheetForm form, @Nullable @RequestParam String access_token) {
        return (ResponseEntity<? extends Object>) this.spreadsheetService.getSheet(spreadsheetId, sheetId, form,access_token);
    }

    @PostMapping("/{spreadsheetId}/{sheetId}")
    @ResponseBody
    public ResponseEntity<? extends Object> updateSheet(@PathVariable String spreadsheetId,
            @PathVariable Integer sheetId, @RequestBody SheetForm form, @Nullable @RequestParam String access_token) {
        return (ResponseEntity<? extends Object>) this.spreadsheetService.updateSheet(spreadsheetId, sheetId, form,access_token);
    }

    @DeleteMapping("/{spreadsheetId}/{sheetId}")
    @ResponseBody
    public ResponseEntity<? extends Object> deleteSheet(@PathVariable String spreadsheetId,
            @PathVariable Integer sheetId, @Nullable @RequestParam String access_token) {
        return (ResponseEntity<? extends Object>) this.spreadsheetService.deleteSheet(spreadsheetId, sheetId,access_token);
    }

    @DeleteMapping("/{spreadsheetId}/{sheetId}/rows")
    public ResponseEntity<? extends Object> deleteRowsRequest(@PathVariable String spreadsheetId,
            @PathVariable Integer sheetId, @Nullable @RequestParam Integer start, @Nullable @RequestParam Integer end,
            @Nullable @RequestParam String access_token) {
        return (ResponseEntity<? extends Object>) this.spreadsheetService.deleteRowsRequest(spreadsheetId, sheetId,
                start, end,access_token);
    }

    @DeleteMapping("/{spreadsheetId}/{sheetId}/columns")
    public ResponseEntity<? extends Object> deleteColumnsRequest(@PathVariable String spreadsheetId,
            @PathVariable Integer sheetId, @Nullable @RequestParam Integer start, @Nullable @RequestParam Integer end,
            @Nullable @RequestParam String access_token) {
        return (ResponseEntity<? extends Object>) this.spreadsheetService.deleteColumnsRequest(spreadsheetId, sheetId,
                start, end,access_token);
    }

}
