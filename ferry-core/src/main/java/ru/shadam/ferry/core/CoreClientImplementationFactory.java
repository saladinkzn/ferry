package ru.shadam.ferry.core;

import ru.shadam.ferry.factory.ClientImplementationFactory;
import ru.shadam.ferry.factory.converter.StringRequestBodyConverter;

/**
 * JDK-only ClientImplementationFactory implementation
 *
 * NOTE: this factory only handles String as method return and request body type.
 *
 * @author timur.shakurov@dz.ru
 *
 * @see ru.shadam.ferry.factory.ClientImplementationFactory
 */
public class CoreClientImplementationFactory extends ClientImplementationFactory {

    public CoreClientImplementationFactory() {
        super(new CoreMethodExecutorFactory(), new CoreResultExtractorFactory(), new StringRequestBodyConverter());
    }
}
