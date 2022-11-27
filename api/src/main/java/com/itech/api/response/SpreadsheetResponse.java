package com.itech.api.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.itech.api.form.SpreadsheetForm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpreadsheetResponse {
	@JsonInclude(Include.NON_NULL)
    private String sheetId;
    
    @JsonInclude(Include.NON_NULL)
    private String name;
    
    @JsonInclude(Include.NON_NULL)
    private String sheetName;
    
    @JsonInclude(Include.NON_NULL)
    private String range;
    
    @JsonInclude(Include.NON_NULL)
    private List<List<Object>> values;
    
    public SpreadsheetResponse(SpreadsheetForm form) {
        this.name = form.getName();
        this.range = form.getRange();
        this.sheetId = form.getSheetId();
        this.values = form.getValues();
        this.sheetName = form.getSheetName();
    }
}
