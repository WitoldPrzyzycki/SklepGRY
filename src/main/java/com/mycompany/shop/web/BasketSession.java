package com.mycompany.shop.web;

import com.mycompany.shop.dto.ItemDTO;
import com.mycompany.shop.dto.ItemDescriptionDTO;
import com.mycompany.shop.dto.OrderDTO;
import com.mycompany.shop.ejb.endpoints.ItemEndpoint;
import com.mycompany.shop.ejb.endpoints.OrderEndpoint;
import com.mycompany.shop.exceptions.ItemException;
import com.mycompany.shop.exceptions.OrderException;
import com.mycompany.shop.utils.ContextUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import javax.inject.Named;

@SessionScoped
@Named("BasketSession")
public class BasketSession implements Serializable {

    @Inject
    private OrderEndpoint orderEndpoint;
    

    @Inject
    private ItemEndpoint itemEndpoint;

    private OrderDTO orderDTO;
    private List<ItemDescriptionDTO> itemDescriptionDTOs;
    private List<ItemDTO> itemDTOs;
    private DataModel<ItemDTO> dataModelItemDTOs;
    private int basketValue;

    public OrderDTO getOrderDTO() {
        return orderDTO;
    }

    public int getBasketValue() {
        return basketValue;
    }

    public DataModel<ItemDTO> getDataModelItemDTOs() {
        return dataModelItemDTOs;
    }

    @PostConstruct
    public void init() {
        basketValue = 0;
        orderDTO = orderEndpoint.findOpenOrderForUser();
        setItemDTOsFromOrderDTO();
        itemEndpoint.itemsListInit(orderDTO);
        dataModelItemDTOs = new ListDataModel<>(itemDTOs);
    }

    private void setItemDTOsFromOrderDTO() {
        itemDTOs = orderDTO.getItemDTOs();
        itemDescriptionDTOs = new ArrayList<>();
        for (ItemDTO itemDTO : itemDTOs) {
            ItemDescriptionDTO itemDescriptionDTO = itemDTO.getItemDescriptionDto();
            itemDescriptionDTOs.add(itemDescriptionDTO);
            basketValue += itemDescriptionDTO.getPrice();
        }
    }

    public void removeFromOrder(ItemDTO itemDTO) {
        try {
            itemEndpoint.removeFromOrder(itemDTO, orderDTO);
            basketValue -= itemDTO.getItemDescriptionDto().getPrice();
            init();
        } catch (ItemException ie) {
            ContextUtils.emitInternationalizedMessage(null, ie.getMessage());
        }
    }

    public String placeORder() {
        if (orderDTO.getItemDTOs().isEmpty()) {
            return "basket";
        }
        try {
            orderEndpoint.placeOrder(orderDTO);
            basketValue = 0;
            
            init();
            return "basket";
        } catch (OrderException oe) {
            ContextUtils.emitInternationalizedMessage(null, oe.getMessage());
            init();
            return "";
        }
    }

    public Integer basketSize() {
        return orderDTO.getItemDTOs().size();
    }
}
