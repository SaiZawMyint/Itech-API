package com.itech.api.pkg.tools.auth;

import com.itech.api.pkg.google.form.GoogleCredentialForm;

public interface AuthServiceProvider {

    public String requestCode(String service,GoogleCredentialForm form);

    public Object authorize();
}
