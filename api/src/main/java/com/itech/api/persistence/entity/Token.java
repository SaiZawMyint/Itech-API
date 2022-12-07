package com.itech.api.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "token")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column
    private String accessToken;
    
    @Column
    private String refreshToken;
    
    @Column
    private Long expiresIn;
    
    @Column
    private String scope;
    
    @Column
    private String tokenType;
    
    @Column(nullable = true)
    private String idToken;
    
    @OneToOne(mappedBy = "token")
    private Project project;
    
}
