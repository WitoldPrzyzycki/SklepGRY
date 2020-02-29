package com.mycompany.shop.dto;

import com.mycompany.shop.entitis.AccountStatus;


public class AccountDTO {
    
    private String login;
    private String password;
    private String role;
    private AccountStatus status;
    
    
    public AccountDTO(String login, String password,String role, AccountStatus status) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.status = status;
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
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "AccountDTO{" + "login=" + login + '}';
    }
    
}
