package com.mycompany.shop.web;

import com.mycompany.shop.dto.GenreDTO;
import com.mycompany.shop.dto.ItemDTO;
import com.mycompany.shop.dto.ItemDescriptionDTO;
import com.mycompany.shop.ejb.endpoints.ItemDescriptionEndpoint;
import com.mycompany.shop.ejb.endpoints.ItemEndpoint;
import com.mycompany.shop.entitis.ItemDescriptionStatus;
import com.mycompany.shop.entitis.ItemStatus;
import com.mycompany.shop.entitis.Pegi;
import com.mycompany.shop.exceptions.ItemDescriptionException;
import com.mycompany.shop.exceptions.ItemException;
import com.mycompany.shop.utils.ContextUtils;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import javax.inject.Named;

@SessionScoped
@Named("EmployeeItemListSession")
public class EmployeeItemListSession implements Serializable {

    @Inject
    private ItemListSession itemListSession;
    @Inject
    private ItemEndpoint itemEndpoint;
    @Inject
    private ItemDescriptionEndpoint itemDescriptionEndpoint;
    

    private List<ItemDescriptionDTO> itemDescriptionDTOs;
    private DataModel<ItemDescriptionDTO> dataModelItemDescriptionDTOs;
    private ItemDescriptionDTO itemDescriptionDTO;

    public ItemDescriptionDTO getItemDescriptionDTO() {
        return itemDescriptionDTO;
    }

    public void setItemDescriptionDTO(ItemDescriptionDTO itemDescriptionDTO) {
        this.itemDescriptionDTO = itemDescriptionDTO;
    }

    public DataModel<ItemDescriptionDTO> getDataModelItemDescriptionDTOs() {
        return dataModelItemDescriptionDTOs;
    }

    public void setDataModelItemDescriptionDTOs(DataModel<ItemDescriptionDTO> dataModelItemDescriptionDTOs) {
        this.dataModelItemDescriptionDTOs = dataModelItemDescriptionDTOs;
    }

    @PostConstruct
    public void init() {
        itemDescriptionDTOs = itemDescriptionEndpoint.employeeItemListInit();
        itemEndpoint.itemsListInit(itemDescriptionDTOs);
        dataModelItemDescriptionDTOs = new ListDataModel<>(itemDescriptionDTOs);
    }

    public void findIdsByConditions(String titleSearch, List<GenreDTO> genreDtoSearchList, Pegi pegiSearch, List<ItemDescriptionStatus> statusSearch, Integer minPrice, Integer maxPrice) {
        itemDescriptionDTOs = itemDescriptionEndpoint.findByConditions(titleSearch, genreDtoSearchList, pegiSearch, statusSearch, minPrice, maxPrice);
        dataModelItemDescriptionDTOs = new ListDataModel<>(itemDescriptionDTOs);
    }

    public Boolean isItemActive(ItemDTO itemDTO) {
        if (ItemStatus.ACTIVE == itemDTO.getStatus()) {
            return true;
        }
        return false;
    }

    public Boolean isItemInactive(ItemDTO itemDTO) {
        if (ItemStatus.INACTIVE == itemDTO.getStatus()) {
            return true;
        }
        return false;
    }

    public Boolean isItemReservedOrSold(ItemDTO itemDTO) {
        if ((ItemStatus.RESERVED == itemDTO.getStatus()) || (ItemStatus.SOLD == itemDTO.getStatus())) {
            return true;
        }
        return false;
    }

    public Boolean isItemDescriptionActive(ItemDescriptionDTO itemDescriptionDTO) {
        if (ItemDescriptionStatus.ACTIVE == itemDescriptionDTO.getStatus()) {
            return true;
        }
        return false;
    }

    public String setItemDescriptionActive(ItemDescriptionDTO itemDescriptionDTO) {
        try {
            itemDescriptionEndpoint.setItemDescriptionActive(itemDescriptionDTO);
            init();
            itemListSession.init();
            return "employeeItemList";
        } catch (ItemDescriptionException ide) {
            ContextUtils.emitInternationalizedMessage(null, ide.getMessage());
            init();
            itemListSession.init();
            return "";
        }
    }

    public String setItemDescriptionInctive(ItemDescriptionDTO itemDescriptionDTO) {
        try {
            itemDescriptionEndpoint.setItemDescriptionInactive(itemDescriptionDTO);
            init();
            itemListSession.init();
            return "employeeItemList";
        } catch (ItemDescriptionException ide) {
            ContextUtils.emitInternationalizedMessage(null, ide.getMessage());
            init();
            itemListSession.init();
            return "";
        }
    }

    public String setItemDescriptionDeleted(ItemDescriptionDTO itemDescriptionDTO) {
        try {
            itemDescriptionEndpoint.setItemDescriptionDeleted(itemDescriptionDTO);
            init();
            itemListSession.init();
            return "employeeItemList";
        } catch (ItemDescriptionException ie) {
            ContextUtils.emitInternationalizedMessage(null, ie.getMessage());
            init();
            itemListSession.init();
            return "";
        }
    }

    public String setItemActive(ItemDTO itemDTO) {
        try {
            itemEndpoint.setItemActive(itemDTO);
            init();
            itemListSession.init();
            return "employeeItemList";
        } catch (ItemException ie) {
            ContextUtils.emitInternationalizedMessage(null, ie.getMessage());
            init();
            itemListSession.init();
            return "";
        }
    }

    public String setItemInactive(ItemDTO itemDTO) {
        try {
            itemEndpoint.setItemInactive(itemDTO);
            init();
            itemListSession.init();
            return "employeeItemList";
        } catch (ItemException ie) {
            ContextUtils.emitInternationalizedMessage(null, ie.getMessage());
            init();
            itemListSession.init();
            return "";
        }
    }

    public String setItemDeleted(ItemDTO itemDTO) {
        try {
            itemEndpoint.setItemDeleted(itemDTO);
            init();
            itemListSession.init();
            return "employeeItemList";
        } catch (ItemException ie) {
            ContextUtils.emitInternationalizedMessage(null, ie.getMessage());
            init();
            itemListSession.init();
            return "";
        }
    }
    
    public String createItem(ItemDescriptionDTO itemDescriptionDTO){
        this.itemDescriptionDTO = itemDescriptionDTO;
        return "createItem";
    }
}
