package com.ryd.apt_annotation;

import androidx.annotation.AnimRes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Bind a field to the specified animation resource ID.
 *
 * @BindAnim(animResId = R.anim.fade_in)
 * Animation fadeIn;
 *
 */
@Retention(value = RetentionPolicy.CLASS)
@Target(value = {ElementType.FIELD})
public @interface BindAnim {
    /** Animation resource ID to which the field will be bound. */
    @AnimRes int animResId() default -1;
}
