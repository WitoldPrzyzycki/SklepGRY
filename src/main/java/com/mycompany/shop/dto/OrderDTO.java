package com.mycompany.shop.dto;

import com.mycompany.shop.entitis.OrderStatus;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class OrderDTO implements Comparable<OrderDTO> {

    private final long id;
    private OrderStatus status;
    private AccountDTO accountDto;
    private List<ItemDTO> itemDTOs;
    private Date placedAt;

    public OrderDTO(long id, OrderStatus status, AccountDTO accountDto, Date placedAt, List<ItemDTO> itemDTOs) {
        this.id = id;
        this.status = status;
        this.accountDto = accountDto;
        this.itemDTOs = itemDTOs;
        this.placedAt = placedAt;
    }

    public long getId() {
        return id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public AccountDTO getAccountDto() {
        return accountDto;
    }

    public void setAccountDto(AccountDTO accountDto) {
        this.accountDto = accountDto;
    }

    public List<ItemDTO> getItemDTOs() {
        return itemDTOs;
    }

    public void setItemDTOs(List<ItemDTO> itemDTOs) {
        this.itemDTOs = itemDTOs;
    }

    public Date getPlacedAt() {
        return placedAt;
    }

    public String getPlacedAtString() {
        return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(placedAt);
    }

    public void setPlacedAt(Date placedAt) {
        this.placedAt = placedAt;
    }

    @Override
    public int compareTo(OrderDTO o) {
        return ((this.placedAt.after(o.placedAt)) ? -1 : ((this.placedAt.before(o.placedAt)) ? 1 : 0));
    }

}
