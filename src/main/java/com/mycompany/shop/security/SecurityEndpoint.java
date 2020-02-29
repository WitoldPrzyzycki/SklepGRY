package com.mycompany.shop.security;

import com.mycompany.shop.ejb.facades.AccountFacade;
import com.mycompany.shop.entitis.Account;
import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class SecurityEndpoint {

    @Inject
    private AccountFacade accountFacade;

    public Account findByLogin(String login) {

        return accountFacade.findByLogin(login);
    }

}
