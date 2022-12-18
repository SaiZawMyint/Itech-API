package com.itech.api.bl.service;

import com.itech.api.form.ProjectForm;
import com.itech.api.form.ProjectUploadForm;
import com.itech.api.persistence.entity.Project;

public interface ProjectService {

    public Object createProject(ProjectForm form);

    public Object getProject();

    public Object getProject(Integer id);
    
    public Project getProjectData(Integer id);

    public Object updateProject(Integer id, ProjectForm form);

    public Object deleteProject(Integer id);
    
    public Project getUserProject(Integer id,String u_token);

    public Object uploadProject(ProjectUploadForm form);
    
    public String getAccessToken(Integer id);
    
}
