package com.itech.api.form;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectUploadForm {

    private String name;
    private MultipartFile file;
    
}
