package com.mycompany.shop.ejb.facades;

import com.mycompany.shop.entitis.Item;
import com.mycompany.shop.entitis.ItemDescription;
import com.mycompany.shop.entitis.ItemStatus;
import com.mycompany.shop.exceptions.ItemException;
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
public class ItemFacade extends AbstractFacade<Item> {

    @PersistenceContext(unitName = "com.mycompany_Shop_v0.1_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemFacade() {
        super(Item.class);
    }

    public List<Item> findActiveByItemDescription(ItemDescription itemDescription) {
        TypedQuery<Item> tq = em.createNamedQuery("Items.findByItemDescriptionAndStatus", Item.class);
        tq.setParameter("itemDescription", itemDescription);
        tq.setParameter("itemStatus", ItemStatus.ACTIVE);
        return tq.getResultList();
    }

    @RolesAllowed("Employee")
    public void createItem(Item item) throws ItemException {
        try {
            em.persist(item);
            em.flush();
        } catch (PersistenceException pe) {
            throw  ItemException.createItemExceptionWithDBConstaint(pe);
        } catch (ConstraintViolationException cve) {
            String message = "";
            for (ConstraintViolation cv : cve.getConstraintViolations()) {
                message = cv.getMessage();
            }
            throw new ItemException(message);
        }
    }
}
