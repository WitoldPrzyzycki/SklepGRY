package com.mycompany.shop.entitis;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "ORDERS")
@NamedQueries({
    @NamedQuery(name = "Orders.findAll", query = "SELECT o FROM Orders o")
    , @NamedQuery(name = "Orders.findById", query = "SELECT o FROM Orders o WHERE o.id = :id")
    , @NamedQuery(name = "Orders.findByStatus", query = "SELECT o FROM Orders o WHERE o.status = :status")
    , @NamedQuery(name = "Orders.findPlacedAndClosed", query = "SELECT o FROM Orders o WHERE o.status <> :status")
    , @NamedQuery(name = "Orders.findPastOrdersForUser", query = "SELECT o FROM Orders o JOIN o.account a WHERE o.status <> :status AND a.login = :login")
    , @NamedQuery(name = "Orders.findByStatusAndLogin", query = "SELECT o FROM Orders o JOIN o.account a WHERE o.status = :status AND a.login = :login")
    , @NamedQuery(name = "Orders.findByPlacedAt", query = "SELECT o FROM Orders o WHERE o.placedAt = :placedAt")
    , @NamedQuery(name = "Orders.findByConditions", query = "SELECT DISTINCT o FROM Item i JOIN i.orders o JOIN i.itemDescription ides JOIN o.account a WHERE a.login LIKE :login AND o.placedAt <= :toPlacedAt AND :fromPlacedAt <= o.placedAt AND o.status in :order_status AND ides.title LIKE :title AND ides.genre IN :genre AND ides.pegi <= :pegi AND :minprice <= ides.price AND ides.price <= :maxprice")})
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "STATUS", nullable = false)
    private OrderStatus status;

    @Column(name = "PLACED_AT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date placedAt;

    @JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ID")
    @ManyToOne
    private Account account;

    @OneToMany(mappedBy = "orders")
    private List<Item> itemList;

    @Version
    @NotNull
    @Column(name = "VERSION", nullable = false)
    private long version;

    public Orders() {
    }

    public Orders(OrderStatus status, Account account) {
        this.status = status;
        this.account = account;
    }

    public Long getId() {
        return id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Date getPlacedAt() {
        return placedAt;
    }

    public void setPlacedAt(Date placedAt) {
        this.placedAt = placedAt;
    }

    public Account getAccount() {
        return account;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Orders)) {
            return false;
        }
        Orders other = (Orders) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
}
