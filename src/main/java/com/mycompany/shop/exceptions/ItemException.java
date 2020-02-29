package com.mycompany.shop.exceptions;

public class ItemException extends AppBaseException {

    static final public String KEY_OPTIMISTIC_LOCK = "error.optimistic.lock";
    static final public String KEY_GAME_UNAVAILABLE = "error.chosen.game.unavailable";
    static final public String KEY_DB_CONSTRAINT = "error.item.exist";
    static final public String KEY_NULL_ITEM = "error.null.object.not.found";

    public ItemException(String message, Throwable cause) {
        super(message, cause);
    }

    public ItemException(String message) {
        super(message);
    }

    static public ItemException createItemExceptionWithOptimisticLock(Throwable cause) {
        ItemException ie = new ItemException(KEY_OPTIMISTIC_LOCK, cause);
        return ie;
    }

    static public ItemException createItemExceptionWithGameUnavailable() {
        ItemException ie = new ItemException(KEY_GAME_UNAVAILABLE);
        return ie;
    }

    static public ItemException createItemExceptionWithDBConstaint(Throwable cause) {
        ItemException ie = new ItemException(KEY_DB_CONSTRAINT, cause);
        return ie;
    }
    static public ItemException createItemExceptionWithNullItem() {
        ItemException ie = new ItemException(KEY_NULL_ITEM);
        return ie;
    }
}
