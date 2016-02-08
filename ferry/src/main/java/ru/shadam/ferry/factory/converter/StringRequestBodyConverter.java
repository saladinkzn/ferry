package ru.shadam.ferry.factory.converter;

/**
 * @author sala
 */
public class StringRequestBodyConverter implements RequestBodyConverter {
    @Override
    public <T> String convert(T value) {
        return ((String) value);
    }

    @Override
    public <T> boolean canConvert(Class<T> clazz) {
        return clazz.isAssignableFrom(String.class);
    }
}
