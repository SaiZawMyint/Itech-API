package com.itech.api.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.itech.api.form.GoogleClientForm;
import com.itech.api.pkg.tools.Response;
import com.itech.api.pkg.tools.enums.ResponseCode;
import com.itech.api.response.OauthResponse;
import com.itech.api.service.AuthService;
import com.itech.api.utils.PropertyUtils;

@Service
public class AuthServiceImpl implements AuthService {

    @Value("${google.auth.gurl}")
    private String GURL;

    @Value("${google.client.id}")
    private String clientId;

    @Override
    public Object requestCode() {
        GoogleClientForm client = PropertyUtils.getGoogleClientData();
        if (client == null)
            Response.send(ResponseCode.UNAUTHORIZED, false);

        String urlTemplate = this.urlTemplate(client);

        URI uri = null;
        try {
            uri = new URI(urlTemplate);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(uri);
        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
    }

    @Override
    public Object authorize(HttpHeaders header, String code) {
        GoogleClientForm client = PropertyUtils.getGoogleClientData();
        final String uri = "https://accounts.google.com/o/oauth2/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String clientCredentials = Base64.getEncoder()
                .encodeToString((client.getClientId() + ":" + client.getClientsecret()).getBytes());
        headers.add("Authorization", "Basic " + clientCredentials);
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("code", code);
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("redirect_uri", client.getRedirectUris().get(0));
        requestBody.add("scope", SheetsScopes.SPREADSHEETS);

        HttpEntity<MultiValueMap<String, String>> formEntity = new HttpEntity<MultiValueMap<String, String>>(
                requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<OauthResponse> response = restTemplate.exchange(uri, HttpMethod.POST, formEntity,
                OauthResponse.class);
        try {
            storeToken(response.getBody());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Response.send(response.getBody(), ResponseCode.SUCCESS, true);
    }

    @SuppressWarnings("unchecked")
    private String urlTemplate(GoogleClientForm client) {
        UriComponentsBuilder uri = UriComponentsBuilder.fromHttpUrl(GURL).queryParam("response_type", "code")
                .queryParam("scope", SheetsScopes.SPREADSHEETS).queryParam("redirect_uri", client.getRedirectUris())
                .queryParam("access_type", "offline").queryParam("client_id", client.getClientId());

        File token = new File("token.json");
        boolean refreshTokenExist = !token.exists();
        if (token.exists()) {
            try {
                Map<String, Object> tokenData = new ObjectMapper().readValue(token, Map.class);
                if (tokenData.containsKey("refresh_token")) {
                    refreshTokenExist = tokenData.get("refresh_token") != null
                            && ((String) tokenData.get("refresh_token")).length() > 0;
                } else {
                    refreshTokenExist = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                refreshTokenExist = false;
            }
        }
        if (!refreshTokenExist)
            uri.queryParam("prompt", "consent");

        return uri.encode().toUriString();
    }

    @SuppressWarnings("unchecked")
    private void storeToken(OauthResponse response) throws IOException {
        String json = new ObjectMapper().writeValueAsString(response);
        File file = new File("token.json");
        if (file.exists()) {
            Map<String, Object> tokenData = new ObjectMapper().readValue(file, Map.class);
            tokenData.forEach((k, v) -> {
                switch (k) {
                case "access_token":
                    tokenData.put("access_token", response.getAccess_token());
                    break;
                case "expires_in":
                    tokenData.put("expires_in", response.getExpires_in());
                    break;
                case "scope":
                    tokenData.put("scope", response.getScope());
                    break;
                case "token_type":
                    tokenData.put("token_type", response.getToken_type());
                    break;
                case "id_token":
                    tokenData.put("id_token", response.getId_token());
                    break;
                }
            });
            json = new ObjectMapper().writeValueAsString(tokenData);
        }
        try {
            file.createNewFile();
            PrintWriter out = new PrintWriter(file);
            out.print(json);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
