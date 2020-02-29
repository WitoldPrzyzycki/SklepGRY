package com.mycompany.shop.web;

import com.mycompany.shop.dto.GenreDTO;
import com.mycompany.shop.dto.ItemDTO;
import com.mycompany.shop.dto.ItemDescriptionDTO;
import com.mycompany.shop.dto.OrderDTO;
import com.mycompany.shop.ejb.endpoints.ItemDescriptionEndpoint;
import com.mycompany.shop.ejb.endpoints.ItemEndpoint;
import com.mycompany.shop.entitis.ItemStatus;
import com.mycompany.shop.entitis.Pegi;
import com.mycompany.shop.exceptions.ItemException;
import com.mycompany.shop.utils.ContextUtils;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import javax.inject.Named;

@SessionScoped
@Named("itemListSession")
public class ItemListSession implements Serializable {

    @Inject
    private ItemDescriptionEndpoint itemDescriptionEndpoint;
    @Inject
    private ItemEndpoint itemEndpoint;
    @Inject
    private BasketSession basketSession;

    private List<ItemDescriptionDTO> itemDescriptionDTOs;
    private DataModel<ItemDescriptionDTO> dataModelItemDescriptionDTOs;

    public DataModel<ItemDescriptionDTO> getDataModelItemDescriptionDTOs() {
        return dataModelItemDescriptionDTOs;
    }

    public void setDataModelItemDescriptionDTOs(DataModel<ItemDescriptionDTO> dataModelItemDescriptionDTOs) {
        this.dataModelItemDescriptionDTOs = dataModelItemDescriptionDTOs;
    }

    @PostConstruct
    public void init() {
        itemDescriptionDTOs = itemDescriptionEndpoint.itemListInit();
        itemEndpoint.itemsListInit(itemDescriptionDTOs);
        dataModelItemDescriptionDTOs = new ListDataModel<>(itemDescriptionDTOs);
    }

    public void findActiveIdsByConditions(String titleSearch, List<GenreDTO> genreDtoSearch, Pegi pegiSearch, Integer minPrice, Integer maxPrice) {
        itemDescriptionDTOs = itemDescriptionEndpoint.findActiveIdsByConditions(titleSearch, genreDtoSearch, pegiSearch, minPrice, maxPrice);
        dataModelItemDescriptionDTOs = new ListDataModel<>(itemDescriptionDTOs);
    }

    public String addToOrder(ItemDescriptionDTO itemDescriptionDTO) {
        OrderDTO orderDTO = basketSession.getOrderDTO();
        try {
            Iterator<ItemDTO> itemDtoItr = itemDescriptionDTO.getItemsDtoCollection().iterator();

            while (itemDtoItr.hasNext()) {
                ItemDTO itemDTO = itemDtoItr.next();
                if (itemDTO.getStatus().equals(ItemStatus.ACTIVE)) {
                    itemEndpoint.addtoOrder(itemDTO, orderDTO);
                    break;
                }
            }
            init();
            basketSession.init();
            return "basket";
        } catch (ItemException ie) {
            ContextUtils.emitInternationalizedMessage(null, ie.getMessage());
            init();
            basketSession.init();
            return "";
        }
    }
}
