package ru.shadam.ferry.simple.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shadam.ferry.factory.converter.RequestBodyConverter;

import ru.shadam.ferry.util.MoreObjects;

/**
 * @author sala
 */
public class ObjectMapperRequestBodyConverter implements RequestBodyConverter {
    private static final Logger logger = LoggerFactory.getLogger(ObjectMapperRequestBodyConverter.class);

    private final ObjectMapper objectMapper;

    public ObjectMapperRequestBodyConverter(ObjectMapper objectMapper) {
        MoreObjects.requireNonNull(objectMapper);
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> String convert(T value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException jsonEx) {
            logger.error("process json exception", jsonEx);
            return null;
        }
    }

    @Override
    public <T> boolean canConvert(Class<T> clazz) {
        // Check if supported by jackson
        return true;
    }
}
