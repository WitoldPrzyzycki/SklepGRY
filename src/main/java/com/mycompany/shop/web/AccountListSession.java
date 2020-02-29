package com.mycompany.shop.web;

import com.mycompany.shop.dto.AccountDTO;
import com.mycompany.shop.ejb.endpoints.AccountEndpoint;
import com.mycompany.shop.entitis.AccountStatus;
import com.mycompany.shop.exceptions.AccountException;
import com.mycompany.shop.utils.ContextUtils;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import javax.inject.Named;

@SessionScoped
@Named("AccountListSession")
public class AccountListSession implements Serializable {

    @Inject
    private AccountEndpoint accountEndpoint;

    private List<AccountDTO> accountDTOs;
    private DataModel<AccountDTO> dataModelAccountDTOs;

    public AccountListSession() {
    }

    @PostConstruct
    public void init() {
        accountDTOs = accountEndpoint.findAll();
        dataModelAccountDTOs = new ListDataModel<>(accountDTOs);
    }

    public DataModel<AccountDTO> getDataModelAccountDTOs() {
        return dataModelAccountDTOs;
    }

    public void setDataModelAccountDTOs(DataModel<AccountDTO> dataModelAccountDTOs) {
        this.dataModelAccountDTOs = dataModelAccountDTOs;
    }

    public Boolean isAccountActive(AccountDTO accountDTO) {
        if (accountDTO.getStatus() != AccountStatus.ACTIVE) {
            return false;
        }
        return true;
    }

    public void findByConditions(String loginSearch, String roleSearch, List<AccountStatus> statusSearch) {
        accountDTOs = accountEndpoint.findByConditions(loginSearch, roleSearch, statusSearch);
        dataModelAccountDTOs = new ListDataModel<>(accountDTOs);
    }

    public String activateAccount(AccountDTO accountDTO) {
        try {
            accountDTO.setStatus(AccountStatus.ACTIVE);
            accountEndpoint.activateAccount(accountDTO);
            init();
            return "accountList";
        } catch (AccountException ae) {
            ContextUtils.emitInternationalizedMessage(null, ae.getMessage());
            init();
            return "";
        }
    }

    public String deactivateAccount(AccountDTO accountDTO) {
        try {
            accountDTO.setStatus(AccountStatus.INACTIVE);
            accountEndpoint.deactivateAccount(accountDTO);
            init();
            return "accountList";
        } catch (AccountException ae) {
            ContextUtils.emitInternationalizedMessage(null, ae.getMessage());
            init();
            return "";
        }
    }

    public String deleteAccount(AccountDTO accountDTO) {
        try {

            accountEndpoint.deleteAccount(accountDTO);
            init();
            return "accountList";
        } catch (AccountException ae) {
            ContextUtils.emitInternationalizedMessage(null, ae.getMessage());
            init();
            return "";
        }
    }
    public Boolean isThisMyAccount(AccountDTO accountDTO){
        return accountEndpoint.isThisMyAccount(accountDTO);
    }
}
