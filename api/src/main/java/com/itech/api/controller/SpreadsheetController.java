package com.itech.api.controller;

import java.util.Map;

import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.itech.api.form.SheetForm;
import com.itech.api.form.SpreadsheetForm;
import com.itech.api.service.SpreadsheetService;

@RestController
@RequestMapping("/itech/api/spreadsheet")
public class SpreadsheetController {

    @Autowired
    private SpreadsheetService spreadsheetService;

    @GetMapping("/")
    @ResponseBody
    public Map<?, ?> spreadSheetHome() {
        return (Map<?, ?>) this.spreadsheetService.getSpreadSheetDocumentation();
    }

    @PostMapping("/create")
    @ResponseBody
    public Map<?, ?> createSpreadsheet(@Nullable @RequestBody SpreadsheetForm form) {
        return (Map<?, ?>) this.spreadsheetService.createSpreadSheet(form);
    }

    @GetMapping("/{spreadsheetId}")
    @ResponseBody
    public Map<?, ?> getSpreadsheet(@PathVariable String spreadsheetId) {
        return (Map<?, ?>) this.spreadsheetService.getSpreadsheetData(spreadsheetId);
    }

    @PostMapping("/{spreadsheetId}")
    @ResponseBody
    public Map<?, ?> updateSpreadsheet(@PathVariable String spreadsheetId, @Nullable @RequestBody SpreadsheetForm form,
            @Nullable @RequestParam String fields) {
        return (Map<?, ?>) this.spreadsheetService.updateSpreadsheet(spreadsheetId, form);
    }
    
    @PostMapping("/{spreadsheetId}/sheets")
    @ResponseBody
    public Map<?, ?> addNewSheet(@PathVariable String spreadsheetId,@Nullable @RequestBody SheetForm form) {
        return (Map<?,?>) this.spreadsheetService.addNewSheet(spreadsheetId, form);
    }

    @GetMapping("/{spreadsheetId}/sheets")
    @ResponseBody
    public Map<?,?> getSheets(@PathVariable String spreadsheetId,@Nullable @RequestParam String name, @Nullable@RequestParam Integer id){
        return (Map<?,?>) this.spreadsheetService.getSheets(spreadsheetId,name,id);
    }
    
    @GetMapping("/{spreadsheetId}/{sheetId}")
    @ResponseBody
    public Map<?,?> getSheet(@PathVariable String spreadsheetId,@PathVariable Integer sheetId,@Nullable@ModelAttribute SheetForm form){
        return (Map<?,?>) this.spreadsheetService.getSheet(spreadsheetId, sheetId, form);
    }
    
    @PostMapping("/{spreadsheetId}/{sheetId}")
    @ResponseBody
    public Map<?,?> updateSheet(@PathVariable String spreadsheetId, @PathVariable Integer sheetId,@RequestBody SheetForm form){
        return (Map<?,?>) this.spreadsheetService.updateSheet(spreadsheetId,sheetId,form);
    }
    
    @DeleteMapping("/{spreadsheetId}/{sheetId}")
    @ResponseBody
    public Map<?,?> deleteSheet(@PathVariable String spreadsheetId, @PathVariable Integer sheetId){
        return (Map<?,?>) this.spreadsheetService.deleteSheet(spreadsheetId,sheetId);
    }
    
    @DeleteMapping("/{spreadsheetId}/{sheetId}/rows")
    public Map<?,?> deleteRowsRequest(@PathVariable String spreadsheetId,@PathVariable Integer sheetId, @Nullable @RequestParam Integer start,@Nullable @RequestParam Integer end){
        return (Map<?,?>) this.spreadsheetService.deleteRowsRequest(spreadsheetId,sheetId,start,end);
    }
    
    @DeleteMapping("/{spreadsheetId}/{sheetId}/columns")
    public Map<?,?> deleteColumnsRequest(@PathVariable String spreadsheetId,@PathVariable Integer sheetId, @Nullable @RequestParam Integer start,@Nullable @RequestParam Integer end){
        return (Map<?,?>) this.spreadsheetService.deleteColumnsRequest(spreadsheetId,sheetId,start,end);
    }
    
}
