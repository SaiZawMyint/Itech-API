package com.itech.api.pkg.google;

import java.io.ByteArrayInputStream;
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
import com.itech.api.persistence.dto.TokenDTO;
import com.itech.api.pkg.spreadsheet.tools.Property;
import com.itech.api.pkg.tools.enums.ResponseCode;
import com.itech.api.pkg.tools.exceptions.AuthException;
import com.itech.api.utils.CommonUtils;

@SuppressWarnings({ "deprecation" })
public class GoogleConnection {
    private static final JacksonFactory FACTOURY = JacksonFactory.getDefaultInstance();

    public static Credential connect(Property props) throws IOException, GeneralSecurityException, AuthException {
        if (props.getToken() != null && !props.getToken().isEmpty()) {
            TokenResponse tokenResponse = new TokenResponse();
            tokenResponse.setAccessToken(props.getToken());
            return createCredentialWithAccessTokenOnly(tokenResponse);
        }

        TokenResponse tokenResponse = getTokenResponse(props.getTokenResource());
        if (tokenResponse == null) {
            Map<String, Object> causes = new HashMap<>();
            causes.put("message", "You need to provide your token first!");
            throw new AuthException(ResponseCode.UNAUTHORIZED, causes);
        }
        String clientSecret = new ObjectMapper().writeValueAsString(props.getProject());
        clientSecret= "{\"web\":"+clientSecret+"}";
        InputStream stream = new ByteArrayInputStream(clientSecret.getBytes());
        return createCredentialWithRefreshToken(GoogleNetHttpTransport.newTrustedTransport(), FACTOURY,
                loadClientSecretsResource(FACTOURY, new InputStreamReader(stream)), tokenResponse);
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

    protected static TokenResponse getTokenResponse(TokenDTO token)
            throws StreamReadException, DatabindException, IOException {
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(token.getAccess_token());
        tokenResponse.setRefreshToken(token.getRefresh_token());
        tokenResponse.setExpiresInSeconds(token.getExpires_in());
        tokenResponse.setScope(CommonUtils.converListToString(token.getScope()));
        tokenResponse.setTokenType(token.getToken_type());
        return tokenResponse;
    }
}
