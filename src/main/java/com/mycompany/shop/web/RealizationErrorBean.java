
package com.mycompany.shop.web;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;


@RequestScoped
@Named("realizationErrorBean")
public class RealizationErrorBean {

    @Inject
    private OrdersListSession ordersListSession;

    public String errorEscape() {
        ordersListSession.init();
        return "orders";
    }
}
