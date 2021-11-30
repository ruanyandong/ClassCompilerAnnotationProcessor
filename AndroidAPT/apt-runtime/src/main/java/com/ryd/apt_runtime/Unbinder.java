package com.ryd.apt_runtime;

import androidx.annotation.UiThread;

/**
 * @author : ruanyandong
 * @e-mail : ruanyandong@didiglobal.com
 * @date : 11/24/21 3:29 PM
 * @desc : com.ryd.apt_runtime
 */
public interface Unbinder {
    @UiThread
    void unbind();

    Unbinder EMPTY = () -> { };
}
