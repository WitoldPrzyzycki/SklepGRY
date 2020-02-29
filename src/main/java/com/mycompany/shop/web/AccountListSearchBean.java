
package com.mycompany.shop.web;

import com.mycompany.shop.ejb.endpoints.AccountEndpoint;
import com.mycompany.shop.entitis.AccountStatus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;


@RequestScoped
@Named("AccountListSearchBean")
public class AccountListSearchBean {

    @Inject
    private AccountListSession accountListSession;
    @Inject
    private AccountEndpoint accountEndpoint;

    private String loginSearch;
    private String roleSearch;
    private AccountStatus statusSearch;
    private List<String> roleList;
    private List<AccountStatus> statusList;

    public AccountListSearchBean() {
    }

    @PostConstruct
    public void init() {
        roleList = accountEndpoint.findRoleList();
        Collections.sort(roleList);
        statusList = new ArrayList<>();
        statusList.add(AccountStatus.ACTIVE);
        statusList.add(AccountStatus.INACTIVE);
    }

    public String getLoginSearch() {
        return loginSearch;
    }

    public void setLoginSearch(String loginSearch) {
        this.loginSearch = "%" + loginSearch + "%";
    }

    public String getRoleSearch() {
        return roleSearch;
    }

    public void setRoleSearch(String roleSearch) {
        this.roleSearch = roleSearch;
    }

    public AccountStatus getStatusSearch() {
        return statusSearch;
    }

    public void setStatusSearch(AccountStatus statusSearch) {
        this.statusSearch = statusSearch;
    }

    public List<String> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<String> roleList) {
        this.roleList = roleList;
    }

    public List<AccountStatus> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<AccountStatus> statusList) {
        this.statusList = statusList;
    }

    public String findByConditions() {
        if (null == roleSearch) {
            roleSearch = "%";
        }
        List<AccountStatus> statusSearchList = new ArrayList<>();
        if (null == statusSearch) {
            statusSearchList.add(AccountStatus.ACTIVE);
            statusSearchList.add(AccountStatus.INACTIVE);
        } else {
            statusSearchList.add(statusSearch);
        }
        accountListSession.findByConditions(loginSearch, roleSearch, statusSearchList);
        return "accountList";
    }
}
