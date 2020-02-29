package com.mycompany.shop.ejb.endpoints;

import com.mycompany.shop.dto.AccountDTO;
import com.mycompany.shop.dto.GenreDTO;
import com.mycompany.shop.dto.ItemDTO;
import com.mycompany.shop.dto.OrderDTO;
import com.mycompany.shop.ejb.facades.ItemFacade;
import com.mycompany.shop.ejb.facades.OrderFacade;
import com.mycompany.shop.ejb.interceptor.LoggingInterceptor;
import com.mycompany.shop.entitis.Account;
import com.mycompany.shop.entitis.Genre;
import com.mycompany.shop.entitis.Item;
import com.mycompany.shop.entitis.ItemStatus;
import com.mycompany.shop.entitis.OrderStatus;
import com.mycompany.shop.entitis.Orders;
import com.mycompany.shop.entitis.Pegi;
import com.mycompany.shop.exceptions.AppBaseException;
import com.mycompany.shop.exceptions.ItemException;
import com.mycompany.shop.exceptions.OrderException;
import com.mycompany.shop.utils.ContextUtils;
import com.mycompany.shop.utils.mail.MailServiceBean;
import com.mycompany.shop.utils.mail.messageBuilders.MessageBuilder;
import com.mycompany.shop.utils.mail.messageBuilders.OrderMessageBuilder;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
public class OrderEndpoint extends AbstractEndpoint implements SessionSynchronization, Serializable {

    @Inject
    private ItemFacade itemFacade;
    @Inject
    private AccountEndpoint accountEndpoint;
    @Inject
    private ItemEndpoint itemEndpoint;
    @Inject
    private OrderFacade orderFacade;
    @Inject
    private GenreEndpoint genreEndpoint;
    @Inject
    private MailServiceBean mailService;

    /**
     * Przechowuje listę objektów Orders których kopie jako instancje OrderDTO
     * które zostały wysłane do warstwy prezentacji
     */
    private List<Orders> ordersList = new ArrayList<>();

    /**
     * Przechowuje objekt Orders o statusie otwartym, jest to kopia obiektu
     * Orders wysłanego do warstwy prezentacji. Reprezentuje on koszyk dla
     * użtkownika
     */
    private Orders openOrder;

    /**
     * Metoda zwraca listę OrderDTO, w zależności od roli wywołującego: klient -
     * lista zamówień wywołującego, pracownik - lista wszystkich złożonych i
     * zrealizowanych zamówień
     *
     * @return listę OrderDTO
     */
    @RolesAllowed({"Employee", "Client"})
    public List<OrderDTO> listInit() {
        if (ContextUtils.getContext().isUserInRole("Client")) {
            String login = sctx.getCallerPrincipal().getName();
            List<Orders> orderList = orderFacade.findPastOrdersForUser(login);
            ordersList = orderList;
            return orderDTOsListGenerator(orderList);
        } else if (ContextUtils.getContext().isUserInRole("Employee")) {
            List<Orders> orderList = orderFacade.findPlacedAndClosed();
            ordersList = orderList;
            return orderDTOsListGenerator(orderList);
        }
        return new ArrayList<>();
    }

    /**
     * Metoda zwraca listę obiektów OrderDTO odpowiadającą liście Orders
     * dostarczonej jako parametr
     *
     * @param orderList
     * @return listę OrderDTO
     */
    private List<OrderDTO> orderDTOsListGenerator(List<Orders> orderList) {
        List<OrderDTO> orderDtoList = new ArrayList<>();
        for (Orders order : orderList) {
            OrderDTO orderDTO = orderDtoGenerator(order);
            orderDtoList.add(orderDTO);
        }
        return orderDtoList;
    }

