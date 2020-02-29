package com.mycompany.shop.ejb.facades;

import com.mycompany.shop.entitis.Genre;
import com.mycompany.shop.entitis.OrderStatus;
import com.mycompany.shop.entitis.Orders;
import com.mycompany.shop.entitis.Pegi;
import com.mycompany.shop.exceptions.OrderException;
import java.util.Date;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Stateless
@LocalBean
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class OrderFacade extends AbstractFacade<Orders> {

    @PersistenceContext(unitName = "com.mycompany_Shop_v0.1_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OrderFacade() {
        super(Orders.class);
    }

    @RolesAllowed("Client")
    public List<Orders> findPastOrdersForUser(String login) {
        TypedQuery<Orders> tq = em.createNamedQuery("Orders.findPastOrdersForUser", Orders.class);
        tq.setParameter("status", OrderStatus.OPEN);
        tq.setParameter("login", login);
        return tq.getResultList();
    }
    @RolesAllowed("Admin")
    public List<Orders> findPlacedOrdersForUser(String login) {
        TypedQuery<Orders> tq = em.createNamedQuery("Orders.findByStatusAndLogin", Orders.class);
        tq.setParameter("status", OrderStatus.PLACED);
        tq.setParameter("login", login);
        return tq.getResultList();
    }

    @RolesAllowed("Employee")
    public List<Orders> findPlacedAndClosed() {
        TypedQuery<Orders> tq = em.createNamedQuery("Orders.findPlacedAndClosed", Orders.class);
        tq.setParameter("status", OrderStatus.OPEN);
        return tq.getResultList();
    }

    public List<Orders> findOpenOrders() {
        TypedQuery<Orders> tq = em.createNamedQuery("Orders.findByStatus", Orders.class);
        tq.setParameter("status", OrderStatus.OPEN);
        return tq.getResultList();
    }

    public List<Orders> findPlacedOrders() {
        TypedQuery<Orders> tq = em.createNamedQuery("Orders.findByStatus", Orders.class);
        tq.setParameter("status", OrderStatus.PLACED);
        return tq.getResultList();
    }

    @RolesAllowed({"Employee", "Client"})
    public List<Orders> findByConditions(String login, Date fromDate, Date toDate, List<OrderStatus> status, String title, List<Genre> genre, Pegi pegi, Integer minPrice, Integer maxPrice) {
        TypedQuery<Orders> tq = em.createNamedQuery("Orders.findByConditions", Orders.class);
        tq.setParameter("login", login);
        tq.setParameter("toPlacedAt", toDate);
        tq.setParameter("fromPlacedAt", fromDate);
        tq.setParameter("order_status", status);
        tq.setParameter("title", title);
        tq.setParameter("genre", genre);
        tq.setParameter("pegi", pegi);
        tq.setParameter("minprice", minPrice);
        tq.setParameter("maxprice", maxPrice);
        return tq.getResultList();
    }

    @RolesAllowed("Client")
    public Orders findOpenOrderForUser(String login) throws OrderException {
        TypedQuery<Orders> tq = em.createNamedQuery("Orders.findByStatusAndLogin", Orders.class);
        tq.setParameter("status", OrderStatus.OPEN);
        tq.setParameter("login", login);
        try {
            Orders order = tq.getSingleResult();
            return order;
        } catch (NoResultException nre) {
            throw OrderException.createOrderExeptionWithNoSuchOrder(nre);
        }
    }
}
