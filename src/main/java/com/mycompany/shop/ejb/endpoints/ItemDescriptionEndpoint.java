package com.mycompany.shop.ejb.endpoints;

import com.mycompany.shop.dto.GenreDTO;
import com.mycompany.shop.dto.ItemDTO;
import com.mycompany.shop.dto.ItemDescriptionDTO;
import com.mycompany.shop.ejb.facades.ItemDescriptionFacade;
import com.mycompany.shop.ejb.facades.ItemFacade;
import com.mycompany.shop.ejb.facades.OrderFacade;
import com.mycompany.shop.ejb.interceptor.LoggingInterceptor;
import com.mycompany.shop.entitis.Genre;
import com.mycompany.shop.entitis.Item;
import com.mycompany.shop.entitis.ItemDescription;
import com.mycompany.shop.entitis.ItemDescriptionStatus;
import com.mycompany.shop.entitis.ItemStatus;
import com.mycompany.shop.entitis.Pegi;
import com.mycompany.shop.exceptions.ItemDescriptionException;
import com.mycompany.shop.utils.ContextUtils;
import java.io.Serializable;
import java.util.ArrayList;
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
public class ItemDescriptionEndpoint extends AbstractEndpoint implements SessionSynchronization, Serializable {

    @Inject
    private ItemDescriptionFacade itemDescriptionFacade;
    @Inject
    private GenreEndpoint genreEndpoint;
    @Inject
    private OrderFacade orderFacade;
    @Inject
    private ItemFacade itemFacade;
    @Inject
    private OrderEndpoint orderEndpoint;

    /**
     * Przechowuje listę objektów ItemDescription których kopie jako instancje
     * ItemDescriptionDTO które zostały wysłane do warstwy prezentacji
     */
    private List<ItemDescription> itemDescriptions;

    /**
     * Metoda zwraca listę ItemDescriptionDTO, jest to lista aktywnych i
     * nieaktywnych tytułów dostarczana w celu edycji do widoku listy gier dla
     * pracownika
     *
     * @return listę ItemDescriptionDTO
     */
    @RolesAllowed({"Employee","Client"})
    public List<ItemDescriptionDTO> employeeItemListInit() {
        List<ItemDescription> itemDescriptionList = itemDescriptionFacade.findActive();
        itemDescriptions = itemDescriptionList;
        return itemDescriptionDTOsListGenerator(itemDescriptionList);
    }

    /**
     * Metoda zwraca listę aktywnych ItemDescriptionDTO, jest to podstawowa
     * lista do wyświetlenia użytkownikom a roli Client
     *
     * @return listę ItemDescriptionDTO
     */
    public List<ItemDescriptionDTO> itemListInit() {
        List<ItemDescription> itemList = itemDescriptionFacade.findActiveIds();
        itemDescriptions = itemList;
        return itemDescriptionDTOsListGenerator(itemList);
    }

    /**
     * Metoda odnajdująca ItemDescription odpowiadające dostarczonemu jako
     * parametr ItemDescriptionDTO
     *
     * @param itemDescriptionDTO
     * @return ItemDescription lub null jeżeli takiego nie ma
     */
    private ItemDescription getItemDescriptionFromList(ItemDescriptionDTO itemDescriptionDTO) {
        Iterator<ItemDescription> idlitr = itemDescriptions.iterator();
        while (idlitr.hasNext()) {
            ItemDescription itemDescription = idlitr.next();
            if (Objects.equals(itemDescription.getId(), itemDescriptionDTO.getId())) {
                return itemDescription;
            }
        }
        return null;
    }

    /**
     * Metoda generująca listę obiektów ItemDescriptionDTO na podstawie
     * dostarczonej jako parametr listy ItemDescription
     *
     * @param itemDescriptionList
     * @return listę ItemDescriptionDTO
     */
    private List<ItemDescriptionDTO> itemDescriptionDTOsListGenerator(List<ItemDescription> itemDescriptionList) {
        List<ItemDescriptionDTO> ItemDescriptionDTOList = new ArrayList<>();
        for (ItemDescription itemDescription : itemDescriptionList) {
            List<Item> itemlist = itemDescription.getItemsList();
            List<ItemDTO> itemDtoList = new ArrayList<>();

            ItemDescriptionDTO itemDescriptionDTO = new ItemDescriptionDTO(itemDescription.getId(),
                    itemDescription.getTitle(),
                    new GenreDTO(itemDescription.getGenre().getId(), itemDescription.getGenre().getName()),
                    itemDescription.getPegi(),
                    itemDescription.getStatus(),
                    itemDescription.getPrice());
            for (Item item : itemlist) {
                if ((!ContextUtils.getContext().isUserInRole("Employee")) && (item.getStatus() == ItemStatus.ACTIVE)) {
                    ItemDTO itemDTO = new ItemDTO(item.getId(), itemDescriptionDTO, item.getStatus(), item.getKeyValue(), item.getReservedAt());
                    itemDtoList.add(itemDTO);
                } else if (ContextUtils.getContext().isUserInRole("Employee") && (item.getStatus() != ItemStatus.DELETED)) {
                    ItemDTO itemDTO = new ItemDTO(item.getId(), itemDescriptionDTO, item.getStatus(), item.getKeyValue(), item.getReservedAt());
                    itemDtoList.add(itemDTO);
                }
            }
            if (itemDtoList.isEmpty() && (!ContextUtils.getContext().isUserInRole("Employee"))) {
                continue;
            }
            itemDescriptionDTO.setItemsDtoList(itemDtoList);
            ItemDescriptionDTOList.add(itemDescriptionDTO);
        }
        return ItemDescriptionDTOList;
    }

