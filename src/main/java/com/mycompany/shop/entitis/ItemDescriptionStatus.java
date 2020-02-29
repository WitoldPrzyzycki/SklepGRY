package com.mycompany.shop.entitis;

import com.mycompany.shop.utils.ContextUtils;

public enum ItemDescriptionStatus {
    ACTIVE("itemDescription.status.active"),
    INACTIVE("itemDescription.status.inactive"),
    DELETED("itemDescription.status.deleted");
    
    private String value;

    private ItemDescriptionStatus(String value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return ContextUtils.getDefaultBundle().getString(value);
    }
}
