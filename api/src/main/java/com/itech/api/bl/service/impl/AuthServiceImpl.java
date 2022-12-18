package com.itech.api.bl.service.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.itech.api.bl.service.AuthService;
import com.itech.api.bl.service.ProjectService;
import com.itech.api.common.ErrorResponse;
import com.itech.api.form.AuthRequestForm;
import com.itech.api.form.AuthResponseForm;
import com.itech.api.form.GoogleClientForm;
import com.itech.api.form.UserForm;
import com.itech.api.form.response.TokenInfoResponse;
import com.itech.api.jwt.JwtUtil;
import com.itech.api.persistence.entity.Project;
import com.itech.api.persistence.entity.Role;
import com.itech.api.persistence.entity.Token;
import com.itech.api.persistence.entity.User;
import com.itech.api.pkg.tools.Response;
import com.itech.api.pkg.tools.enums.ResponseCode;
import com.itech.api.pkg.webclient.HttpRestClient;
import com.itech.api.response.OauthResponse;
import com.itech.api.respositories.ProjectRepo;
import com.itech.api.respositories.RoleRepository;
import com.itech.api.respositories.TokenRepository;
import com.itech.api.respositories.UserRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    @Value("${google.auth.gurl}")
    private String GURL;

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.tokenInfoURI}")
    private String TOKEN_INFO_URI;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    UserRepository userRepo;

    @Autowired
    RoleRepository roleRepo;

    @Autowired
    ProjectService projectService;

    @Autowired
    TokenRepository tokenRepo;

    @Autowired
    ProjectRepo projectRepo;

    @Override
    public Object loginUser(AuthRequestForm form) {
        try {
            Authentication authentication = authManager
                    .authenticate(new UsernamePasswordAuthenticationToken(form.getEmail(), form.getPassword()));

            User user = (User) authentication.getPrincipal();
            String accessToken = jwtUtil.generateAccessToken(user);
            AuthResponseForm response = new AuthResponseForm(user.getEmail(), accessToken);

            return Response.send(response, ResponseCode.SUCCESS, true);

        } catch (BadCredentialsException ex) {
            ex.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return Response.send(ResponseCode.BAD_REQUEST, false, error);
        }
    }

    @Override
    public Object requestServiceCode(String service, Integer projectId, String scopes, String u_token) {

        switch (service) {
        case "SPREADSHEET": {
            return this.requestSpreadsheetAccessCode(projectId, scopes, u_token);
        }
        default: {
            ErrorResponse err = new ErrorResponse();
            err.setCode(500);
            err.setError("Invalid service type!");
            return Response.send(err, ResponseCode.BAD_REQUEST, false);
        }
        }

    }

    @Override
    public Object authorize(Integer id, String service, String code) {
        switch (service) {
        case "SPREADSHEET": {
            return this.authorizeService(id, code);
        }
        default: {
            return Response.send(ResponseCode.BAD_REQUEST, false, "Unavailable service!");
        }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public Object registerUser(@Valid UserForm form) {
        User user = new User(form);
        Role role = roleRepo.getById(form.getRole());
        user.setRole(role);

        try {
            User savedUser = userRepo.save(user);
            Map<String, Object> response = new HashMap<>();
            response.put("email", savedUser.getEmail());
            response.put("role", roleRepo.findById(form.getRole()).get().getName());
            return Response.send(response, ResponseCode.REGIST_REQUEST_ACCEPT, true);
        } catch (Exception e) {
            e.printStackTrace();
            ErrorResponse error = new ErrorResponse();
            error.setCode(500);
            error.setError(e instanceof DataIntegrityViolationException ? "Username already used!" : e.getMessage());
            return Response.send(ResponseCode.ERROR, false, error);
        }
    }

    @Override
    public Object sendCode(String code) {
        return code;
    }

    @SuppressWarnings("deprecation")
    @Override
    public User getLoggedUser(String token) {
        UserDetails userDetails = token == null
                ? (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                : this.jwtUtil.getUserDetails(token);
        return this.userRepo.getById(((User) userDetails).getId());
    }

    @Override
    public Object status(Integer id, String access_token) {
        HttpRestClient client = new HttpRestClient(TOKEN_INFO_URI);
        Project project = this.projectService.getProjectData(id);
        if(project == null) {
            return Response.send(ResponseCode.BAD_REQUEST, false,"Invalid project!");
        }else if(project.getToken() == null) {
            return Response.send(ResponseCode.REQUIRED_AUTH, false);
        }
        access_token = access_token == null ? this.projectService.getAccessToken(id) : access_token;
        TokenInfoResponse tokenResponse = null;
        try {
            String reponse = client.get("?access_token=" + access_token);
            tokenResponse = new ObjectMapper().readValue(reponse, TokenInfoResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.send(ResponseCode.TOKEN_EXPIRED, false,"Token expired!");
        }
        return Response.send(tokenResponse, ResponseCode.SUCCESS, true);
    }

    private Object requestSpreadsheetAccessCode(Integer projectId, String scopes, String u_token) {
        Project project = this.projectService.getUserProject(projectId, u_token);
        if (project == null)
            return Response.send("Unavaliable project!", ResponseCode.BAD_REQUEST, false);

        GoogleClientForm client = new GoogleClientForm(project);
        String urlTemplate = this.urlTemplate(projectId, client, scopes, u_token);

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

    private String urlTemplate(Integer id, GoogleClientForm client, String scope, String u_token) {
        UriComponentsBuilder uri = UriComponentsBuilder.fromHttpUrl(GURL).queryParam("response_type", "code")
                .queryParam("scope", scope == null ? SheetsScopes.SPREADSHEETS : scope)
                .queryParam("redirect_uri", client.getRedirectUris()).queryParam("access_type", "offline")
                .queryParam("client_id", client.getClientId());

        Project project = this.projectService.getUserProject(id, u_token);

        boolean refreshTokenExist = project != null && project.getToken() != null;
        if (refreshTokenExist) {
            refreshTokenExist = project.getToken().getAccessToken() != null;
        }
        if (!refreshTokenExist)
            uri.queryParam("prompt", "consent");

        return uri.encode().toUriString();
    }

    private Object authorizeService(Integer id, String code) {
        Project project = this.projectService.getUserProject(id, null);
        if (project == null)
            return Response.send("Unavaliable project!", ResponseCode.BAD_REQUEST, false);

        GoogleClientForm client = new GoogleClientForm(project);

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
        Token token = new Token();
        token.setAccessToken(response.getBody().getAccess_token());
        token.setExpiresIn(Long.parseLong(response.getBody().getExpires_in()));
        token.setScope(response.getBody().getScope());
        token.setIdToken(response.getBody().getId_token());
        token.setTokenType(response.getBody().getToken_type());

        if (project.getToken() == null) {
            token.setRefreshToken(response.getBody().getRefresh_token());
        } else {
            token.setId(project.getToken().getId());
            token.setRefreshToken(project.getToken().getRefreshToken());
        }
        project.setToken(token);
        this.projectRepo.save(project);
        return Response.send(response.getBody(), ResponseCode.SUCCESS, true);
    }

}
