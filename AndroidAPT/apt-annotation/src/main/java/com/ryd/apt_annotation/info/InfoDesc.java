package com.ryd.apt_annotation.info;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author : ruanyandong
 * @e-mail : ruanyandong@didiglobal.com
 * @date : 11/19/21 11:26 AM
 * @desc : com.ryd.apt_annotation
 */
@Inherited
@Documented
@Retention(value = RetentionPolicy.CLASS)
@Target(value = {ElementType.TYPE,ElementType.METHOD})
public @interface InfoDesc {
    String desc() default "";
}
