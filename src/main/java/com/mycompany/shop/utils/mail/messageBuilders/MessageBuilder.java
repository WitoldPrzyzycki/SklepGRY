/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.shop.utils.mail.messageBuilders;

/**
 *
 * @author java
 */
public abstract class MessageBuilder {
    public String to;
    public String subject;
    public String body;

    public String getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }
    
}
