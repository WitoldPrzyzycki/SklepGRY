package com.mycompany.shop.managers;

import com.mycompany.shop.ejb.facades.ItemFacade;
import com.mycompany.shop.ejb.facades.OrderFacade;
import com.mycompany.shop.ejb.interceptor.LoggingInterceptor;
import com.mycompany.shop.entitis.Item;
import com.mycompany.shop.entitis.ItemStatus;
import com.mycompany.shop.entitis.Orders;
import com.mycompany.shop.utils.mail.MailServiceBean;
import com.mycompany.shop.utils.mail.messageBuilders.MessageBuilder;
import com.mycompany.shop.utils.mail.messageBuilders.OrderMessageBuilder;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.annotation.security.RunAs;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

@Singleton
@Startup
@Interceptors(LoggingInterceptor.class)
@RunAs("Employee")
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class OrdersManager {

    @Inject
    private OrderFacade orderFacade;
    @Inject
    private ItemFacade itemFacade;
    @Inject
    private MailServiceBean mailService;

    @Schedule(hour = "*/1")
    public void openOrderTimeout() {
        List<Orders> ordersList = orderFacade.findOpenOrders();
        for (Orders order : ordersList) {
            List<Item> items = order.getItemList();
            for (Item item : items) {
                if ((item.getReservedAt().toInstant().plus(1, ChronoUnit.DAYS)).isBefore(Instant.now())) {
                    System.out.println(item.getReservedAt().toInstant().plus(1, ChronoUnit.DAYS));
                    System.out.println(item.getReservedAt().toInstant().plus(1, ChronoUnit.DAYS).isBefore(Instant.now()));
                    item.setReservedAt(null);
                    item.setOrder(null);
                    item.setStatus(ItemStatus.ACTIVE);
                    itemFacade.edit(item);
                }
            }
            items.clear();
            orderFacade.edit(order);
        }
    }

    @Schedule(hour = "*/1")
    public void placedOrderTimeout() {
        List<Orders> ordersList = orderFacade.findPlacedOrders();
        for (Orders order : ordersList) {
            if ((order.getPlacedAt().toInstant().plus(7, ChronoUnit.DAYS)).isBefore(Instant.now())) {
                MessageBuilder messageBuilder = OrderMessageBuilder.createOrderCancelDueToTimeoutConfirmationMessage(order);
                List<Item> itemList = order.getItemList();
                for (Item item : itemList) {
                    item.setOrder(null);
                    item.setReservedAt(null);
                    item.setStatus(ItemStatus.ACTIVE);
                    itemFacade.edit(item);
                }
                orderFacade.remove(order);
                mailService.sendMessage(messageBuilder);
            }
        }
    }
}
