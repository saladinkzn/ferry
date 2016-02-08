package ru.shadam.ferry.factory.converter;

/**
 * @author sala
 */
public interface RequestBodyConverter {
    <T> String convert(T value);

    <T> boolean canConvert(Class<T> clazz);
}
