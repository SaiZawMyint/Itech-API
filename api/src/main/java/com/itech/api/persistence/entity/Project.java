package com.itech.api.persistence.entity;

import com.itech.api.form.ProjectForm;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table( name = "porjects")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column
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
    
    public Project(ProjectForm form) {
        this.name = form.getName();
        this.clientId = form.getClientId();
        this.clientSecret = form.getClientSecret();
        this.authURI = form.getAuthURI();
        this.authProvider = form.getAuthProvider();
        this.tokenURI = form.getTokenURI();
        this.redirectURIs = form.getRedirectURIs().toString();
    }
    
}
