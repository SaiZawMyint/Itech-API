package com.itech.api.bl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itech.api.bl.service.AuthService;
import com.itech.api.bl.service.ProjectService;
import com.itech.api.form.ProjectForm;
import com.itech.api.persistence.entity.Project;
import com.itech.api.persistence.entity.User;
import com.itech.api.pkg.tools.Response;
import com.itech.api.pkg.tools.enums.ResponseCode;
import com.itech.api.respositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    AuthService authService;
    @Autowired
    UserRepository userRepository;
    
    @Override
    public Object createProject(ProjectForm form) {
        User user = authService.getLoggedUser();
        Project project = new Project(form);
        user.addProject(project);
        User u = userRepository.save(user);
        return Response.send(u, ResponseCode.REGIST_REQUEST_ACCEPT, true);
    }

}
