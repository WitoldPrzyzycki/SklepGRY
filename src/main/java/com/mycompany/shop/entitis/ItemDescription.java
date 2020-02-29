
package com.mycompany.shop.entitis;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
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
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Entity
@Table(name = "ITEM_DESCRIPTION")
@NamedQueries({
    @NamedQuery(name = "ItemDescription.findActive", query = "SELECT i FROM ItemDescription i  WHERE i.status <> :status")
    , @NamedQuery(name = "ItemDescription.findGenreList", query = "SELECT DISTINCT i.genre FROM ItemDescription i")
    , @NamedQuery(name = "ItemDescription.findPegiList", query = "SELECT DISTINCT i.pegi FROM ItemDescription i")
    , @NamedQuery(name = "ItemDescription.findByConditions", query = "SELECT i FROM ItemDescription i  "
            + "WHERE i.status in :status "
            + "AND i.title LIKE :title "
            + "AND i.genre IN :genre "
            + "AND i.pegi <= :pegi "
            + "AND :minPrice <= i.price "
            + "AND i.price <= :maxPrice ")
    , @NamedQuery(name = "ItemDescription.findActiveIds", query = "SELECT DISTINCT ides FROM Item i "
            + "JOIN i.itemDescription ides "
            + "WHERE i.status = :status "
            + "AND ides.status = :ides_status")
    ,@NamedQuery(name = "ItemDescription.findActiveIdsByConditions", query = "SELECT DISTINCT ides FROM Item i "
            + "JOIN i.itemDescription ides "
            + "WHERE i.status = :status "
            + "AND ides.status = :ides_status "
            + "AND ides.title LIKE :title "
            + "AND ides.genre IN :genre "
            + "AND ides.pegi <= :pegi "
            + "AND :minPrice <= ides.price AND ides.price <= :maxPrice ")})
public class ItemDescription implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Long version;

    @NotNull(message = "constraint.title.length.notinrange2")
    @Size(min = 1, max = 200, message = "constraint.title.length.notinrange")
    @Column(name = "TITLE", nullable = false, unique = true)
    private String title;

    @NotNull
    @Column(name = "PEGI", nullable = false)
    private Pegi pegi;

    @NotNull
    @Column(name = "PRICE", nullable = false)
    private int price;

    @NotNull
    @Column(name = "STATUS", nullable = false)
    private ItemDescriptionStatus status;

    @OneToMany(mappedBy = "itemDescription")
    private List<Item> itemsList;

    @NotNull
    @JoinColumn(name = "GENRE_ID", referencedColumnName = "ID", nullable = false)
    @ManyToOne
    private Genre genre;

    public ItemDescription() {
    }

    public ItemDescription(String title, Genre genre, Pegi pegi, int price, ItemDescriptionStatus status) {
        this.title = title;
        this.pegi = pegi;
        this.price = price;
        this.status = status;
        this.genre = genre;
    }

    public ItemDescriptionStatus getStatus() {
        return status;
    }

    public void setStatus(ItemDescriptionStatus status) {
        this.status = status;
    }

    public Genre getGenre() {
        return genre;
    }

    public Pegi getPegi() {
        return pegi;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<Item> getItemsList() {
        return itemsList;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ItemDescription)) {
            return false;
        }
        ItemDescription other = (ItemDescription) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "id=" + id + " " + this.getTitle();
    }
}
