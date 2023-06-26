package com.itech.api.persistence.entity;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "services")
@Getter
@Setter
public class Services {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column
    private String type;
    
    @Column
    private String name;
    
    @Column(name = "ref_id")
    private String refId;
    
    @Column(name = "link")
    private String link;
    
    @ManyToOne
    @JoinColumn(name="project_id",nullable = false)
    private Project project;
    
    @Column(name = "del_flag")
    private boolean delFlag;

    @Column(name = "created_at")
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Date updatedAt;
}
