package com.mycompany.shop.exceptions;

public class AccountException extends AppBaseException {

    static final public String KEY_DB_CONSTRAINT = "error.account.db.constraint";
    static final public String KEY_OPTIMISTIC_LOCK = "error.optimistic.lock";
    static final public String KEY_PLACED_ORDER_WARNING = "placed.order.warning";

    public AccountException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountException(String message) {
        super(message);
    }

    static public AccountException createAccountExeptionWithDbCheckConstraintKey(Throwable cause) {
        AccountException ae = new AccountException(KEY_DB_CONSTRAINT, cause);
        return ae;
    }

    static public AccountException createAccountExeptionWithOptimisticLock(Throwable cause) {
        AccountException ae = new AccountException(KEY_OPTIMISTIC_LOCK, cause);
        return ae;
    }
    static public AccountException createAccountExeptionWithPlacedOrderWarning() {
        AccountException ae = new AccountException(KEY_PLACED_ORDER_WARNING);
        return ae;
    }

}
