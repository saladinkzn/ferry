package ru.shadam.ferry.core;

import ru.shadam.ferry.analyze.MethodContext;
import ru.shadam.ferry.factory.response.ResponseWrapper;
import ru.shadam.ferry.factory.result.ResultExtractor;
import ru.shadam.ferry.factory.result.ResultExtractorFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author timur.shakurov@dz.ru
 */
public class CoreResultExtractorFactory implements ResultExtractorFactory {

    private static final StringExtractor<?> STRING_EXTRACTOR = new StringExtractor<>();

    @Override
    public <T> ResultExtractor<T> getResultExtractor(MethodContext methodContext) {
        return (ResultExtractor<T>) STRING_EXTRACTOR;
    }

    private static class StringExtractor<T> implements ResultExtractor<T> {
        @Override
        public T extractResponse(ResponseWrapper responseWrapper) throws IOException {
            final StringBuilder responseBuilder = new StringBuilder();
            try(final InputStreamReader inputStreamReader = new InputStreamReader(responseWrapper.getInputStream())) {
                try(final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        responseBuilder.append(line);
                    }
                    return (T)responseBuilder.toString();
                }
            }
        }
    }
}
