package com.mycompany.shop.web;

import com.mycompany.shop.dto.GenreDTO;
import com.mycompany.shop.dto.ItemDTO;
import com.mycompany.shop.dto.OrderDTO;
import com.mycompany.shop.ejb.endpoints.OrderEndpoint;
import com.mycompany.shop.entitis.OrderStatus;
import com.mycompany.shop.entitis.Pegi;
import com.mycompany.shop.exceptions.AppBaseException;
import com.mycompany.shop.utils.ContextUtils;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import javax.inject.Named;

@SessionScoped
@Named("OrdersListSession")
public class OrdersListSession implements Serializable {

    @Inject
    private EmployeeItemListSession employeeItemListSession;
    @Inject
    private OrderEndpoint orderEndpoint;
    @Inject
    private ItemListSession itemListSession;

    private OrderDTO inRealizationOrder;
    private List<OrderDTO> orderDTOs;
    private DataModel<OrderDTO> dataModelOrderDTOs;

    public DataModel<OrderDTO> getDataModelOrderDTOs() {
        return dataModelOrderDTOs;
    }

    @PostConstruct
    public void init() {
        orderDTOs = orderEndpoint.listInit();
        Collections.sort(orderDTOs);
        dataModelOrderDTOs = new ListDataModel<>(orderDTOs);
    }

    public OrderDTO getInRealizationOrder() {
        return inRealizationOrder;
    }

    public void setInRealizationOrder(OrderDTO inRealizationOrder) {
        this.inRealizationOrder = inRealizationOrder;
    }

    public Integer getSelectedOrderValue(OrderDTO orderDTO) {
        List<ItemDTO> itemDTOs = orderDTO.getItemDTOs();
        Integer selectedOrderValue = 0;
        for (ItemDTO itemDTO : itemDTOs) {
            selectedOrderValue += itemDTO.getItemDescriptionDto().getPrice();
        }
        return selectedOrderValue;
    }

    public Boolean isOrderClosed(OrderDTO orderDTO) {
        if (OrderStatus.CLOSED.equals(orderDTO.getStatus())) {
            return true;
        }
        return false;
    }

    public String cancelOrder(OrderDTO orderDTO) {
        try {
            orderEndpoint.cancelOrder(orderDTO);
            init();
            itemListSession.init();
            employeeItemListSession.init();
            return "orders";
        } catch (AppBaseException abe) {
            ContextUtils.emitInternationalizedMessage(null, abe.getMessage());
            init();
            itemListSession.init();
            employeeItemListSession.init();
            return "";
        }
    }

    public String realizeOrder(OrderDTO orderDTO) {
        inRealizationOrder = orderDTO;
        return "orderRealization";
    }

    public String confirmOrderRealization() {
        try {
            orderEndpoint.realizeOrder(inRealizationOrder);
            inRealizationOrder = null;
            init();
            return "orders";
        } catch (AppBaseException abe) {
            ContextUtils.emitInternationalizedMessage(null, abe.getMessage());
            init();
            return "realizationError";
        }
    }

    public void findByConditions(String login, Date fromDate, Date toDate, List<OrderStatus> status, String title, List<GenreDTO> genreDtoSearchList, Pegi pegi, Integer minPrice, Integer maxPrice) {
        orderDTOs = orderEndpoint.findByConditions(login, fromDate, toDate, status, title, genreDtoSearchList, pegi, minPrice, maxPrice);
        Collections.sort(orderDTOs);
        dataModelOrderDTOs = new ListDataModel<>(orderDTOs);
    }

}
