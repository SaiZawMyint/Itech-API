package com.itech.api.bl.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itech.api.bl.service.AuthService;
import com.itech.api.bl.service.ProjectService;
import com.itech.api.form.ProjectForm;
import com.itech.api.form.ProjectUploadForm;
import com.itech.api.form.response.ProjectResponse;
import com.itech.api.persistence.entity.Project;
import com.itech.api.persistence.entity.User;
import com.itech.api.pkg.tools.Response;
import com.itech.api.pkg.tools.enums.ResponseCode;
import com.itech.api.respositories.ProjectRepo;
import com.itech.api.respositories.UserRepository;
import com.itech.api.utils.CommonUtils;

import jakarta.transaction.Transactional;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    AuthService authService;

    @Autowired
    UserRepository userRepository;
    @Autowired
    ProjectRepo projectRepo;

    @Override
    public Object createProject(ProjectForm form) {
        if (form == null)
            return Response.send(ResponseCode.BAD_REQUEST, false, "Invalid client json!");
        if (form.getName() == null)
            return Response.send(ResponseCode.REQUIRED, false, "Project name required");
        if (form.getClientId() == null || form.getClientSecret() == null || form.getAuthURI() == null
                || form.getTokenURI() == null || form.getAuthProvider() == null || form.getAuthProvider() == null
                || (form.getRedirectURIs() == null && form.getRedirectURIs().size() == 0))
            return Response.send(ResponseCode.REQUIRED, false, "Invalid client data!");

        User user = authService.getLoggedUser(null);
        
        try {
            Project project = new Project(form);
            project.setUser(user);
            Project repo = this.projectRepo.save(project);
            ProjectResponse response = new ProjectResponse(repo);
            return Response.send(response, ResponseCode.REGIST_REQUEST_ACCEPT, true);
        } catch (Exception e) {
            return Response.send(ResponseCode.ERROR, false, "Project name already used!");
        }
    }

    @Transactional
    @Override
    public Object getProject() {
        User user=this.authService.getLoggedUser(null);
        if (user.getProjects().size() > 0) {
            List<ProjectResponse> projects = new ArrayList<>();
            for (Project p : user.getProjects()) {
                ProjectResponse pr = new ProjectResponse(p);
                projects.add(pr);
            }
            return Response.send(projects, ResponseCode.SUCCESS, true);
        } else {
            return Response.send(ResponseCode.EMPTY, true);
        }
    }

    @Transactional
    @Override
    public Object getProject(Integer id) {
        User user = this.authService.getLoggedUser(null);
        if (user.getProjects().size() > 0) {
            ProjectResponse pr = null;
            for (Project p : user.getProjects()) {
                if (p.getId().equals(id)) {
                    pr = new ProjectResponse(p);
                    break;
                }
            }
            if (pr == null) {
                return Response.send(ResponseCode.EMPTY, true);
            } else {
                return Response.send(pr, ResponseCode.SUCCESS, true);
            }
        } else {
            return Response.send(ResponseCode.EMPTY, true);
        }
    }

    @Transactional
    @Override
    public Object updateProject(Integer id, ProjectForm form) {
        if (form == null)
            return Response.send(ResponseCode.BAD_REQUEST, false, "Need at least one value to update!");
        Project project = this.getUserProject(id,null);
        if (project == null)
            return Response.send("Not project found!", ResponseCode.EMPTY, true);

        if (form.getName() != null)
            project.setName(form.getName());
        if (form.getClientId() != null)
            project.setClientId(form.getClientId());
        if (form.getClientSecret() != null)
            project.setClientSecret(form.getClientSecret());
        if (form.getAuthProvider() != null)
            project.setAuthProvider(form.getAuthProvider());
        if (form.getAuthURI() != null)
            project.setAuthURI(form.getAuthURI());
        if (form.getTokenURI() != null)
            project.setTokenURI(form.getTokenURI());
        if (form.getProjectId() != null)
            project.setProjectId(form.getProjectId());
        if (form.getRedirectURIs() != null)
            project.setRedirectURIs(CommonUtils.converListToString(form.getRedirectURIs()));

        Project updated = this.projectRepo.save(project);
        return Response.send(new ProjectResponse(updated), ResponseCode.UPDATE_SUCCESS, true);
    }

    @Transactional
    @Override
    public Object deleteProject(Integer id) {
        Project project = this.getUserProject(id,null);
        if (project == null)
            return Response.send("Not project to delete!", ResponseCode.EMPTY, true);
        this.projectRepo.delete(project);

        return Response.send("Delete success!", ResponseCode.DELETE, true);
    }

    @Transactional
    @Override
    public Project getUserProject(Integer id,String u_token) {
        User user = this.authService.getLoggedUser(u_token);;
        Project project = null;
        for (Project p : user.getProjects()) {
            if (p.getId().equals(id)) {
                project = p;
                break;
            }
        }
        return project;
    }

    @SuppressWarnings("unchecked")
    @Transactional
    @Override
    public Object uploadProject(ProjectUploadForm form) {
        try {
            Map<String, Object> map = new ObjectMapper().readValue(form.getFile().getBytes(), Map.class);
            System.out.println(map);
        } catch (Exception e) {

        }
        return null;
    }

    @Override
    @Transactional
    public Project getProjectData(Integer id) {
        User user = this.authService.getLoggedUser(null);
        if (user.getProjects().size() > 0) {
            Project pr = null;
            for (Project p : user.getProjects()) {
                if (p.getId().equals(id)) {
                    pr = p;
                    break;
                }
            }
            return pr;
        } else {
            return null;
        }
    }

    @SuppressWarnings("deprecation")
    @Transactional
    @Override
    public String getAccessToken(Integer id) {
        Project p = this.projectRepo.getById(id);
        if(p == null ) return null;
        if(p.getToken() == null) return null;
        return p.getToken().getAccessToken();
    }

}
