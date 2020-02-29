
package com.mycompany.shop.web;

import com.mycompany.shop.dto.GenreDTO;
import com.mycompany.shop.ejb.endpoints.GenreEndpoint;
import com.mycompany.shop.ejb.endpoints.ItemDescriptionEndpoint;
import com.mycompany.shop.entitis.ItemDescriptionStatus;
import com.mycompany.shop.entitis.Pegi;
import com.mycompany.shop.exceptions.ItemDescriptionException;
import com.mycompany.shop.utils.ContextUtils;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import javax.inject.Named;


@RequestScoped
@Named("createItemDescriptionBean")
public class CreateItemDescriptionBean {

    @Inject
    private EmployeeItemListSession employeeItemListSession;
    @Inject
    private ItemListSession itemListSession;
    @Inject
    private ItemDescriptionEndpoint itemDescriptionEndpoint;
    @Inject
    private GenreEndpoint genreEndpoint;

    private String title;
    private String genre;
    private Pegi pegi;
    private int price;
    private ItemDescriptionStatus itemDescriptionStatus;
    private List<ItemDescriptionStatus> itemDescriptionStatusList;
    private List<GenreDTO> genreDToList;
    private DataModel<GenreDTO> dataModelGenreDTOs;
    private GenreDTO genreDTO;
    private List<Pegi> pegiList;

    @PostConstruct
    public void init() {
        pegiList = new ArrayList<>();
        Pegi[] ps = Pegi.values();
        for (Pegi p : ps) {
            pegiList.add(p);
        }
        List<ItemDescriptionStatus> idsl = new ArrayList<>();
        idsl.add(ItemDescriptionStatus.INACTIVE);
        idsl.add(ItemDescriptionStatus.ACTIVE);
        itemDescriptionStatusList = idsl;
        List<GenreDTO> genreDTOs = new ArrayList<>();
        genreDTOs = genreEndpoint.findAll();
        dataModelGenreDTOs = new ListDataModel<>(genreDTOs);
        genreDToList = genreDTOs;
        genre = "strategia";

    }

    public DataModel<GenreDTO> getDataModelGenreDTOs() {
        return dataModelGenreDTOs;
    }

    public void setDataModelGenreDTOs(DataModel<GenreDTO> dataModelGenreDTOs) {
        this.dataModelGenreDTOs = dataModelGenreDTOs;
    }

    public GenreDTO getGenreDTO() {
        return genreDTO;
    }

    public List<Pegi> getPegiList() {
        return pegiList;
    }

    public void setGenreDTO(GenreDTO genreDTO) {
        this.genreDTO = genreDTO;
    }

    public List<GenreDTO> getGenreDToList() {
        return genreDToList;
    }

    public void setGenreDToList(List<GenreDTO> genreDToList) {
        this.genreDToList = genreDToList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Pegi getPegi() {
        return pegi;
    }

    public void setPegi(Pegi pegi) {
        this.pegi = pegi;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public ItemDescriptionStatus getItemDescriptionStatus() {
        return itemDescriptionStatus;
    }

    public void setItemDescriptionStatus(ItemDescriptionStatus itemDescriptionStatus) {
        this.itemDescriptionStatus = itemDescriptionStatus;
    }

    public List<ItemDescriptionStatus> getItemDescriptionStatuses() {
        return itemDescriptionStatusList;
    }

    public void setItemDescriptionStatuses(List<ItemDescriptionStatus> itemDescriptionStatuses) {
        this.itemDescriptionStatusList = itemDescriptionStatuses;
    }

    public String createItemDescription() {
        try {
            itemDescriptionEndpoint.createItemDescription(title, genreDTO, pegi, price, itemDescriptionStatus);
            employeeItemListSession.init();
            itemListSession.init();
            return "employeeItemList";
        } catch (ItemDescriptionException ide) {
            ContextUtils.emitInternationalizedMessage(null, ide.getMessage());
            return "";
        }
    }
}
