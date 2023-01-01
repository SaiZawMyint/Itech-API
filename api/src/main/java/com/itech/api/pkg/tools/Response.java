package com.itech.api.pkg.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.itech.api.common.FileUtils;
import com.itech.api.form.response.drive.DownloadResponse;
import com.itech.api.pkg.tools.enums.ResponseCode;

public class Response {
    public static ResponseEntity<?> send(ResponseCode code, boolean status) {
        return resolveResponse(code, code.getMessage(), null, status, null);
    }

    public static ResponseEntity<?> send(Object data, ResponseCode code, boolean status) {
        return resolveResponse(code, code.getMessage(), data, status, null);
    }

    public static ResponseEntity<?> send(Object data, ResponseCode code, boolean status, String message) {
        return resolveResponse(code, message, data, status, null);
    }

    public static ResponseEntity<?> send(ResponseCode code, boolean status, Object error) {
        return resolveResponse(code, code.getMessage(), null, status, error);
    }

    public static ResponseEntity<?> send(Integer statusCode, boolean status, String message, Object data) {
        Map<String, Object> responsebody = new HashMap<>();
        responsebody.put("ok", status);
        responsebody.put("code", statusCode);
        responsebody.put("message", message);
        responsebody.put("data", data);
        return ResponseEntity.status(statusCode).body(responsebody);
    }

    static ResponseEntity<?> resolveResponse(ResponseCode code, String message, Object data, boolean status,
            Object error) {
        Map<String, Object> response = new HashMap<>();
        response.put("ok", status);
        response.put("code", code);
        response.put("message", message);
        if (data != null)
            response.put("data", data);
        if (error != null)
            response.put("error", error);

        switch (code) {
        case EMPTY: {
            return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
        }
        case EMPTY_CONTENT: {
            return new ResponseEntity<Object>(response, HttpStatus.NO_CONTENT);
        }
        case DELETE: {
            return new ResponseEntity<Object>(response, HttpStatus.ACCEPTED);
        }
        case ERROR: {
            return new ResponseEntity<Object>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        case REQUIRED: {
            return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
        }
        case SUCCESS: {
            return new ResponseEntity<Object>(response, HttpStatus.OK);
        }
        case UPDATE_SUCCESS: {
            return new ResponseEntity<Object>(response, HttpStatus.CREATED);
        }
        case UNAUTHORIZED: {
            return new ResponseEntity<Object>(response, HttpStatus.UNAUTHORIZED);
        }
        case BAD_REQUEST: {
            return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
        }
        case REGIST_REQUEST_ACCEPT: {
            return new ResponseEntity<Object>(response, HttpStatus.CREATED);
        }
        case CREATED: {
            return new ResponseEntity<Object>(response, HttpStatus.CREATED);
        }
        case SPREADSHEET_CREATED: {
            return new ResponseEntity<Object>(response, HttpStatus.CREATED);
        }
        case SHEET_CREATED: {
            return new ResponseEntity<Object>(response, HttpStatus.CREATED);
        }
        case SPREADSHEET_IMPORT: {
            return new ResponseEntity<Object>(response, HttpStatus.CREATED);
        }
        case DRIVE_FOLDER_IMPORT: {
            return new ResponseEntity<Object>(response, HttpStatus.CREATED);
        }
        case REQUIRED_AUTH: {
            return new ResponseEntity<Object>(response, HttpStatus.FORBIDDEN);
        }
        case TOKEN_EXPIRED: {
            return new ResponseEntity<Object>(response, HttpStatus.UNAUTHORIZED);
        }
        case DOWNLOAD: {
            try {
                ByteArrayOutputStream stream = ((DownloadResponse) data).getOutputStream();
                File file = FileUtils.downloadFile(stream, ((DownloadResponse) data).getFile().getName());
                stream.flush();
                stream.close();
                
                InputStreamResource res = new InputStreamResource(new FileInputStream(file));
                return ResponseEntity.ok()
                        .header("Access-Control-Expose-Headers", "file-name")
                        .header("file-name", ((DownloadResponse) data).getFile().getName())
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+((DownloadResponse) data).getFile().getName()+"\"")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .contentLength(file.length())
                        .body(res);
            } catch (Exception e) {
                e.printStackTrace();
                send(ResponseCode.ERROR, false, e);
            }

        }
        default:
            break;
        }
        return null;
    }

}
