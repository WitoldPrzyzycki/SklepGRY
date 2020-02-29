
package com.mycompany.shop.entitis;

import com.mycompany.shop.utils.ContextUtils;


public enum AccountStatus {
    ACTIVE("account.status.active"),
    INACTIVE("account.status.inactive"),
    DELETED("account.status.deleted");

    private String value;

    private AccountStatus(String value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return ContextUtils.getDefaultBundle().getString(value);
    }
}
