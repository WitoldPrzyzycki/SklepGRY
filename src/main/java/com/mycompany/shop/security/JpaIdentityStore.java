package com.mycompany.shop.security;

import com.mycompany.shop.entitis.Account;
import com.mycompany.shop.entitis.AccountStatus;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;


@ApplicationScoped
public class JpaIdentityStore implements IdentityStore {

    @Inject
    private SecurityEndpoint securityEndpoint;

    @Override
    public Set<String> getCallerGroups(CredentialValidationResult validationResult) {
        return IdentityStore.super.getCallerGroups(validationResult);
    }

    @Override
    public CredentialValidationResult validate(Credential credential) {
        if (credential instanceof UsernamePasswordCredential) {
            UsernamePasswordCredential usernamePasswordCredential = (UsernamePasswordCredential) credential;
            try {
                Account account = securityEndpoint.findByLogin(usernamePasswordCredential.getCaller());
                String group = account.getRole();
                if ((usernamePasswordCredential.compareTo(account.getLogin(), account.getPassword()) 
                        && (account.getStatus().equals(AccountStatus.ACTIVE)))) {
                    return new CredentialValidationResult(account.getLogin(), new HashSet<>(Arrays.asList(group)));
                }
            } catch (NoResultException nre) {
                return CredentialValidationResult.INVALID_RESULT;
            }
        }
        return CredentialValidationResult.NOT_VALIDATED_RESULT;

    }

}
