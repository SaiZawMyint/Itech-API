package com.itech.api.pkg.google;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.itech.api.pkg.spreadsheet.tools.Property;
import com.itech.api.pkg.tools.enums.ResponseCode;
import com.itech.api.pkg.tools.exceptions.AuthException;

@SuppressWarnings({ "deprecation" })
public class GoogleConnection {
    private static final JacksonFactory FACTOURY = JacksonFactory.getDefaultInstance();

    public static Credential connect(Property props) throws IOException, GeneralSecurityException, AuthException {
        InputStream in = GoogleConnection.class.getResourceAsStream(props.getClientSecretPath());
        if (in == null) {
            throw new FileNotFoundException("Client secret file not found!");
        }

        if (props.getToken() != null && !props.getToken().isEmpty()) {
            TokenResponse tokenResponse = new TokenResponse();
            tokenResponse.setAccessToken(props.getToken());
            return createCredentialWithAccessTokenOnly(tokenResponse);
        }

        TokenResponse tokenResponse = getTokenResponse();
        if (tokenResponse == null) {
            Map<String, Object> causes = new HashMap<>();
            causes.put("message", "You need to provide your token first!");
            throw new AuthException(ResponseCode.UNAUTHORIZED, causes);
        }

        return createCredentialWithRefreshToken(GoogleNetHttpTransport.newTrustedTransport(), FACTOURY,
                loadClientSecretsResource(FACTOURY, new InputStreamReader(in)), tokenResponse);
    }

    protected static GoogleCredential createCredentialWithAccessTokenOnly(TokenResponse tokenResponse) {
        return new GoogleCredential().setFromTokenResponse(tokenResponse);
    }

    protected static Credential createCredentialWithRefreshToken(HttpTransport transport, JsonFactory jsonFactory,
            GoogleClientSecrets clientSecrets, TokenResponse tokenResponse) {
        return new GoogleCredential.Builder().setTransport(transport).setJsonFactory(jsonFactory)
                .setClientSecrets(clientSecrets).build().setFromTokenResponse(tokenResponse);
    }

    protected static GoogleClientSecrets loadClientSecretsResource(JsonFactory jsonFactory, InputStreamReader reader)
            throws IOException {
        return GoogleClientSecrets.load(jsonFactory, reader);
    }

    protected static TokenResponse getTokenResponse() throws StreamReadException, DatabindException, IOException {
        File file = new File("token.json");
        if (!file.exists())
            return null;

        @SuppressWarnings("unchecked")
        Map<String, Object> tokenData = new ObjectMapper().readValue(file, Map.class);
        TokenResponse tokenResponse = new TokenResponse();
        tokenData.forEach((k, v) -> {
            switch (k) {
            case "access_token":
                tokenResponse.setAccessToken((String) v);
                break;
            case "refresh_token":
                tokenResponse.setRefreshToken((String) v);
                break;
            case "expires_in":
                tokenResponse.setExpiresInSeconds(Long.parseLong((String) v));
                break;
            case "scope":
                tokenResponse.setScope((String) v);
                break;
            case "token_type":
                tokenResponse.setTokenType((String) v);
                break;
            }
        });
        return tokenResponse;
    }
}
