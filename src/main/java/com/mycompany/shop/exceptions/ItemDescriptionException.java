package com.mycompany.shop.exceptions;

public class ItemDescriptionException extends AppBaseException {

    static final public String KEY_OPTIMISTIC_LOCK = "error.optimistic.lock";
    static final public String KEY_DELETE_WITH_ACTIVE = "error.delete.itemDescription.with.activeOrReserved.items";
    static final public String KEY_DB_UNIQUE_CONSTRAINT = "error.itemDescription.exist";
    static final public String KEY_NULL_ITEMDSC = "error.null.object.not.found";

    public ItemDescriptionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ItemDescriptionException(String message) {
        super(message);
    }

    static public ItemDescriptionException createItemDescriptionExceptionWithOptimisticLock(Throwable cause) {
        ItemDescriptionException ide = new ItemDescriptionException(KEY_OPTIMISTIC_LOCK, cause);
        return ide;
    }

    static public ItemDescriptionException createItemDescriptionExceptionWithDeleteLock() {
        ItemDescriptionException ide = new ItemDescriptionException(KEY_DELETE_WITH_ACTIVE);
        return ide;
    }

    static public ItemDescriptionException createItemDescriptionExceptionWithDBConstraint(Throwable cause) {
        ItemDescriptionException ide = new ItemDescriptionException(KEY_DB_UNIQUE_CONSTRAINT, cause);
        return ide;
    }

    static public ItemDescriptionException createItemDescriptionExceptionWithNullItemDesc() {
        ItemDescriptionException ide = new ItemDescriptionException(KEY_NULL_ITEMDSC);
        return ide;
    }
}
