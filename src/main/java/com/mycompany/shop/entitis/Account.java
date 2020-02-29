package com.mycompany.shop.entitis;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(name = "ACCOUNT")
@NamedQueries({
    @NamedQuery(name = "Account.findAll", query = "SELECT a FROM Account a WHERE a.status != :status")
    , @NamedQuery(name = "Account.findById", query = "SELECT a FROM Account a WHERE a.id = :id")
    , @NamedQuery(name = "Account.findRoleList", query = "SELECT DISTINCT a.role FROM Account a")
    , @NamedQuery(name = "Account.findByLogin", query = "SELECT a FROM Account a WHERE a.login = :login AND a.status != :status")
    , @NamedQuery(name = "Account.findByConditions", query = "SELECT a FROM Account a WHERE a.login LIKE :login AND a.status IN :status AND a.role LIKE :role")
    , @NamedQuery(name = "Account.findByPassword", query = "SELECT a FROM Account a WHERE a.password = :password")
    , @NamedQuery(name = "Account.findByVersion", query = "SELECT a FROM Account a WHERE a.version = :version")})
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(name = "VERSION", nullable = false)
    private long version;

    @NotNull
    @Size(min = 4, max = 100)
    @Column(name = "LOGIN", nullable = false, unique = true, updatable = false)
    @Pattern(regexp="^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})$", message="login.validator.message")
    private String login;

    @NotNull
    @Size(min = 1, max = 500, message = "constraint.password.length.notinrange")
    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "ROLE", nullable = false, updatable = false)
    private String role;

    @NotNull
    @Column(name = "STATUS", nullable = false)
    private AccountStatus status;

    @OneToMany(mappedBy = "account")
    private List<Orders> ordersList;

    public Account() {
    }

    public Account(String login, String password, String role, AccountStatus status) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Account)) {
            return false;
        }
        Account other = (Account) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Account. Login: " + this.getLogin()
                + " Rola: " + this.getRole();
    }

}
