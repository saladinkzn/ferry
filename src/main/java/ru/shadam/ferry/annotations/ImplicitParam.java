package ru.shadam.ferry.annotations;

import java.lang.annotation.*;

/**
 * Annotation to provide implicit (value not supported in method call) request parameters.
 *
 * <p> Implicit parameters can be "constant" and "provided":
 *
 * <p> Value for <em>constant</em> implicit parameter provided through <code>constValue</code> property
 *
 * <p> Value for <em>provided</em> implicit parameter provided by ImplicitParameterProvided. Name of this provider
 * is specified with <code>providerName</code> property.
 *
 * <p><b>Example:</b>
 * <pre><code>
 *     &#64;ImplicitParam(paramName = "v", constValue = "5.41")
 *     void callVKApi();
 * </code></pre>
 *
 * @author sala
 * @see ru.shadam.ferry.factory.ClientImplementationFactory
 * @see ru.shadam.ferry.implicit.ImplicitParameterProvider
 * @see ru.shadam.ferry.implicit.ImplicitParameterWithNameProvider
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ImplicitParam {
    String paramName();

    String constValue() default "";

    String providerName() default "";
}
