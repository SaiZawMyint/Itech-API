package com.itech.api.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileUtils {

    public static final String USER_HOME = System.getProperty("user.home");
    
    public static File downloadFile(ByteArrayOutputStream contents,String filename) throws IOException {
        File dir = new File(USER_HOME+File.separator+"Itech/download");
        dir.mkdirs();
        File file = new File(USER_HOME+File.separator+"Itech/download"+File.separator+filename);
        if(!file.exists()) file.createNewFile();
        OutputStream stream = new FileOutputStream(file);
        contents.writeTo(stream);
        contents.close();
        return file;
    }
    
}
