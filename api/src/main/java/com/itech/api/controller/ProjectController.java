package com.itech.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itech.api.bl.service.ProjectService;
import com.itech.api.form.ProjectForm;
import com.itech.api.form.ProjectUploadForm;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/itech/api/project")
public class ProjectController {

    @Autowired
    ProjectService projectService;
    
    @PostMapping("")
    public ResponseEntity<?> createProject(@Valid @RequestBody ProjectForm form){
        return (ResponseEntity<?>) this.projectService.createProject(form);
    }
    
    @PostMapping("/upload")
    public ResponseEntity<?> uploadProject(@RequestBody ProjectUploadForm form){
        return (ResponseEntity<?>) this.projectService.uploadProject(form);
    }
    
    @GetMapping("")
    public ResponseEntity<?> getProjects(){
        return (ResponseEntity<?>) this.projectService.getProject();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getProjectById(@PathVariable Integer id){
        return (ResponseEntity<?>) this.projectService.getProject(id);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(@PathVariable Integer id,@Nullable @RequestBody ProjectForm form){
        return (ResponseEntity<?>) this.projectService.updateProject(id,form);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Integer id){
        return (ResponseEntity<?>) this.projectService.deleteProject(id);
    }
}
