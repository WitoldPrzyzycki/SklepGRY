package com.mycompany.shop.web;

import com.mycompany.shop.dto.ItemDTO;
import com.mycompany.shop.dto.ItemDescriptionDTO;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.model.DataModel;
import javax.inject.Inject;
import javax.inject.Named;

@RequestScoped
@Named("basketBean")
public class BasketBean implements Serializable {

    @Inject
    private BasketSession basketSession;
    @Inject
    private OrdersListSession ordersListSession;
    @Inject
    private ItemListSession itemListSession;

    private int basketValue;
    private DataModel<ItemDTO> dataModelItemDTOs;
    private DataModel<ItemDescriptionDTO> dataModelItemDescriptionDTOs;

    public DataModel<ItemDTO> getDataModelItemDTOs() {
        return dataModelItemDTOs;
    }

    @PostConstruct
    public void init() {
        dataModelItemDTOs = basketSession.getDataModelItemDTOs();
        basketValue = basketSession.getBasketValue();
    }

    public BasketBean() {
    }

    public int getBasketValue() {
        return basketValue;
    }

    public DataModel<ItemDescriptionDTO> getDataModelItemDescriptionDTOs() {
        return dataModelItemDescriptionDTOs;
    }

    public void setDataModelItemDescriptionDTOs(DataModel<ItemDescriptionDTO> dataModelitemDescriptionDTOs) {
        this.dataModelItemDescriptionDTOs = dataModelitemDescriptionDTOs;
    }

    public String removeFromOrder(ItemDTO itemDTO) {
        basketSession.removeFromOrder(itemDTO);
        itemListSession.init();
        return "basket";
    }

    public String placeOrder() {
        String outcome = basketSession.placeORder();
        ordersListSession.init();
        return outcome;
    }
}
