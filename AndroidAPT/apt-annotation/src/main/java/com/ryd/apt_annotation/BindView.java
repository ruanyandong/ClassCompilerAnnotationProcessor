package com.ryd.apt_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Bind a field to the view for the specified ID. The view will automatically be cast to the field
 * type.
 *
 *  @BindView(value = R.id.title)
 *  TextView title;
 *
 */
@Retention(value = RetentionPolicy.CLASS)
@Target(value = {ElementType.FIELD})
public @interface BindView {
    /** View ID to which the field will be bound. */
    int value() default -1;
}
