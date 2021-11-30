package com.ryd.apt_annotation;

import androidx.annotation.StringRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * Bind a field to the specified string resource ID.
 *
 *  @BindString(stringId = R.string.username_error)
 *  String usernameErrorText;
 *
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD})
public @interface BindString {
    /** String resource ID to which the field will be bound. */
    @StringRes int stringId() default -1;
}
