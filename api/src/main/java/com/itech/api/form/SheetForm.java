package com.itech.api.form;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SheetForm {

    private Integer sheetId;
    private String name;
    private String range;
    private List<List<Object>> values;

}
