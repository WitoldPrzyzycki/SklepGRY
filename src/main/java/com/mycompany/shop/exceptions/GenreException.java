package com.mycompany.shop.exceptions;

public class GenreException extends AppBaseException {

    static final public String KEY_DB_CONSTRAINT = "error.genre.db.constraint";

    public GenreException(String message, Throwable cause) {
        super(message, cause);
    }

    public GenreException(String message) {
        super(message);
    }

    static public GenreException createGenreExceptionWithDbCheckConstraintKey(Throwable cause) {
        GenreException ge = new GenreException(KEY_DB_CONSTRAINT, cause);
        return ge;
    }
}