    /**
     * Metoda generje obiekt OrderDTO odpowiadający dostarczonemu jako parametr
     * obiektowi Orders
     *
     * @param order
     * @return Orders
     */
    private OrderDTO orderDtoGenerator(Orders order) {
        List<Item> itemList = order.getItemList();
        if (null == itemList) {
            itemList = new ArrayList<>();
        }
        List<ItemDTO> itemDTOs = itemEndpoint.itemDTOsListGenerator(itemList);
        AccountDTO accountDTO = new AccountDTO(order.getAccount().getLogin(),
                order.getAccount().getPassword(),
                order.getAccount().getRole(),
                order.getAccount().getStatus());
        OrderDTO orderDTO = new OrderDTO(order.getId(), order.getStatus(), accountDTO, order.getPlacedAt(), itemDTOs);
        return orderDTO;
    }

    /**
     * Metoda zwracająca listę obiektów OrderDTO spełniających podane jako
     * parametry kryteria
     *
     * @param login String z początkowym i końcowym znakiem "%"
     * @param fromDate data początkowa
     * @param toDate data końcowa
     * @param status lista statusów zamówienia
     * @param title tytuł gry w zamówieniu String z początkowym i końcowym
     * znakiem "%"
     * @param genreDtoSearchList lista GenreDTO
     * @param pegi Pegi, wynik będzie spełniał warunek x.pegi &#60= pegiSearch
     * @param minPrice Integer minimalna cena wynik =&#62 minPrice
     * @param maxPrice Integer maxymalna cena wynik &#60= maxPrice
     * @return listę OrderDTO
     */
    @RolesAllowed({"Employee", "Client"})
    public List<OrderDTO> findByConditions(String login, Date fromDate, Date toDate, List<OrderStatus> status, String title, List<GenreDTO> genreDtoSearchList, Pegi pegi, Integer minPrice, Integer maxPrice) {
        String loginSearch;
        if (ContextUtils.getContext().isUserInRole("Client")) {
            loginSearch = sctx.getCallerPrincipal().getName();
        } else {
            loginSearch = login;
        }
        List<Genre> genreSearchList = new ArrayList<>();
        for (GenreDTO genreDTO : genreDtoSearchList) {
            Genre genre = genreEndpoint.findById(genreDTO.getId());
            genreSearchList.add(genre);
        }
        List<Orders> orderList = orderFacade.findByConditions(loginSearch, fromDate, toDate, status, title, genreSearchList, pegi, minPrice, maxPrice);
        ordersList = orderList;
        return orderDTOsListGenerator(orderList);
    }

    /**
     * Metoda zwraca obiekt OrderDTO o statusie otwarte dla zalogowanego
     * użytkownika reprezentujący koszyk
     *
     * @return OrderDTO
     */
    @RolesAllowed("Client")
    public OrderDTO findOpenOrderForUser() {
        try {
            String login = sctx.getCallerPrincipal().getName();
            Orders order = orderFacade.findOpenOrderForUser(login);
            openOrder = order;
            OrderDTO orderDTO = orderDtoGenerator(order);
            return orderDTO;
        } catch (OrderException e) {
            OrderDTO orderDTO = createOrder();
            return orderDTO;
        }
    }

    /**
     * Metoda tworzy zamówienie i zwraca OrderDTO o statusie otwartym, związany
     * z wywołującym reprezentujący koszyk
     *
     * @return OrderDTO
     */
    @RolesAllowed("Client")
    private OrderDTO createOrder() {
        String login = sctx.getCallerPrincipal().getName();
        Account account = accountEndpoint.findByLogin(login);
        Orders order = new Orders(OrderStatus.OPEN, account);
        orderFacade.create(order);
        orderFacade.flush();
        OrderDTO orderDto = findOpenOrderForUser();
        return orderDto;
    }

    /**
     * Zwraca instancję Orders odpowiadającą dostarczonemu OrderDTO z listy
     * przechowującej encje
     *
     * @param orderDTO
     * @return Orders
     */
    private Orders getOrderFromList(OrderDTO orderDTO) {
        Iterator<Orders> oItr = ordersList.iterator();
        while (oItr.hasNext()) {
            Orders order = oItr.next();
            if (Objects.equals(order.getId(), orderDTO.getId())) {
                return order;
            }
        }
        return null;
    }

