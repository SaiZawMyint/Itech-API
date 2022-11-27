package com.itech.api.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.api.services.sheets.v4.model.Sheet;
import com.itech.api.form.SheetForm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SheetResponse {
    @JsonInclude(Include.NON_NULL)
    private Integer sheetId;
    @JsonInclude(Include.NON_NULL)
    private String name;
    @JsonInclude(Include.NON_NULL)
    private String range;
    @JsonInclude(Include.NON_NULL)
    private List<List<Object>> values;
    
    public SheetResponse(SheetForm form) {
        this.sheetId = form.getSheetId();
        this.name = form.getName();
        this.range = form.getRange();
        this.values = form.getValues();
    }
    
    public SheetResponse(Sheet sheet) {
        this.sheetId = sheet.getProperties().getSheetId();
        this.name = sheet.getProperties().getTitle();
    }
}
