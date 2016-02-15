package ru.shadam.ferry.factory.result;

import java.lang.reflect.Type;

/**
 * Thrown if method's return type is not supported by ResultExtractorFactory
 *
 * @author timur.shakurov@dz.ru
 */
public class UnsupportedTypeException extends RuntimeException {
    public UnsupportedTypeException(Type type) {
        super("Type [" + type + "] is not supported");
    }
}
