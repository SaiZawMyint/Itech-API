package com.itech.api.controller;

import com.itech.api.bl.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;

@RestController
@RequestMapping("/itech/api/service")
@CrossOrigin
public class ServiceController {
    @Autowired
    private Service service;
    @DeleteMapping("/{pid}/{serviceId}")
    @ResponseBody
    public ResponseEntity<? extends Object> deleteSpreadsheet(@PathVariable Integer pid, @PathVariable String serviceId,
                                                              @Nullable @RequestParam Boolean includeResource,
                                                              @Nullable @RequestParam String access_token,
                                                              @RequestHeader(name = "Service-Type", required = true) String serviceType) {
        return (ResponseEntity<? extends Object>) this.service.deleteProjectService(pid,serviceId,includeResource, serviceType,access_token);
    }

}
