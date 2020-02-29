package com.mycompany.shop.security;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.annotation.FacesConfig;
import javax.security.enterprise.authentication.mechanism.http.CustomFormAuthenticationMechanismDefinition;
import javax.security.enterprise.authentication.mechanism.http.LoginToContinue;

@FacesConfig
@ApplicationScoped
@CustomFormAuthenticationMechanismDefinition(
        loginToContinue = @LoginToContinue(
                loginPage = "/faces/login/login.xhtml",
                errorPage = "",
                useForwardToLogin = false
        ))
public class SecurityAppConfig {

}