    /**
     * Metoda zmienia status na złożone i dodaje datę do zamówienia (koszyka)
     *
     * @param orderDTO
     * @throws OrderException jeżeli wystąpi blokada optymistyczna
     */
    @RolesAllowed("Client")
    public void placeOrder(OrderDTO orderDTO) throws OrderException {
        try {
            Date date = new Date();
            Orders order = openOrder;
            order.setStatus(OrderStatus.PLACED);
            orderDTO.setStatus(OrderStatus.PLACED);
            order.setPlacedAt(date);
            orderDTO.setPlacedAt(date);
            orderFacade.edit(order);
            orderFacade.flush();

            MessageBuilder messageBuilder = OrderMessageBuilder.createOrderPlacedConfirmationMessage(order);
            mailService.sendMessage(messageBuilder);

        } catch (EJBTransactionRolledbackException etre) {
            if (etre.getCause().getCause() instanceof OptimisticLockException) {
                throw OrderException.createOrderExeptionWithOptimisticLock(etre);
            }
            throw etre;
        }
    }

    /**
     * Metoda zwalnia rezerwacje z kluczy zawartych w zamówieniu i je usuwa
     *
     * @param orderDTO
     * @throws AppBaseException ItemException, OrderException jeżeli wystąpi
     * blokada optymistyczna
     */
    @RolesAllowed({"Employee", "Client"})
    public void cancelOrder(OrderDTO orderDTO) throws AppBaseException {
        Orders order = getOrderFromList(orderDTO);
        MessageBuilder messageBuilder = OrderMessageBuilder.createOrderCancelConfirmationMessage(order);
        List<Item> itemList = order.getItemList();
        for (Item item : itemList) {
            try {
                item.setOrder(null);
                item.setReservedAt(null);
                item.setStatus(ItemStatus.ACTIVE);
                itemFacade.edit(item);
                itemFacade.flush();
            } catch (EJBTransactionRolledbackException etre) {
                if (etre.getCause().getCause() instanceof OptimisticLockException) {
                    throw ItemException.createItemExceptionWithOptimisticLock(etre);
                }
            }
        }
        try {
            orderFacade.remove(order);
            mailService.sendMessage(messageBuilder);
        } catch (EJBTransactionRolledbackException etre) {
            if (etre.getCause().getCause() instanceof OptimisticLockException) {
                throw OrderException.createOrderExeptionWithOptimisticLock(etre);
            }
            throw etre;
        }
    }

    /**
     * Metoda zmienia status zamówienia na zamknięte oraz kluczy w nim zawartych
     * na sprzedane
     *
     * @param orderDTO
     * @throws AppBaseException ItemException, OrderException jeżeli wystąpi
     * blokada optymistyczna
     */
    @RolesAllowed("Employee")
    public void realizeOrder(OrderDTO orderDTO) throws AppBaseException {
        Orders order = getOrderFromList(orderDTO);
        List<Item> itemList = order.getItemList();
        for (Item item : itemList) {
            try {
                item.setStatus(ItemStatus.SOLD);
                itemFacade.edit(item);
            } catch (EJBTransactionRolledbackException etre) {
                if (etre.getCause().getCause() instanceof OptimisticLockException) {
                    throw ItemException.createItemExceptionWithOptimisticLock(etre);
                }
            }
            try {
                order.setStatus(OrderStatus.CLOSED);
                orderDTO.setStatus(OrderStatus.CLOSED);
                orderFacade.edit(order);
            } catch (EJBTransactionRolledbackException etre) {
                if (etre.getCause().getCause() instanceof OptimisticLockException) {
                    throw OrderException.createOrderExeptionWithOptimisticLock(etre);
                }
                throw etre;
            }
        }
    }
}
