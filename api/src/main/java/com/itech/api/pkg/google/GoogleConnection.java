package com.itech.api.pkg.google;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.itech.api.pkg.spreadsheet.tools.Property;

public class GoogleConnection {
    private static final JacksonFactory FACTOURY = JacksonFactory.getDefaultInstance();

    public static Credential connect(Property props) throws IOException, GeneralSecurityException {
        InputStream in = GoogleConnection.class.getResourceAsStream(props.getClientSecretPath());
        if (in == null) {
            throw new FileNotFoundException("Client secret file not found!");
        }

        GoogleClientSecrets clientSecret = GoogleClientSecrets.load(FACTOURY, new InputStreamReader(in));

        NetHttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();

        GoogleAuthorizationCodeFlow authFlow = new GoogleAuthorizationCodeFlow.Builder(transport, FACTOURY,
                clientSecret, props.getScope())
                        .setDataStoreFactory(new FileDataStoreFactory(new File(props.getStoreTokenPath())))
                        .setAccessType(props.getAccessType()).build();
        LocalServerReceiver reciever = new LocalServerReceiver.Builder().setPort(props.getPort())
                .setCallbackPath(props.getCallBack()).build();
        
        return new AuthorizationCodeInstalledApp(authFlow, reciever).authorize("user");
    }
}
