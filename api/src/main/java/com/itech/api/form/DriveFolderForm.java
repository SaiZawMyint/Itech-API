package com.itech.api.form;

import java.util.List;

import lombok.Data;

@Data
public class DriveFolderForm {

    private String id;
    
    private String name;
    
    private String description;
    
    private List<String> parents;
    
}
