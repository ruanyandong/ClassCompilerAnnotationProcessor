package com.ryd.apt_annotation;

import android.graphics.Bitmap;
import androidx.annotation.DrawableRes;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Bind a field to a {@link Bitmap} from the specified drawable resource ID.
 * <pre><code>
 * {@literal @}BindBitmap(R.drawable.logo) Bitmap logo;
 * </code></pre>
 */
@Retention(value = RetentionPolicy.CLASS)
@Target(value = {ElementType.FIELD})
public @interface BindBitmap {
    /** Drawable resource ID from which the {@link Bitmap} will be created. */
    @DrawableRes int bitmapResId() default -1;
}
