package com.mycompany.shop.web;

import com.mycompany.shop.dto.AccountDTO;
import com.mycompany.shop.ejb.endpoints.AccountEndpoint;
import com.mycompany.shop.entitis.AccountStatus;
import com.mycompany.shop.exceptions.AccountException;
import com.mycompany.shop.security.HashGenerator;
import com.mycompany.shop.security.Sha256;
import com.mycompany.shop.utils.ContextUtils;
import java.io.Serializable;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

@RequestScoped
@Named("registerBean")
public class RegisterBean implements Serializable {

    @Inject
    private AccountEndpoint accountEndpoint;
    @Inject
    @Sha256
    private HashGenerator hashGenerator;
    private String login;
    private String password;
    private String passwordConfirmation;

    private AccountDTO accountDto;

    public RegisterBean() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = hashGenerator.generateHash(password);
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = hashGenerator.generateHash(passwordConfirmation);
    }

    public AccountDTO getAccountDto() {
        return accountDto;
    }

    public void setAccountDto(AccountDTO accountDto) {
        this.accountDto = accountDto;
    }

    private Boolean passwordCompare() {
        if (password.equals(passwordConfirmation)) {
            return true;
        }
        return false;
    }

    public String register() {

        if (!passwordCompare()) {
            ContextUtils.emitInternationalizedMessage(null, "error.password.repeat.not.match");
            return "";
        }
        accountDto = new AccountDTO(login, password, "Client", AccountStatus.ACTIVE);
        try {
            accountEndpoint.register(accountDto);
        } catch (AccountException ae) {
            ContextUtils.emitInternationalizedMessage(null, ae.getMessage());
            return "";
        }
        return "login";
    }
}
