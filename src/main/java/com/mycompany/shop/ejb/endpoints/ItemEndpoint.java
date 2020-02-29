package com.mycompany.shop.ejb.endpoints;

import com.mycompany.shop.dto.GenreDTO;
import com.mycompany.shop.dto.ItemDTO;
import com.mycompany.shop.dto.ItemDescriptionDTO;
import com.mycompany.shop.dto.OrderDTO;
import com.mycompany.shop.ejb.facades.ItemDescriptionFacade;
import com.mycompany.shop.ejb.facades.ItemFacade;
import com.mycompany.shop.ejb.facades.OrderFacade;
import com.mycompany.shop.ejb.interceptor.LoggingInterceptor;
import com.mycompany.shop.entitis.Item;
import com.mycompany.shop.entitis.ItemDescription;
import com.mycompany.shop.entitis.ItemStatus;
import com.mycompany.shop.entitis.Orders;
import com.mycompany.shop.exceptions.ItemException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
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
public class ItemEndpoint extends AbstractEndpoint implements SessionSynchronization, Serializable {

    @Inject
    private ItemDescriptionFacade itemDescriptionFacade;
    @Inject
    private OrderFacade orderFacade;
    @Inject
    private ItemFacade itemFacade;
    /**
     * Przechowuje listę objektów Item których kopie jako instancje ItemDTO
     * które zostały wysłane do warstwy prezentacji
     */
    private List<Item> itemsList = new ArrayList<>();

    /**
     * Metoda zapisuje listę objektów Item których kopie jako instancje ItemDTO
     * które zostały wysłane do warstwy prezentacji, jest ona zapełniana na
     * podstawie listy ItemItemDescriptionDTO
     *
     * @param itemDescriptionDTOs
     */
    public void itemsListInit(List<ItemDescriptionDTO> itemDescriptionDTOs) {
        itemsList.clear();
        Iterator<ItemDescriptionDTO> iddlitr = itemDescriptionDTOs.iterator();
        while (iddlitr.hasNext()) {
            ItemDescriptionDTO itemDescriptionDTO = iddlitr.next();
            Iterator<ItemDTO> itemItr = itemDescriptionDTO.getItemsDtoCollection().iterator();
            while (itemItr.hasNext()) {
                ItemDTO itemDTO = itemItr.next();
                Item item = itemFacade.find(itemDTO.getId());
                itemsList.add(item);
            }
        }
    }

    /**
     * Metoda zapisuje listę objektów Item których kopie jako instancje ItemDTO
     * które zostały wysłane do warstwy prezentacji, jest ona zapełniana na
     * podstawie listy ItemDTO zawartej w OrderDto dostarczonym jako parametr
     *
     * @param orderDTO
     */
    public void itemsListInit(OrderDTO orderDTO) {
        itemsList.clear();
        Iterator<ItemDTO> itemItr = orderDTO.getItemDTOs().iterator();
        while (itemItr.hasNext()) {
            ItemDTO itemDTO = itemItr.next();
            Item item = itemFacade.find(itemDTO.getId());
            itemsList.add(item);
        }
    }

    /**
     * Zwraca instancję Item odpowiadającą dostarczonemu ItemDTO z listy
     * przechowującej encje
     *
     * @param itemDTO
     * @return Item lub null gdy nie odnaleziono
     */
    private Item getItemFromList(ItemDTO itemDTO) {
        Iterator<Item> itemItr = itemsList.iterator();
        while (itemItr.hasNext()) {
            Item item = itemItr.next();
            if (Objects.equals(item.getId(), itemDTO.getId())) {
                return item;
            }
        }
        return null;
    }

    /**
     * Metoda generuje listę objektów ItemDTO odpowiadającą dostarczonej jako
     * parametr liście Item
     *
     * @param itemList
     * @return listę ItemDTO
     */
    public List<ItemDTO> itemDTOsListGenerator(List<Item> itemList) {
        List<ItemDTO> itemDtoList = new ArrayList<>();
        for (Item item : itemList) {
            itemDtoList.add(itemDtoGenerator(item));
        }
        Collections.reverse(itemDtoList);
        return itemDtoList;
    }

    /**
     * Metoda generuje objekt ItemDTO odpowiadający dostarczonemu jako parametr
     * obiektowi Item
     *
     * @param item
     * @return Item
     */
    private ItemDTO itemDtoGenerator(Item item) {
        ItemDescriptionDTO itemDescriptionDTO = new ItemDescriptionDTO(item.getItemDescription().getId(),
                item.getItemDescription().getTitle(),
                new GenreDTO(item.getItemDescription().getGenre().getId(), item.getItemDescription().getGenre().getName()),
                item.getItemDescription().getPegi(),
                item.getItemDescription().getStatus(),
                item.getItemDescription().getPrice());
        ItemDTO itemDTO = new ItemDTO(item.getId(), itemDescriptionDTO, item.getStatus(), item.getKeyValue(), item.getReservedAt());
        return itemDTO;
    }

