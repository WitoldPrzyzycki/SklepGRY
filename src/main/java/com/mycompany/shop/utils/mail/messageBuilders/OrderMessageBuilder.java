/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.shop.utils.mail.messageBuilders;

import com.mycompany.shop.entitis.Orders;
import com.mycompany.shop.utils.ContextUtils;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author java
 */
public class OrderMessageBuilder extends MessageBuilder {

    public static OrderMessageBuilder createOrderPlacedConfirmationMessage(Orders order) {
        OrderMessageBuilder omb = new OrderMessageBuilder();
        omb.to = ContextUtils.getContext().getRemoteUser();

        String formatedDate = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM).format(order.getPlacedAt());
        omb.subject = "Potwierdzenie złożenia zamówienia w " + formatedDate;

        StringBuilder sb = new StringBuilder("Zamówienie złożone w ");
        sb.append(formatedDate + " zawiera:");
        sb.append("\n");
        int orderValue = 0;
        for (int i = 0; i < order.getItemList().size(); i++) {
            String priceStr = priceToString(order.getItemList().get(i).getItemDescription().getPrice());
            sb.append("\n");
            sb.append((i + 1) + ":");
            sb.append(order.getItemList().get(i).getItemDescription().getTitle());
            sb.append(" w cenie: " + priceStr + ContextUtils.getDefaultBundle().getString("price.currency"));
            orderValue += order.getItemList().get(i).getItemDescription().getPrice();
        }
        String formatedOrderValue = priceToString(orderValue);
        sb.append("\n");
        sb.append("Wartość zamówienia: " + formatedOrderValue + ContextUtils.getDefaultBundle().getString("price.currency"));
        sb.append("\n");
        omb.body = sb.toString();
        return omb;
    }

    public static OrderMessageBuilder createOrderCancelConfirmationMessage(Orders order) {
        OrderMessageBuilder omb = new OrderMessageBuilder();
        omb.to = order.getAccount().getLogin();
        
        String formatedDate = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM).format(order.getPlacedAt());
        omb.subject = "Anulowanie zamówienia złożonego w " + formatedDate;
        
        StringBuilder sb = new StringBuilder("Zamówienie złożone w ");
        sb.append(formatedDate + " zawierające:");
        sb.append("\n");
        int orderValue = 0;
        for (int i = 0; i < order.getItemList().size(); i++) {
            String priceStr = priceToString(order.getItemList().get(i).getItemDescription().getPrice());
            sb.append("\n");
            sb.append((i + 1) + ":");
            sb.append(order.getItemList().get(i).getItemDescription().getTitle());
            sb.append(" w cenie: " + priceStr + ContextUtils.getDefaultBundle().getString("price.currency"));
            orderValue += order.getItemList().get(i).getItemDescription().getPrice();
        }
        String formatedOrderValue = priceToString(orderValue);
        sb.append("\n");
        sb.append("Wartość zamówienia: " + formatedOrderValue + ContextUtils.getDefaultBundle().getString("price.currency"));
        sb.append("\n");
        sb.append("Zostało anulowane przez " + ContextUtils.getContext().getRemoteUser());
        sb.append("\n");
        sb.append("Jeżeli to nie Ty skontaktuj się z obsługą");
        omb.body = sb.toString();
        return omb;
    }
    
    public static OrderMessageBuilder createOrderCancelDueToTimeoutConfirmationMessage(Orders order) {
        OrderMessageBuilder omb = new OrderMessageBuilder();
        omb.to = order.getAccount().getLogin();
        
        String formatedDate = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.MEDIUM).format(order.getPlacedAt());
        omb.subject = "Anulowanie zamówienia złożonego w " + formatedDate;
        
        StringBuilder sb = new StringBuilder("Zamówienie złożone w ");
        sb.append(formatedDate + " zawierające:");
        sb.append("\n");
        int orderValue = 0;
        for (int i = 0; i < order.getItemList().size(); i++) {
            String priceStr = priceToString(order.getItemList().get(i).getItemDescription().getPrice());
            sb.append("\n");
            sb.append((i + 1) + ":");
            sb.append(order.getItemList().get(i).getItemDescription().getTitle());
            sb.append(" w cenie: " + priceStr + " PLN");
            orderValue += order.getItemList().get(i).getItemDescription().getPrice();
        }
        String formatedOrderValue = priceToString(orderValue);
        sb.append("\n");
        sb.append("Wartość zamówienia: " + formatedOrderValue + " PLN");
        sb.append("\n");
        sb.append("Zostało anulowane ponieważ przekroczono maksymalny czas oczekiwania na zapłatę. Jeżeli dokonałeś wpłaty skontaktuj sie z obsługą.");
        
        omb.body = sb.toString();
        return omb;
    }

    private static String priceToString(int price) {
        BigDecimal value1 = new BigDecimal((Integer) price);
        BigDecimal value2 = value1.divide(new BigDecimal(100));
        NumberFormat f = NumberFormat.getCurrencyInstance(new Locale("pl"));
        if (f instanceof DecimalFormat) {
            ((DecimalFormat) f).format(value2);
        }
        return value2.toPlainString().replace(".", ",");
    }
}
