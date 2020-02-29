
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
import javax.validation.constraints.Size;


@Entity
@Table(name = "GENRE")
@NamedQueries({
    @NamedQuery(name = "Genre.findAll", query = "SELECT g FROM Genre g")
    ,
    @NamedQuery(name = "Genre.findByName", query = "SELECT g FROM Genre g WHERE g.name LIKE :name")})
public class Genre implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Long version;

    @NotNull
    @Size(min = 1, max = 50, message = "constraint.genre.length.notinrange")
    @Column(name = "NAME", nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "genre")
    private List<ItemDescription> itemDescriptionsList;

    public Genre() {
    }

    public Genre(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

}
