
package com.mycompany.shop.entitis;

import com.mycompany.shop.utils.ContextUtils;

public enum OrderStatus {
    OPEN("order.status.open"),
    PLACED("order.status.placed"),
    CLOSED("order.status.closed");
    
    private String value;

    private OrderStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return ContextUtils.getDefaultBundle().getString(value);
    }
    
}
