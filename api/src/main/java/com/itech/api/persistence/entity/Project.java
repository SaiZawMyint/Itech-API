package com.itech.api.persistence.entity;

import java.util.Date;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.itech.api.form.ProjectForm;
import com.itech.api.utils.CommonUtils;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table( name = "projects")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(unique = true)
    private String name;
    
    @Column
    private String clientId;
    
    @Column
    private String clientSecret;
    
    @Column
    private String projectId;
    
    @Column
    private String authURI;
    
    @Column
    private String tokenURI;
    
    @Column
    private String authProvider;
    
    @Column
    private String redirectURIs;
    
    @ManyToOne
    @JoinColumn(name="user_id",nullable = false)
    private User user;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "token_id", referencedColumnName = "id")
    private Token token;
    
    @OneToMany(mappedBy = "project")
    private Set<Services> services;
    
    @Column(name = "created_at")
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Date updatedAt;
    
    public Project(ProjectForm form) {
        this.name = form.getName();
        this.clientId = form.getClientId();
        this.clientSecret = form.getClientSecret();
        this.projectId = form.getProjectId();
        this.authURI = form.getAuthURI();
        this.authProvider = form.getAuthProvider();
        this.tokenURI = form.getTokenURI();
        this.redirectURIs = CommonUtils.converListToString(form.getRedirectURIs());
    }
    
}
