package com.mycompany.shop.entitis;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "ITEM", uniqueConstraints = @UniqueConstraint(columnNames = {"KEY_VALUE", "ITEM_DESCRIPTION_ID"}))
@NamedQueries({
    @NamedQuery(name = "Items.findByItemDescriptionAndStatus", query = "SELECT i FROM Item i WHERE i.itemDescription = :itemDescription AND i.status = :itemStatus")})
public class Item implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Long version;

    @NotNull
    @Size(min = 1, max = 200, message = "constraint.keyValue.length.notinrange")
    @Column(name = "KEY_VALUE", nullable = false)
    private String keyValue;

    @NotNull
    @Enumerated
    @Column(name = "STATUS", nullable = false)
    private ItemStatus status;

    @Column(name = "RESERVED_AT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date reservedAt;

    @JoinColumn(name = "ITEM_DESCRIPTION_ID", referencedColumnName = "ID", nullable = false)
    @ManyToOne
    private ItemDescription itemDescription;

    @JoinColumn(name = "ORDERS_ID", referencedColumnName = "ID")
    @ManyToOne
    private Orders orders;

    public Item() {
    }

    public Item(String keyValue, ItemDescription itemDescription, ItemStatus status) {
        this.keyValue = keyValue;
        this.itemDescription = itemDescription;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public Date getReservedAt() {
        return reservedAt;
    }

    public void setReservedAt(Date reservedAt) {
        this.reservedAt = reservedAt;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public ItemDescription getItemDescription() {
        return itemDescription;
    }

    public void setOrder(Orders order) {
        this.orders = order;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Item)) {
            return false;
        }
        Item other = (Item) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

//    @Override
//    public String toString() {
//        return "Item{" + "id=" + id + ", version=" + version + ", keyValue=" + keyValue + ", status=" + status + ", reservedAt=" + reservedAt + ", itemDescription=" + itemDescription + ", orders=" + orders + '}';
//    }

}
