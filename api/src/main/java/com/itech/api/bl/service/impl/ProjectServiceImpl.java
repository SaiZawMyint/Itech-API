package com.itech.api.bl.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itech.api.bl.service.AuthService;
import com.itech.api.bl.service.ProjectService;
import com.itech.api.common.ErrorResponse;
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
        User user = authService.getLoggedUser();
        Project project = new Project(form);
        project.setUser(user);
        try {
            Project repo = this.projectRepo.save(project);
            ProjectResponse response = new ProjectResponse(repo);
            return Response.send(response, ResponseCode.REGIST_REQUEST_ACCEPT, true);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse();
            error.setCode(500);
            error.setError(
                    e instanceof DataIntegrityViolationException ? "Project name already used!" : e.getMessage());
            return Response.send(ResponseCode.ERROR, false, error);
        }

    }

    @Override
    public Object getProject() {
        User user = this.authService.getLoggedUser();
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

    @Override
    public Object getProject(Integer id) {
        User user = this.authService.getLoggedUser();
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

    @Override
    public Object updateProject(Integer id, ProjectForm form) {
        if (form == null)
            return Response.send(ResponseCode.BAD_REQUEST, false, "Need at least one value to update!");
        Project project = this.getUserProject(id);
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

    @Override
    public Object deleteProject(Integer id) {
        Project project = this.getUserProject(id);
        if (project == null)
            return Response.send("Not project to delete!", ResponseCode.EMPTY, true);
        this.projectRepo.delete(project);

        return Response.send("Delete success!", ResponseCode.DELETE, true);
    }

    @Override
    public Project getUserProject(Integer id) {
        User user = this.authService.getLoggedUser();
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
    @Override
    public Object uploadProject(ProjectUploadForm form) {
        try {
            Map<String, Object> map = new ObjectMapper().readValue(form.getFile().getBytes(), Map.class);
            System.out.println(map);
        }catch(Exception e) {
            
        }
        return null;
    }

}
