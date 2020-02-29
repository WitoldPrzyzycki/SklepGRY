package com.mycompany.shop.ejb.facades;

import com.mycompany.shop.entitis.Genre;
import com.mycompany.shop.exceptions.GenreException;
import java.sql.SQLIntegrityConstraintViolationException;
import javax.annotation.security.RolesAllowed;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

@Stateless
@LocalBean
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class GenreFacade extends AbstractFacade<Genre> {

    @PersistenceContext(unitName = "com.mycompany_Shop_v0.1_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GenreFacade() {
        super(Genre.class);
    }

    public Genre findByName(String name) {
        TypedQuery<Genre> tq = em.createNamedQuery("Genre.findByName", Genre.class);
        tq.setParameter("name", name);
        return tq.getSingleResult();
    }

    @RolesAllowed("Employee")
    public void createGenre(Genre genre) throws GenreException {
        try {
            em.persist(genre);
            em.flush();
        } catch (PersistenceException pe) {
            if (pe.getCause().getCause() instanceof SQLIntegrityConstraintViolationException) {
                throw GenreException.createGenreExceptionWithDbCheckConstraintKey(pe);
            } else {
                throw pe;
            }
        }
    }
}
