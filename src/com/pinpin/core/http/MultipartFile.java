package com.pinpin.core.http;

import java.io.File;
import java.net.URLConnection;

/**
 * 
 * multipart/form-data file element
 * 
 * @author lixd186
 */
public class MultipartFile {
    private String name;
    private String filename;
    private File file;
    
    public MultipartFile(String name, File file) {
        this.name = name;
        this.file = file;
        
        if (file == null) {
            filename = "";
        } else {
            filename = file.getName();
        }
    }
    
    public MultipartFile(String name, String filename, File file) {
        this.name = name;
        this.filename = filename;
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public String getFilename() {
        return filename;
    }

    public File getFile() {
        return file;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setFile(File file) {
        this.file = file;
    }
    
    
    public String getContentType() {
        String contentType = URLConnection.guessContentTypeFromName(filename);
        
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
            
        return contentType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("name = ").append(name).append("; filename = ")
                .append(filename).append("; file = ").append(file.getAbsoluteFile());
        return sb.toString();
    }

}
