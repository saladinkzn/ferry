package ru.shadam.ferry.core;

import ru.shadam.ferry.analyze.MethodContext;
import ru.shadam.ferry.factory.response.ResponseWrapper;
import ru.shadam.ferry.factory.result.ResultExtractor;
import ru.shadam.ferry.factory.result.ResultExtractorFactory;
import ru.shadam.ferry.factory.result.UnsupportedTypeException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author timur.shakurov@dz.ru
 */
public class CoreResultExtractorFactory implements ResultExtractorFactory {

    private static final StringExtractor<?> STRING_EXTRACTOR = new StringExtractor<Object>();

    @Override
    public <T> ResultExtractor<T> getResultExtractor(MethodContext methodContext) throws UnsupportedTypeException {
        if(!canCreateExtractor(methodContext)) {
            throw new UnsupportedTypeException(methodContext.returnType());
        }
        return (ResultExtractor<T>) STRING_EXTRACTOR;
    }

    @Override
    public boolean canCreateExtractor(MethodContext methodContext) {
        return String.class.equals(methodContext.returnType());
    }

    private static class StringExtractor<T> implements ResultExtractor<T> {
        @Override
        public T extractResponse(ResponseWrapper responseWrapper) throws IOException {
            final StringBuilder responseBuilder = new StringBuilder();
            final InputStreamReader inputStreamReader = new InputStreamReader(responseWrapper.getInputStream());
            try {
                final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                try {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        responseBuilder.append(line);
                    }
                    return (T)responseBuilder.toString();
                } finally {
                    bufferedReader.close();
                }
            } finally {
                inputStreamReader.close();
            }
        }
    }
}
