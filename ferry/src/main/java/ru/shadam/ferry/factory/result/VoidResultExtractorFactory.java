package ru.shadam.ferry.factory.result;

import ru.shadam.ferry.analyze.MethodContext;
import ru.shadam.ferry.factory.response.ResponseWrapper;

import java.io.IOException;

/**
 * @author timur.shakurov@dz.ru
 */
public class VoidResultExtractorFactory implements ResultExtractorFactory {

    @Override
    public boolean canCreateExtractor(MethodContext methodContext) {
        return Void.TYPE.equals(methodContext.returnType());
    }

    @Override
    public <T> ResultExtractor<T> getResultExtractor(MethodContext methodContext) throws UnsupportedTypeException {
        if(!canCreateExtractor(methodContext)) {
            throw new UnsupportedTypeException(methodContext.returnType());
        }
        return new ResultExtractor<T>() {
            @Override
            public T extractResponse(ResponseWrapper responseWrapper) throws IOException {
                return null;
            }
        };
    }
}
