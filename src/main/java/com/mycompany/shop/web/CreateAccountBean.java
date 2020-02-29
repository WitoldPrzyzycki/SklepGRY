
package com.mycompany.shop.web;

import com.mycompany.shop.dto.AccountDTO;
import com.mycompany.shop.ejb.endpoints.AccountEndpoint;
import com.mycompany.shop.entitis.AccountStatus;
import com.mycompany.shop.exceptions.AccountException;
import com.mycompany.shop.security.HashGenerator;
import com.mycompany.shop.utils.ContextUtils;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;


@RequestScoped
@Named("CreateAccountBean")
public class CreateAccountBean {

    @Inject
    private AccountListSession accountListSession;
    @Inject
    private AccountEndpoint accountEndpoint;
    @Inject
    private HashGenerator hashGenerator;
    private String role;
    private String login;
    private String password;
    private String passwordConfirmation;

    private AccountDTO accountDto;

    public CreateAccountBean() {
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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
        this.password = hashGenerator.generateHash(password);;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = hashGenerator.generateHash(passwordConfirmation);;
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
        } else if (null == role) {
            ContextUtils.emitInternationalizedMessage(null, "message.chose.role");
            return "";
        }
        accountDto = new AccountDTO(login, password, role, AccountStatus.ACTIVE);
        try {
            accountEndpoint.createAccount(accountDto);
            accountListSession.init();
            return "accountList";
        } catch (AccountException ae) {
            ContextUtils.emitInternationalizedMessage("regex", ae.getMessage());
            return "";
        }
    }
}
