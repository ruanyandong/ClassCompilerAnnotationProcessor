package com.ryd.apt_annotation;

import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Bind a field to the specified color resource ID. Type can be {@code int} or
 *  android.content.res.ColorStateList.
 * <pre><code>
 * {@literal @}BindColor(R.color.background_green) int green;
 * {@literal @}BindColor(R.color.background_green_selector) ColorStateList greenSelector;
 * </code></pre>
 */
@Retention(value = RetentionPolicy.CLASS)
@Target(value = {ElementType.FIELD})
public @interface BindColor {
    /** Color resource ID to which the field will be bound. */
    @ColorRes int colorResId() default -1;
}
