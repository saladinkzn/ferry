package ru.shadam.ferry.core;

import ru.shadam.ferry.factory.ClientImplementationFactory;
import ru.shadam.ferry.factory.converter.StringRequestBodyConverter;

/**
 * @author timur.shakurov@dz.ru
 */
public class CoreClientImplementationFactory extends ClientImplementationFactory {

    public CoreClientImplementationFactory() {
        super(new CoreMethodExecutorFactory(), new CoreResultExtractorFactory(), new StringRequestBodyConverter());
    }
}
