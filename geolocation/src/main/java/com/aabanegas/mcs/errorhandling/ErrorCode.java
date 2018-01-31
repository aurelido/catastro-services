package com.aabanegas.mcs.errorhandling;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;

/**
 * Marks an Exception as having a code and an info field associated with them
 * <p/>
 * Used by {@link RestErrorAttributes} to extract the information to set in an error response payload
 */
@SuppressWarnings("unused")
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ErrorCode {

    /**
     * Alias for {@link #code}
     *
     * @return the code
     */
    @AliasFor("code")
    String value() default "";

    /**
     * The unique error code for classification of the error
     * <p/>
     *
     * @return the code
     */
    @AliasFor("value")
    String code() default "";

    /**
     * Information about the error
     *
     * @return the error info
     */
    String info() default "";
}
