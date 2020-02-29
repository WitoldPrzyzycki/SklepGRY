package com.mycompany.shop.ejb.endpoints;

import com.mycompany.shop.dto.AccountDTO;
import com.mycompany.shop.ejb.facades.AccountFacade;
import com.mycompany.shop.ejb.facades.OrderFacade;
import com.mycompany.shop.ejb.interceptor.LoggingInterceptor;
import com.mycompany.shop.entitis.Account;
import com.mycompany.shop.entitis.AccountStatus;
import com.mycompany.shop.exceptions.AccountException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.LocalBean;
import javax.ejb.SessionSynchronization;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.OptimisticLockException;

@Stateful
@LocalBean
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@Interceptors(LoggingInterceptor.class)
public class AccountEndpoint extends AbstractEndpoint implements SessionSynchronization, Serializable {

    @Inject
    private AccountFacade accountFacade;

    @Inject
    private OrderFacade orderFacade;
    /**
     * Pole przechowuje listę kont których kopie jako instancje AccountDTO
     * zostały wysłane do warstwy prezentacji
     */
    private List<Account> accountList;

    /**
     * Podstawowa metoda do wyszukiwania wszystkich nie usuniętych kont
     *
     * @return Lista AccountDTO
     */
    @RolesAllowed("Admin")
    public List<AccountDTO> findAll() {
        accountList = accountFacade.findAll();
        List<AccountDTO> accountDTOs = accountDTOsGenerator(accountList);
        return accountDTOs;
    }

    /**
     * Prywatna metoda do wyszukiwania instancji Account dla podanego accountDTO
     * z pola klasy accountList
     *
     * @return instancję Account odpowiadającą accountDTO
     */
    private Account getAccountFromList(AccountDTO accountDTO) {
        Iterator<Account> accountItr = accountList.iterator();
        while (accountItr.hasNext()) {
            Account account = accountItr.next();
            if (Objects.equals(account.getLogin(), accountDTO.getLogin())) {
                return account;
            }
        }
        return null;
    }

    /**
     * Prywatna metoda generująca listę obiektów AccountDTO odpowiadającą
     * dostarczonej liście instancji Account
     *
     * @param accounts
     * @return listę AccountDTO
     */
    private List<AccountDTO> accountDTOsGenerator(List<Account> accounts) {
        List<AccountDTO> accountDTOs = new ArrayList<>();
        for (Account account : accounts) {
            AccountDTO accountDTO = new AccountDTO(
                    account.getLogin(),
                    account.getPassword(),
                    account.getRole(),
                    account.getStatus());
            accountDTOs.add(accountDTO);
        }
        return accountDTOs;
    }

    /**
     * Metoda do sprawdzania czy podane jako parametr AccountDTO odpowiada
     * koncie zalogowanego użytkownika
     *
     * @param accountDTO
     * @return true - jeśli konto się zgadza, false - jeśli to inne konto
     */
    @RolesAllowed("Admin")
    public Boolean isThisMyAccount(AccountDTO accountDTO) {
        String login = sctx.getCallerPrincipal().getName();
        if (login.equals(accountDTO.getLogin())) {
            return true;
        }
        return false;
    }

    /**
     * Metoda zwraca instancję Account dla podanego jako String loginu
     *
     * @param login
     * @return instancję Account
     */
    @RolesAllowed({"Admin", "Client"})
    public Account findByLogin(String login) {
        return accountFacade.findByLogin(login);
    }

    /**
     * Metoda rejestrująca klienta, przyjmuje jako parametr instancję AccountDTO
     * na podstawie której tworzy odpowiadającą jej instancję Account i wywołuje
     * metodę tworzącą z fasady
     *
     * @param accountDTO
     * @throws AccountException jeżeli konto o podanym loginie istnieje
     */
    public void register(AccountDTO accountDTO) throws AccountException {
        Account account = new Account(accountDTO.getLogin(), accountDTO.getPassword(), "Client", accountDTO.getStatus());
        accountFacade.createAccount(account);
    }

