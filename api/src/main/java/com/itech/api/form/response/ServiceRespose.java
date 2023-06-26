package com.itech.api.form.response;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.itech.api.persistence.entity.Services;

import lombok.Data;

@Data
public class ServiceRespose {

    @JsonInclude(Include.NON_NULL)
    private Integer id;
    
    @JsonInclude(Include.NON_NULL)
    private String type;

    @JsonInclude(Include.NON_NULL)
    private String name;

    @JsonInclude(Include.NON_NULL)
    private String refId;
    
    @JsonInclude(Include.NON_NULL)
    private String link;

    @JsonInclude(Include.NON_NULL)
    private Date createdAt;

    @JsonInclude(Include.NON_NULL)
    private Date updatedAt;
    
    public ServiceRespose(Services service) {
        this.id = service.getId();
        this.name = service.getName();
        this.type =service.getType();
        this.link = service.getLink();
        this.refId = service.getRefId();
        this.createdAt = service.getCreatedAt();
        this.updatedAt = service.getUpdatedAt();
    }
    
}