    /**
     * Metoda zwraca listę ItemDescriptionDTO spełniających kryteria podane jako
     * parametry, jest to lista aktywnych i nieaktywnych tytułów dostarczana w
     * celu edycji do widoku listy gier dla pracownika
     *
     * @param titleSearch String z początkowym i końcowym znakiem "%"
     * @param genreDtoSearchList lista GenreDTO
     * @param pegiSearch Pegi, wynik będzie spełniał warunek x.pegi &#60=
     * pegiSearch
     * @param status lista ItemDescriptionStatus
     * @param minPrice Integer minimalna cena wynik =&#62 minPrice
     * @param maxPrice Integer maxymalna cena wynik &#60= maxPrice
     * @return lista ItemDescriptionDTO
     */
    @RolesAllowed("Employee")
    public List<ItemDescriptionDTO> findByConditions(String titleSearch,
            List<GenreDTO> genreDtoSearchList, Pegi pegiSearch, List<ItemDescriptionStatus> status,
            Integer minPrice, Integer maxPrice) {
        List<Genre> genreSearchList = new ArrayList<>();
        for (GenreDTO genreDTO : genreDtoSearchList) {
            Genre genre = genreEndpoint.findById(genreDTO.getId());
            genreSearchList.add(genre);
        }
        List<ItemDescription> itemDescriptionList = itemDescriptionFacade.findByConditions(titleSearch, genreSearchList, pegiSearch, status, minPrice, maxPrice);
        itemDescriptions = itemDescriptionList;
        return itemDescriptionDTOsListGenerator(itemDescriptionList);
    }

    /**
     * Metoda zwraca listę ItemDescriptionDTO spełniających kryteria podane jako
     * parametry, jest to lista aktywnych tytułów dostarczana w celu edycji do
     * widoku listy gier dla klienta
     *
     * @param titleSearch String z początkowym i końcowym znakiem "%"
     * @param genreDtoSearch lista GenreDTO
     * @param pegiSearch Pegi, wynik będzie spełniał warunek x.pegi &#60=
     * pegiSearch
     * @param minPrice Integer minimalna cena wynik =&#62 minPrice
     * @param maxPrice Integer maxymalna cena wynik &#60= maxPrice
     * @return lista ItemDescriptionDTO
     */
    public List<ItemDescriptionDTO> findActiveIdsByConditions(String titleSearch, List<GenreDTO> genreDtoSearch, Pegi pegiSearch, Integer minPrice, Integer maxPrice) {
        List<Genre> genreSearchList = new ArrayList<>();
        for (GenreDTO genreDTO : genreDtoSearch) {
            Genre genre = genreEndpoint.findById(genreDTO.getId());
            genreSearchList.add(genre);
        }
        List<ItemDescription> itemList = itemDescriptionFacade.findActiveIdsByConditions(titleSearch, genreSearchList, pegiSearch, minPrice, maxPrice);
        itemDescriptions = itemList;
        return itemDescriptionDTOsListGenerator(itemList);
    }

