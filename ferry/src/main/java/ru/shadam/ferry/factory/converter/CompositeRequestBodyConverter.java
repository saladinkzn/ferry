package ru.shadam.ferry.factory.converter;

import ru.shadam.ferry.util.MoreObjects;

import java.util.List;

/**
 * @author sala
 */
public class CompositeRequestBodyConverter implements RequestBodyConverter {
    private final List<RequestBodyConverter> converters;

    public CompositeRequestBodyConverter(List<RequestBodyConverter> converters) {
        MoreObjects.requireNonNull(converters);
        this.converters = converters;
    }

    @Override
    public <T> String convert(T value) {
        for(RequestBodyConverter converter: converters) {
            if(converter.canConvert(value.getClass())) {
                return converter.convert(value);
            }
        }
        throw new RuntimeException("Cannot convert value: " + value);
    }

    @Override
    public <T> boolean canConvert(Class<T> clazz) {
        for(RequestBodyConverter converter: converters) {
            if(converter.canConvert(clazz)) {
                return true;
            }
        }
        return false;
    }
}
