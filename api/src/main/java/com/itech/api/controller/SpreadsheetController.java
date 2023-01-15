package com.itech.api.controller;

import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.itech.api.bl.service.SpreadsheetService;
import com.itech.api.form.SheetForm;
import com.itech.api.form.SpreadsheetForm;

import jakarta.servlet.http.HttpServletResponse;

@SuppressWarnings("unchecked")
@RestController
@RequestMapping("/itech/api/spreadsheet")
@CrossOrigin
public class SpreadsheetController {

    @Autowired
    private SpreadsheetService spreadsheetService;

    @GetMapping("/")
    @ResponseBody
    public ResponseEntity<? extends Object> spreadSheetHome() {
        return (ResponseEntity<? extends Object>) this.spreadsheetService.getSpreadSheetDocumentation();
    }

    @PostMapping("/{pid}/create")
    @ResponseBody
    public ResponseEntity<? extends Object> createSpreadsheet(@PathVariable Integer pid,@Nullable @RequestBody SpreadsheetForm form,
            @Nullable @RequestParam String access_token) {
        return (ResponseEntity<? extends Object>) this.spreadsheetService.createSpreadSheet(pid,form,access_token);
    }

    @PostMapping("/{pid}/import")
    @ResponseBody
    public ResponseEntity<? extends Object> importSpreadsheet(@PathVariable Integer pid,@Nullable @RequestBody SpreadsheetForm form,
            @Nullable @RequestParam String access_token) {
        return (ResponseEntity<? extends Object>) this.spreadsheetService.importSpreadsheet(pid,form,access_token);
    }
    
    @GetMapping("/{pid}/")
    @ResponseBody
    public ResponseEntity<? extends Object> getSpreadsheets(@PathVariable Integer pid,
            @Nullable @RequestParam String access_token) {
        return (ResponseEntity<? extends Object>) this.spreadsheetService.getSpreadsheets(pid,access_token);
    }
    
    @GetMapping("/{pid}/{spreadsheetId}")
    @ResponseBody
    public ResponseEntity<? extends Object> getSpreadsheet(@PathVariable Integer pid,@PathVariable String spreadsheetId,
            @Nullable @RequestParam String access_token) {
        return (ResponseEntity<? extends Object>) this.spreadsheetService.getSpreadsheetData(pid,spreadsheetId,access_token);
    }

    @PutMapping("/{pid}/{spreadsheetId}")
    @ResponseBody
    public ResponseEntity<? extends Object> updateSpreadsheet(@PathVariable Integer pid,@PathVariable String spreadsheetId,
            @Nullable @RequestBody SpreadsheetForm form, @Nullable @RequestParam String fields,
            @Nullable @RequestParam String access_token) {
        return (ResponseEntity<? extends Object>) this.spreadsheetService.updateSpreadsheet(pid,spreadsheetId, form,access_token);
    }

    @DeleteMapping("/{pid}/{spreadsheetId}")
    @ResponseBody
    public ResponseEntity<? extends Object> deleteSpreadsheet(@PathVariable Integer pid,@PathVariable String spreadsheetId,
            @Nullable @RequestParam Boolean includeResource,
            @Nullable @RequestParam String access_token) {
        return (ResponseEntity<? extends Object>) this.spreadsheetService.deleteSpreadsheet(pid,spreadsheetId,includeResource,access_token);
    }

    @PostMapping("/{pid}/{spreadsheetId}/sheets")
    @ResponseBody
    public ResponseEntity<? extends Object> addNewSheet(@PathVariable Integer pid,@PathVariable String spreadsheetId,
            @Nullable @RequestBody SheetForm form, @Nullable @RequestParam String access_token) {
        return (ResponseEntity<? extends Object>) this.spreadsheetService.addNewSheet(pid,spreadsheetId, form,access_token);
    }

    @GetMapping("/{pid}/{spreadsheetId}/sheets")
    @ResponseBody
    public ResponseEntity<? extends Object> getSheets(@PathVariable Integer pid,@PathVariable String spreadsheetId,
            @Nullable @RequestParam String name, @Nullable @RequestParam Integer id,
            @Nullable @RequestParam String access_token) {
        return (ResponseEntity<? extends Object>) this.spreadsheetService.getSheets(pid,spreadsheetId, name, id,access_token);
    }

    @GetMapping("/{pid}/{spreadsheetId}/{sheetId}")
    @ResponseBody
    public ResponseEntity<? extends Object> getSheet(@PathVariable Integer pid,@PathVariable String spreadsheetId, @PathVariable Integer sheetId,
            @Nullable @ModelAttribute SheetForm form, @Nullable @RequestParam String access_token) {
        return (ResponseEntity<? extends Object>) this.spreadsheetService.getSheet(pid,spreadsheetId, sheetId, form,access_token);
    }

    @PutMapping("/{pid}/{spreadsheetId}/{sheetId}")
    @ResponseBody
    public ResponseEntity<? extends Object> updateSheet(@PathVariable Integer pid,@PathVariable String spreadsheetId,
            @PathVariable Integer sheetId, @RequestBody SheetForm form, @Nullable @RequestParam String access_token) {
        return (ResponseEntity<? extends Object>) this.spreadsheetService.updateSheet(pid,spreadsheetId, sheetId, form,access_token);
    }

    @DeleteMapping("/{pid}/{spreadsheetId}/{sheetId}")
    @ResponseBody
    public ResponseEntity<? extends Object> deleteSheet(@PathVariable Integer pid,@PathVariable String spreadsheetId,
            @PathVariable Integer sheetId, @Nullable @RequestParam String access_token) {
        return (ResponseEntity<? extends Object>) this.spreadsheetService.deleteSheet(pid,spreadsheetId, sheetId,access_token);
    }

    @DeleteMapping("/{pid}/{spreadsheetId}/{sheetId}/rows")
    public ResponseEntity<? extends Object> deleteRowsRequest(@PathVariable Integer pid,@PathVariable String spreadsheetId,
            @PathVariable Integer sheetId, @Nullable @RequestParam Integer start, @Nullable @RequestParam Integer end,
            @Nullable @RequestParam String access_token) {
        return (ResponseEntity<? extends Object>) this.spreadsheetService.deleteRowsRequest(pid,spreadsheetId, sheetId,
                start, end,access_token);
    }

    @DeleteMapping("/{pid}/{spreadsheetId}/{sheetId}/columns")
    public ResponseEntity<? extends Object> deleteColumnsRequest(@PathVariable Integer pid,@PathVariable String spreadsheetId,
            @PathVariable Integer sheetId, @Nullable @RequestParam Integer start, @Nullable @RequestParam Integer end,
            @Nullable @RequestParam String access_token) {
        return (ResponseEntity<? extends Object>) this.spreadsheetService.deleteColumnsRequest(pid,spreadsheetId, sheetId,
                start, end,access_token);
    }

    @GetMapping("/{pid}/{spreadsheetId}/{sheetId}/download")
    public ResponseEntity<?> downloadSheet(@PathVariable Integer pid,@PathVariable String spreadsheetId,@PathVariable Integer sheetId,@Nullable@RequestParam String access_token,@Nullable@RequestParam String u_token,
            HttpServletResponse response){
        return (ResponseEntity<? extends Object>) this.spreadsheetService.downloadSheet(pid,spreadsheetId,sheetId,access_token, null);
    }

}
