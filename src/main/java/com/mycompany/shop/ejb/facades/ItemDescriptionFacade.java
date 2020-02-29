package com.mycompany.shop.ejb.facades;

import com.mycompany.shop.entitis.Genre;
import com.mycompany.shop.entitis.ItemDescription;
import com.mycompany.shop.entitis.ItemDescriptionStatus;
import com.mycompany.shop.entitis.ItemStatus;
import com.mycompany.shop.entitis.Pegi;
import com.mycompany.shop.exceptions.ItemDescriptionException;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

@Stateless
@LocalBean
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class ItemDescriptionFacade extends AbstractFacade<ItemDescription> {

    @PersistenceContext(unitName = "com.mycompany_Shop_v0.1_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemDescriptionFacade() {
        super(ItemDescription.class);
    }

    @RolesAllowed({"Employee","Client"})
    public List<ItemDescription> findActive() {
        TypedQuery<ItemDescription> tq = em.createNamedQuery("ItemDescription.findActive", ItemDescription.class);
        tq.setParameter("status", ItemDescriptionStatus.DELETED);
        return tq.getResultList();
    }

    public List<ItemDescription> findActiveIds() {
        TypedQuery<ItemDescription> tq = em.createNamedQuery("ItemDescription.findActiveIds", ItemDescription.class);
        tq.setParameter("status", ItemStatus.ACTIVE);
        tq.setParameter("ides_status", ItemDescriptionStatus.ACTIVE);
        return tq.getResultList();
    }

    public List<ItemDescription> findActiveIdsByConditions(String titleSearch, List<Genre> genreSearch, Pegi pegiSearch, Integer minPrice, Integer maxPrice) {
        TypedQuery<ItemDescription> tq = em.createNamedQuery("ItemDescription.findActiveIdsByConditions", ItemDescription.class);
        tq.setParameter("status", ItemStatus.ACTIVE);
        tq.setParameter("ides_status", ItemDescriptionStatus.ACTIVE);
        tq.setParameter("title", titleSearch);
        tq.setParameter("genre", genreSearch);
        tq.setParameter("pegi", pegiSearch);
        tq.setParameter("minPrice", minPrice);
        tq.setParameter("maxPrice", maxPrice);
        return tq.getResultList();
    }

    @RolesAllowed("Employee")
    public List<ItemDescription> findByConditions(String titleSearch, List<Genre> genreSearchList, Pegi pegiSearch, List<ItemDescriptionStatus> status, Integer minPrice, Integer maxPrice) {
        TypedQuery<ItemDescription> tq = em.createNamedQuery("ItemDescription.findByConditions", ItemDescription.class);
        tq.setParameter("status", status);
        tq.setParameter("title", titleSearch);
        tq.setParameter("genre", genreSearchList);
        tq.setParameter("pegi", pegiSearch);
        tq.setParameter("minPrice", minPrice);
        tq.setParameter("maxPrice", maxPrice);
        return tq.getResultList();
    }

    @RolesAllowed("Employee")
    public void createItemDescription(ItemDescription entity) throws ItemDescriptionException {
        try {
            em.persist(entity);
            em.flush();
        } catch (PersistenceException pe) {
            throw ItemDescriptionException.createItemDescriptionExceptionWithDBConstraint(pe);
        } catch (ConstraintViolationException cve) {
            String message = "";
            for (ConstraintViolation cv : cve.getConstraintViolations()) {
                message = cv.getMessage();
            }
            throw new ItemDescriptionException(message);
        }
    }

}
