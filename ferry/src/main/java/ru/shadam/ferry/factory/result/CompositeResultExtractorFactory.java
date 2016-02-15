package ru.shadam.ferry.factory.result;

import ru.shadam.ferry.analyze.MethodContext;

import java.util.List;
import java.util.Objects;

/**
 * @author timur.shakurov@dz.ru
 */
public class CompositeResultExtractorFactory implements ResultExtractorFactory {
    private List<ResultExtractorFactory> resultExtractors;

    public CompositeResultExtractorFactory(List<ResultExtractorFactory> resultExtractors) {
        Objects.requireNonNull(resultExtractors);
        this.resultExtractors = resultExtractors;
    }


    @Override
    public boolean canCreateExtractor(MethodContext methodContext) {
        for(ResultExtractorFactory resultExtractorFactory : resultExtractors) {
            if(resultExtractorFactory.canCreateExtractor(methodContext)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public <T> ResultExtractor<T> getResultExtractor(MethodContext methodContext) throws UnsupportedTypeException {
        for(ResultExtractorFactory resultExtractorFactory : resultExtractors) {
            if(resultExtractorFactory.canCreateExtractor(methodContext)) {
                return resultExtractorFactory.getResultExtractor(methodContext);
            }
        }
        throw new UnsupportedTypeException(methodContext.returnType());
    }
}
