package com.itech.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.itech.api.form.SpreadsheetForm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpreadsheetResponse {
	@JsonInclude(Include.NON_NULL)
    private String spreadsheetId;
    
    @JsonInclude(Include.NON_NULL)
    private String name;
    
    @JsonInclude(Include.NON_NULL)
    private String url;
    
    public SpreadsheetResponse(SpreadsheetForm form) {
        this.name = form.getName();
        this.spreadsheetId = form.getSpreadsheetId();
        this.name = form.getName();
    }

    public SpreadsheetResponse(Spreadsheet sheet) {
        this.spreadsheetId = sheet.getSpreadsheetId();
        this.name = sheet.getProperties().getTitle();
        this.url = sheet.getSpreadsheetUrl();
    }
}
