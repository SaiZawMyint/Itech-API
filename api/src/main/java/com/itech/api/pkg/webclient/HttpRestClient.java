package com.itech.api.pkg.webclient;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HttpRestClient {

    private String server;
    private RestTemplate rest;
    private HttpHeaders headers;
    private HttpStatusCode status;

    public HttpRestClient(String server) {
      this.server = server;
      this.rest = new RestTemplate();
      this.headers = new HttpHeaders();
//      headers.add("Content-Type", "application/json");
//      headers.add("Accept", "*/*");
    }

    public String get(String uri) {
      HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);
      ResponseEntity<String> responseEntity = rest.exchange(server + uri, HttpMethod.GET, requestEntity, String.class);
      this.setStatus(responseEntity.getStatusCode());
      return responseEntity.getBody();
    }
    
    public void download(String uri) throws UnsupportedEncodingException {
//        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//        headers.set("Content-Disposition", "attachment; filename="+System.currentTimeMillis()+".xlsx");
//        HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);

     // Optional Accept header
     RequestCallback requestCallback = request -> request.getHeaders()
             .setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));

     String params = URLEncoder.encode(uri, "UTF-8");
     System.out.println("Params : "+params);
     // Streams the response instead of loading it all in memory
     ResponseExtractor<Void> responseExtractor = response -> {
         // Here I write the response to a file but do what you like
         Path path = Paths.get("Downloads");
         Files.copy(response.getBody(), path);
         return null;
     };
     rest.execute(URI.create(server + params), HttpMethod.GET, requestCallback, responseExtractor);
//     return responseEntity.getBody();
      }

    public String post(String uri, String json) {   
      HttpEntity<String> requestEntity = new HttpEntity<String>(json, headers);
      ResponseEntity<String> responseEntity = rest.exchange(server + uri, HttpMethod.POST, requestEntity, String.class);
      this.setStatus(responseEntity.getStatusCode());
      return responseEntity.getBody();
    }

    public void put(String uri, String json) {
      HttpEntity<String> requestEntity = new HttpEntity<String>(json, headers);
      ResponseEntity<String> responseEntity = rest.exchange(server + uri, HttpMethod.PUT, requestEntity, String.class);
      this.setStatus(responseEntity.getStatusCode());   
    }

    public void delete(String uri) {
      HttpEntity<String> requestEntity = new HttpEntity<String>("", headers);
      ResponseEntity<String> responseEntity = rest.exchange(server + uri, HttpMethod.DELETE, requestEntity, String.class);
      this.setStatus(responseEntity.getStatusCode());
    }
    
}
