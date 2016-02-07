package ru.shadam.ferry.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to provide url to implementation generator.
 *
 * <p><b>Example:</b>
 * <pre><code>
 * &#64;Url("http://example.com/entities/")
 * public List&lt;Entity&gt; getAll();
 * </code></pre>
 *
 * @author sala
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Url {
    String value();
}
