package com.mycompany.shop.ejb.facades;

import com.mycompany.shop.ejb.interceptor.LoggingInterceptor;
import com.mycompany.shop.entitis.Account;
import com.mycompany.shop.entitis.AccountStatus;
import com.mycompany.shop.exceptions.AccountException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.security.RolesAllowed;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

@Stateless
@LocalBean
@Interceptors(LoggingInterceptor.class)
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class AccountFacade extends AbstractFacade<Account> {

    @PersistenceContext(unitName = "com.mycompany_Shop_v0.1_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AccountFacade() {
        super(Account.class);
    }

    @RolesAllowed("Admin")
    @Override
    public List<Account> findAll() {
        TypedQuery<Account> tq = em.createNamedQuery("Account.findAll", Account.class);
        tq.setParameter("status", AccountStatus.DELETED);
        return tq.getResultList();
    }

    public Account findByLogin(String login) {
            TypedQuery<Account> tq = em.createNamedQuery("Account.findByLogin", Account.class);
            tq.setParameter("login", login);
            tq.setParameter("status", AccountStatus.DELETED);
        try {
            return tq.getSingleResult();
        } catch (NoResultException nre) {
            StringBuilder sb = new StringBuilder("Nieudana próba logowania z użyciem loginu: ");
            sb.append(login);
            Logger.getLogger("AccountFacade").log(Level.INFO, sb.toString());
            return new Account();
        }
    }

    @RolesAllowed("Admin")
    public List<Account> findByConditions(String loginSearch, String roleSearch, List<AccountStatus> statusSearch) {
        TypedQuery<Account> tq = em.createNamedQuery("Account.findByConditions", Account.class);
        tq.setParameter("login", loginSearch);
        tq.setParameter("role", roleSearch);
        tq.setParameter("status", statusSearch);
        return tq.getResultList();
    }

    @RolesAllowed("Admin")
    public List<String> findRoleList() {
        TypedQuery<String> tq = em.createNamedQuery("Account.findRoleList", String.class);
        return tq.getResultList();
    }

    public void createAccount(Account entity) throws AccountException {
        try {
            em.persist(entity);
            em.flush();
        } catch (PersistenceException pe) {
            if (pe.getCause().getCause() instanceof SQLIntegrityConstraintViolationException) {
                throw AccountException.createAccountExeptionWithDbCheckConstraintKey(pe);
            } else {
                throw pe;
            }
        }
    }

}
