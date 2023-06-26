package com.itech.api.form.response.drive;

import java.io.ByteArrayOutputStream;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DownloadResponse {

    private FileResponse file;
    
    private ByteArrayOutputStream outputStream;
    
    private HttpServletResponse response;
    
}
