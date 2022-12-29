package com.itech.api.form.response.drive;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.api.services.drive.model.File;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileResponse {

    @JsonInclude(Include.NON_NULL)
    private String id;
    
    private String kind;
    
    private String mimeType;
    
    private String name;
    
    private String type;
    
    public FileResponse(File file) {
        this.id = file.getId();
        this.kind = file.getKind();
        this.mimeType = file.getMimeType();
        this.name = file.getName();
    }
}
