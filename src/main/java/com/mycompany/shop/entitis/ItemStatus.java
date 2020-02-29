
package com.mycompany.shop.entitis;

import com.mycompany.shop.utils.ContextUtils;


public enum ItemStatus {
    ACTIVE("item.status.active"),
    INACTIVE("item.status.inactive"),
    RESERVED("item.status.reserved"),
    SOLD("item.status.sold"),
    DELETED("item.status.deleted");
    
    private String value;

    private ItemStatus(String value) {
        this.value = value;
    }
    
    
    @Override
    public String toString() {
        return ContextUtils.getDefaultBundle().getString(value);
    }
    
}
