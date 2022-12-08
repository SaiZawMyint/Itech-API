package com.itech.api.persistence.entity;

import java.io.Serializable;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table( name = "role")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Role implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -4224501773075573799L;

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(unique = true)
    private String name;
    
    @OneToMany(mappedBy = "role")
    Set<User> users;
    
    public Role(String name) {
        this.name = name;
    }
    
    public Role(Integer id) {
        this.id = id;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}