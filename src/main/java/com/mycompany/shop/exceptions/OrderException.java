package com.mycompany.shop.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = false)
public class OrderException extends AppBaseException {

    static final public String KEY_OPTIMISTIC_LOCK = "error.optimistic.lock";
    static final public String KEY_NO_SUCH_ORDER = "error.no.such.order";

    public OrderException(String message) {
        super(message);
    }

    public OrderException(String message, Throwable cause) {
        super(message, cause);
    }

    static public OrderException createOrderExeptionWithOptimisticLock(Throwable cause) {
        OrderException oe = new OrderException(KEY_OPTIMISTIC_LOCK, cause);
        return oe;
    }

    static public OrderException createOrderExeptionWithNoSuchOrder(Throwable cause) {
        OrderException oe = new OrderException(KEY_NO_SUCH_ORDER, cause);
        return oe;
    }
}
