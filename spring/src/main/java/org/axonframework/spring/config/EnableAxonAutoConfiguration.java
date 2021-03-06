package org.axonframework.spring.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @deprecated Use {@link EnableAxon @EnableAxon} instead. This class will be removed in future releases
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@EnableAxon
@Deprecated
public @interface EnableAxonAutoConfiguration {
}
