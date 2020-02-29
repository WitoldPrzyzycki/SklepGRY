package com.mycompany.shop.dto;

import com.mycompany.shop.entitis.ItemStatus;
import java.util.Date;

public class ItemDTO {

    private final long id;
    private String keyValue;
    private ItemDescriptionDTO itemDescriptionDto;
    private ItemStatus status;
    private OrderDTO orders;
    private Date reservedAt;

    public ItemDTO(long id, ItemDescriptionDTO itemDescriptionDto, ItemStatus status, String keyValue, Date reservedAt) {
        this.id = id;
        this.itemDescriptionDto = itemDescriptionDto;
        this.status = status;
        this.keyValue = keyValue;
        this.reservedAt = reservedAt;
    }

    public long getId() {
        return id;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public ItemDescriptionDTO getItemDescriptionDto() {
        return itemDescriptionDto;
    }

    public void setItemDescriptionDtoId(ItemDescriptionDTO itemDescriptionDto) {
        this.itemDescriptionDto = itemDescriptionDto;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public OrderDTO getOrders() {
        return orders;
    }

    public void setOrders(OrderDTO orders) {
        this.orders = orders;
    }

    public Date getReservedAt() {
        return reservedAt;
    }

    public void setReservedAt(Date reservedAt) {
        this.reservedAt = reservedAt;
    }

}
