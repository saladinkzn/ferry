package ru.shadam.ferry.factory.result;

import ru.shadam.ferry.analyze.MethodContext;

/**
 * Factory to create ResultExtractor by method context
 *
 * @author sala
 */
public interface ResultExtractorFactory {
    boolean canCreateExtractor(MethodContext methodContext);

    <T> ResultExtractor<T> getResultExtractor(MethodContext methodContext) throws UnsupportedTypeException;
}
