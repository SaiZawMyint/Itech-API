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

    public static ResponseEntity<?> stream(ByteArrayOutputStream data, HttpHeaders headers){
        return ResponseEntity.status(HttpStatus.OK)
                .header("Content-Type", "video/mp4")
                .header("Content-Length", String.valueOf(data.size()))
                .headers(headers)
                .body(data.toByteArray());
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
        case REQUIRED, BAD_REQUEST: {
            return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
        }
        case SUCCESS: {
            return new ResponseEntity<Object>(response, HttpStatus.OK);
        }
        case UPDATE_SUCCESS, CREATED, REGIST_REQUEST_ACCEPT, SPREADSHEET_CREATED, SHEET_CREATED, SPREADSHEET_IMPORT, DRIVE_FOLDER_IMPORT: {
            return new ResponseEntity<Object>(response, HttpStatus.CREATED);
        }
        case UNAUTHORIZED, TOKEN_EXPIRED: {
            return new ResponseEntity<Object>(response, HttpStatus.UNAUTHORIZED);
        }
            case REQUIRED_AUTH: {
            return new ResponseEntity<Object>(response, HttpStatus.FORBIDDEN);
        }
            case DOWNLOAD: {
            try {
                assert data != null;
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
        case STREAMMING_VIDEO:{
            ByteArrayOutputStream stream = (ByteArrayOutputStream) data;
            assert stream != null;
            return ResponseEntity.status(HttpStatus.OK)
                  .header("Content-Type", "video/mp4")
                  .header("Content-Length", String.valueOf(stream.size()))
//                  .header("Content-Type", "video/mp4")
//                  .header("Content-Type", "video/mp4")
                  .body(stream.toByteArray());
        }
        default:
            break;
        }
        return null;
    }

}