    /**
     * Metoda rejestruje konta o roli Admin lub Client, przyjmuje jako parametr
     * instancję AccountDTO na podstawie której tworzy odpowiadającą jej
     * instancję Account i wywołuje metodę tworzącą z fasady
     *
     * @param accountDTO
     * @throws AccountException jeżeli konto o podanym loginie istnieje
     */
    @RolesAllowed("Admin")
    public void createAccount(AccountDTO accountDTO) throws AccountException {
        Account account = new Account(accountDTO.getLogin(), accountDTO.getPassword(), accountDTO.getRole(), accountDTO.getStatus());
        accountFacade.createAccount(account);
    }

    /**
     * Metoda zwracająca listę ról zdeklarowanych w aplikacji jako String
     *
     * @return listę String
     */
    @RolesAllowed("Admin")
    public List<String> findRoleList() {
        return accountFacade.findRoleList();
    }

    /**
     * Metoda zwracająca listę AccountDTO spełniających warunki przekazane jako
     * parametry
     *
     * @param loginSearch String
     * @param roleSearch String
     * @param statusSearch lista AccountStatus
     * @return lista AccountDTO
     */
    @RolesAllowed("Admin")
    public List<AccountDTO> findByConditions(String loginSearch, String roleSearch, List<AccountStatus> statusSearch) {
        accountList = accountFacade.findByConditions(loginSearch, roleSearch, statusSearch);
        return accountDTOsGenerator(accountList);
    }

    /**
     * Metoda ustawiająca status konta na aktywne, jako parametr przyjmuje
     * AccountDTO
     *
     * @param accountDTO
     * @throws AccountException jeżeli pojawi się OptimisticLockException
     * podczas utrwalania danych
     */
    @RolesAllowed("Admin")
    public void activateAccount(AccountDTO accountDTO) throws AccountException {
        Account account = getAccountFromList(accountDTO);
        try {
            account.setStatus(AccountStatus.ACTIVE);
            accountFacade.edit(account);
            accountFacade.flush();
        } catch (EJBTransactionRolledbackException etre) {
            if (etre.getCause().getCause() instanceof OptimisticLockException) {
                throw AccountException.createAccountExeptionWithOptimisticLock(etre);
            } else {
                throw etre;
            }
        }
    }

    /**
     * Metoda ustawiaja status konta na nieaktywne jeżli nie posiada otwartych
     * zamówień, jako parametr przyjmuje AccountDTO
     *
     * @param accountDTO
     * @throws AccountException jeżeli pojawi się OptimisticLockException
     * podczas utrwalania danych
     */
    @RolesAllowed("Admin")
    public void deactivateAccount(AccountDTO accountDTO) throws AccountException {
        Account account = getAccountFromList(accountDTO);
        if (orderFacade.findPlacedOrdersForUser(account.getLogin()).isEmpty()) {
            try {
                account.setStatus(AccountStatus.INACTIVE);
                accountFacade.edit(account);
                accountFacade.flush();
            } catch (EJBTransactionRolledbackException etre) {
                if (etre.getCause().getCause() instanceof OptimisticLockException) {
                    throw AccountException.createAccountExeptionWithOptimisticLock(etre);
                } else {
                    throw etre;
                }
            }
        } else {
            throw AccountException.createAccountExeptionWithPlacedOrderWarning();
        }
    }

    /**
     * Metoda ustawiaja status konta na usunięte jeżli nie posiada otwartych
     * zamówień, jako parametr przyjmuje AccountDTO
     *
     * @param accountDTO
     * @throws AccountException eżeli pojawi się OptimisticLockException podczas
     * utrwalania danych
     */
    @RolesAllowed("Admin")
    public void deleteAccount(AccountDTO accountDTO) throws AccountException {
        Account account = getAccountFromList(accountDTO);
        if (orderFacade.findPlacedOrdersForUser(account.getLogin()).isEmpty()) {
            try {
                account.setStatus(AccountStatus.DELETED);
                accountFacade.edit(account);
                accountFacade.flush();
            } catch (EJBTransactionRolledbackException etre) {
                if (etre.getCause().getCause() instanceof OptimisticLockException) {
                    throw AccountException.createAccountExeptionWithOptimisticLock(etre);
                } else {
                    throw etre;
                }
            }
        } else {
            throw AccountException.createAccountExeptionWithPlacedOrderWarning();
        }
    }
}
