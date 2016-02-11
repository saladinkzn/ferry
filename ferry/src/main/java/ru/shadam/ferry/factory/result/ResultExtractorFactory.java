package ru.shadam.ferry.factory.result;

import ru.shadam.ferry.analyze.MethodContext;

/**
 * Factory to create ResultExtractor by method context
 *
 * @author sala
 */
public interface ResultExtractorFactory {
    <T> ResultExtractor<T> getResultExtractor(MethodContext methodContext);
}