    /**
     * Metoda usuwa pozycję klucza z listy w zamówieniu i zwalnia rezerwacje
     *
     * @param itemDTO
     * @param orderDTO
     * @throws ItemException jeżeli wystąpi blokada optymistyczna
     */
    @RolesAllowed("Client")
    public void removeFromOrder(ItemDTO itemDTO, OrderDTO orderDTO) throws ItemException {
        Item item = getItemFromList(itemDTO);
        Orders order = orderFacade.find(orderDTO.getId());
        if (null == item) {
            throw ItemException.createItemExceptionWithNullItem();
        }
        item.setOrder(null);
        itemDTO.setOrders(null);
        item.setStatus(ItemStatus.ACTIVE);
        itemDTO.setStatus(ItemStatus.ACTIVE);
        item.setReservedAt(null);
        itemDTO.setReservedAt(null);
        order.getItemList().remove(item);
        orderDTO.getItemDTOs().remove(itemDTO);
        try {
            itemFacade.edit(item);
            itemFacade.flush();
        } catch (EJBTransactionRolledbackException etre) {
            if (etre.getCause().getCause() instanceof OptimisticLockException) {
                throw ItemException.createItemExceptionWithOptimisticLock(etre);
            }
            throw etre;
        }
    }

    /**
     * Metoda wiąże ze sobą pozycję klucza i zamówienie rezerwując podaną
     * pozycję klucza
     *
     * @param itemDTO
     * @param orderDTO
     * @throws ItemException jeżeli wystąpi blokada optymistyczna
     */
    @RolesAllowed("Client")
    public void addtoOrder(ItemDTO itemDTO, OrderDTO orderDTO) throws ItemException {
        Item item = getItemFromList(itemDTO);
        Orders order = orderFacade.find(orderDTO.getId());
        if (null == item) {
            throw ItemException.createItemExceptionWithNullItem();
        }
        itemDTO.setOrders(orderDTO);
        item.setOrder(order);
        itemDTO.setStatus(ItemStatus.RESERVED);
        item.setStatus(ItemStatus.RESERVED);
        itemDTO.setReservedAt(new Date());
        item.setReservedAt(new Date());
        orderDTO.getItemDTOs().add(itemDTO);
        order.getItemList().add(item);
        try {
            itemFacade.edit(item);
            itemFacade.flush();
        } catch (EJBTransactionRolledbackException etre) {
            if (etre.getCause().getCause() instanceof OptimisticLockException) {
                throw ItemException.createItemExceptionWithOptimisticLock(etre);
            }
            throw etre;
        }
    }

    /**
     * Metoda zmienia status pozycji klucza na aktywny
     *
     * @param itemDTO
     * @throws ItemException jeżeli wystąpi blokada optymistyczna
     */
    @RolesAllowed("Employee")
    public void setItemActive(ItemDTO itemDTO) throws ItemException {
        Item item = getItemFromList(itemDTO);
        if (null == item) {
            throw ItemException.createItemExceptionWithNullItem();
        }
        itemDTO.setStatus(ItemStatus.ACTIVE);
        item.setStatus(ItemStatus.ACTIVE);
        try {
            itemFacade.edit(item);
            itemFacade.flush();
        } catch (EJBTransactionRolledbackException etre) {
            if (etre.getCause().getCause() instanceof OptimisticLockException) {
                throw ItemException.createItemExceptionWithOptimisticLock(etre);
            }
            throw etre;
        }

    }

    /**
     * Metoda zmienia status pozycji klucza na nieaktywny
     *
     * @param itemDTO
     * @throws ItemException jeżeli wystąpi blokada optymistyczna
     */
    @RolesAllowed("Employee")
    public void setItemInactive(ItemDTO itemDTO) throws ItemException {
        Item item = getItemFromList(itemDTO);
        if (null == item) {
            throw ItemException.createItemExceptionWithNullItem();
        }
        itemDTO.setStatus(ItemStatus.INACTIVE);
        item.setStatus(ItemStatus.INACTIVE);
        try {
            itemFacade.edit(item);
            itemFacade.flush();
        } catch (EJBTransactionRolledbackException etre) {
            if (etre.getCause().getCause() instanceof OptimisticLockException) {
                throw ItemException.createItemExceptionWithOptimisticLock(etre);
            }
            throw etre;
        }
    }

    /**
     * Metoda zmienia status pozycji klucza na usunięty
     *
     * @param itemDTO
     * @throws ItemException jeżeli wystąpi blokada optymistyczna
     */
    @RolesAllowed("Employee")
    public void setItemDeleted(ItemDTO itemDTO) throws ItemException {
        Item item = getItemFromList(itemDTO);
        if (null == item) {
            throw ItemException.createItemExceptionWithNullItem();
        }
        itemDTO.setStatus(ItemStatus.DELETED);
        item.setStatus(ItemStatus.DELETED);
        try {
            itemFacade.edit(item);
            itemFacade.flush();
        } catch (EJBTransactionRolledbackException etre) {
            if (etre.getCause().getCause() instanceof OptimisticLockException) {
                throw ItemException.createItemExceptionWithOptimisticLock(etre);
            }
            throw etre;
        }
    }

    /**
     * Metoda tworzy pozycję klucz dla tytułu o wartości i statusie podanym jako
     * parametry
     *
     * @param keyValue
     * @param itemDescriptionDto
     * @param status
     * @throws ItemException jeżeli dane przekazane jako parametry naruszają
     * ograniczenia nałożone na bazę, lub naruszają reguły walidacyjne zawarte w
     * klasie Item
     */
    @RolesAllowed("Employee")
    public void createItem(String keyValue, ItemDescriptionDTO itemDescriptionDto,
            ItemStatus status) throws ItemException {
        ItemDescription itemDescription = itemDescriptionFacade.find(itemDescriptionDto.getId());
        Item item = new Item(keyValue, itemDescription, status);
        itemDescription.getItemsList().add(item);
        itemFacade.createItem(item);
    }

}