    /**
     * Metoda zmieniająca status opisu gry na aktywny
     *
     * @param itemDescriptionDTO
     * @throws ItemDescriptionException jeżeli wystąpi blokada optymistyczna lub
     * odpowiednie ItemDescription nie zostanie odnalezione
     */
    @RolesAllowed("Employee")
    public void setItemDescriptionActive(ItemDescriptionDTO itemDescriptionDTO) throws ItemDescriptionException {
        ItemDescription itemDescriptionToChange = getItemDescriptionFromList(itemDescriptionDTO);
        if (itemDescriptionToChange != null) {
            itemDescriptionToChange.setStatus(ItemDescriptionStatus.ACTIVE);
            itemDescriptionDTO.setStatus(ItemDescriptionStatus.ACTIVE);
            try {
                itemDescriptionFacade.edit(itemDescriptionToChange);
                itemDescriptionFacade.flush();
            } catch (EJBTransactionRolledbackException etre) {
                if (etre.getCause().getCause() instanceof OptimisticLockException) {
                    throw ItemDescriptionException.createItemDescriptionExceptionWithOptimisticLock(etre);
                } else {
                    throw etre;
                }
            }
        } else {
            throw ItemDescriptionException.createItemDescriptionExceptionWithNullItemDesc();
        }
    }

    /**
     * Metoda zmieniająca status opisu gry na nieaktywny
     *
     * @param itemDescriptionDTO
     * @throws ItemDescriptionException jeżeli wystąpi blokada optymistyczna lub
     * odpowiednie ItemDescription nie zostanie odnalezione
     */
    @RolesAllowed("Employee")
    public void setItemDescriptionInactive(ItemDescriptionDTO itemDescriptionDTO) throws ItemDescriptionException {
        ItemDescription itemDescriptionToChange = getItemDescriptionFromList(itemDescriptionDTO);
        if (itemDescriptionToChange != null) {
            itemDescriptionToChange.setStatus(ItemDescriptionStatus.INACTIVE);
            itemDescriptionDTO.setStatus(ItemDescriptionStatus.INACTIVE);
            try {
                itemDescriptionFacade.edit(itemDescriptionToChange);
                itemDescriptionFacade.flush();
            } catch (EJBTransactionRolledbackException etre) {
                if (etre.getCause().getCause() instanceof OptimisticLockException) {
                    throw ItemDescriptionException.createItemDescriptionExceptionWithOptimisticLock(etre);
                } else {
                    throw etre;
                }
            }
        } else {
            throw ItemDescriptionException.createItemDescriptionExceptionWithNullItemDesc();
        }
    }

    /**
     * Metoda zmieniająca status opisu gry na usunięty
     *
     * @param itemDescriptionDTO
     * @throws ItemDescriptionException jeżeli wystąpi blokada optymistyczna lub
     * odpowiednie ItemDescription nie zostanie odnalezione
     */
    @RolesAllowed("Employee")
    public void setItemDescriptionDeleted(ItemDescriptionDTO itemDescriptionDTO) throws ItemDescriptionException {
        ItemDescription itemDescriptionToChange = getItemDescriptionFromList(itemDescriptionDTO);
        if (itemDescriptionToChange != null) {
            List<Item> itemList = itemDescriptionToChange.getItemsList();
            for (Item item : itemList) {
                if ((ItemStatus.ACTIVE == item.getStatus()) || (ItemStatus.RESERVED == item.getStatus())) {
                    throw ItemDescriptionException.createItemDescriptionExceptionWithDeleteLock();
                }
            }
            itemDescriptionToChange.setStatus(ItemDescriptionStatus.DELETED);
            itemDescriptionDTO.setStatus(ItemDescriptionStatus.DELETED);
            try {
                itemDescriptionFacade.edit(itemDescriptionToChange);
                itemDescriptionFacade.flush();
            } catch (EJBTransactionRolledbackException etre) {
                if (etre.getCause().getCause() instanceof OptimisticLockException) {
                    throw ItemDescriptionException.createItemDescriptionExceptionWithOptimisticLock(etre);
                } else {
                    throw etre;
                }
            }
        } else {
            throw ItemDescriptionException.createItemDescriptionExceptionWithNullItemDesc();
        }
    }

    /**
     * Metoda tworzy nowy opis gry z podanami jako parametry wartościami pól,
     * następnie utrwala w warstwie składowania
     *
     * @param title
     * @param genreDTO
     * @param pegi
     * @param price
     * @param itemDescriptionStatus
     * @throws ItemDescriptionException jeżeli dane przekazane jako parametry
     * naruszają ograniczenia nałożone na bazę np. tytuł nie jest unikalny, lub
     * naruszają reguły walidacyjne zawarte w klasie ItemDescription
     */
    @RolesAllowed("Employee")
    public void createItemDescription(String title, GenreDTO genreDTO, Pegi pegi, int price, ItemDescriptionStatus itemDescriptionStatus) throws ItemDescriptionException {
        Genre genre = genreEndpoint.findById(genreDTO.getId());
        ItemDescription itemDescription = new ItemDescription(title, genre, pegi, price, itemDescriptionStatus);
        itemDescriptionFacade.createItemDescription(itemDescription);
    }

}
