package com.itech.api.bl.service;

public interface Service {
    public Object deleteProjectService(Integer pid, String spreadsheetId, Boolean includeResource, String serviceType, String accessToken);
}
