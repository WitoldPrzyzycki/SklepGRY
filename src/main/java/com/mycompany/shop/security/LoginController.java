package com.mycompany.shop.security;

import com.mycompany.shop.utils.ContextUtils;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.SecurityContext;
import static javax.security.enterprise.authentication.mechanism.http.AuthenticationParameters.withParams;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.Password;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Named("loginController")
@RequestScoped
public class LoginController {

    private String username;

    private String password;

    @Inject
    private SecurityContext securityContext;

    @Inject
    private FacesContext facesContext;

    @Inject
    @Sha256
    private HashGenerator hashGenerator;

    public String login() {
        Credential credential = new UsernamePasswordCredential(username, new Password(password));
        AuthenticationStatus status = securityContext.authenticate(getRequest(), getResponse(), withParams().credential(credential));
        if (status.equals(AuthenticationStatus.SEND_CONTINUE)) {
            facesContext.responseComplete();
            return "";
        } else if (status.equals(AuthenticationStatus.SEND_FAILURE)) {
            addError(facesContext, ContextUtils.getDefaultBundle().getString("login.fail"));
            return "";
        }
        return "main";
    }

    private HttpServletResponse getResponse() {
        return (HttpServletResponse) facesContext.getExternalContext().getResponse();
    }

    private HttpServletRequest getRequest() {
        return (HttpServletRequest) facesContext.getExternalContext().getRequest();
    }

    private void addError(FacesContext context, String message) {
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
            this.password = hashGenerator.generateHash(password);
    }
}
