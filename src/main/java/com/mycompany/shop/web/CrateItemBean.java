package com.mycompany.shop.web;

import com.mycompany.shop.dto.ItemDescriptionDTO;
import com.mycompany.shop.ejb.endpoints.ItemEndpoint;
import com.mycompany.shop.entitis.ItemStatus;
import com.mycompany.shop.exceptions.ItemException;
import com.mycompany.shop.utils.ContextUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

@ViewScoped
@Named("createItemBean")
public class CrateItemBean implements Serializable {

    @Inject
    private EmployeeItemListSession employeeItemListSession;
    @Inject
    private ItemListSession itemListSession;
    @Inject
    private ItemEndpoint itemEndpoint;

    private String keyValue;
    private ItemStatus itemStatus;
    private ItemDescriptionDTO itemDescriptionDTO;
    private List<ItemStatus> itemStatusList;

    public CrateItemBean() {
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public ItemStatus getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(ItemStatus itemStatus) {
        this.itemStatus = itemStatus;
    }

    public ItemDescriptionDTO getItemDescriptionDTO() {
        return itemDescriptionDTO;
    }

    public void setItemDescriptionDTO(ItemDescriptionDTO itemDescriptionDTO) {
        this.itemDescriptionDTO = itemDescriptionDTO;
    }

    public List<ItemStatus> getItemStatusList() {
        return itemStatusList;
    }

    public void setItemStatusList(List<ItemStatus> itemStatusList) {
        this.itemStatusList = itemStatusList;
    }

    @PostConstruct
    public void init() {
        itemDescriptionDTO = employeeItemListSession.getItemDescriptionDTO();
        List<ItemStatus> isl = new ArrayList<>();
        isl.add(ItemStatus.INACTIVE);
        isl.add(ItemStatus.ACTIVE);
        itemStatusList = isl;
    }

    public String createItem() {
        try {
            itemEndpoint.createItem(keyValue, itemDescriptionDTO, itemStatus);
            this.itemDescriptionDTO = null;
            this.itemStatus = null;
            this.keyValue = null;
            employeeItemListSession.init();
            itemListSession.init();
            return "employeeItemList";
        } catch (ItemException ie) {
            ContextUtils.emitInternationalizedMessage(null, ie.getMessage());
            return "";
        }
    }
}
