package com.itech.api.form;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SpreadsheetForm {
	private String sheetId;

    private String name;

    private String sheetName;

    private String range;

    private List<List<Object>> values;

}
