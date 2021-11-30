package com.ryd.apt_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Bind a method to an  View.OnClickListener on the view for each ID specified.
 *
 *  @OnClick(value = {R.id.example})
 *  void onClick() {
 *   Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show();
 * }
 *
 */
@Retention(value = RetentionPolicy.CLASS)
@Target(value = {ElementType.METHOD})
public @interface OnClick {
    /** View IDs to which the method will be bound. */
    int[] value() default -1;
}
