package com.ryd.apt_api;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.View;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.annotation.VisibleForTesting;

import com.ryd.apt_runtime.Unbinder;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author : ruanyandong
 * @e-mail : ruanyandong@didiglobal.com
 * @date : 11/24/21 5:18 PM
 * @desc : com.ryd.apt_api
 */
public class ButterKnife {

    private ButterKnife(){
        throw new IllegalAccessError("No instance");
    }

//    public static Unbinder bind(Activity activity) {
//        try {
//            Class<?> clazz = Class.forName(activity.getClass().getName() + "_ViewBinding");
//            Constructor<?> cons = clazz.getConstructor(activity.getClass());
//            Unbinder unbinder = (Unbinder) cons.newInstance(activity);
//            return unbinder;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return Unbinder.EMPTY;
//    }


    private static final String TAG = "ButterKnife";
    private static boolean debug = false;

    @VisibleForTesting
    static final Map<Class<?>, Constructor<? extends Unbinder>> BINDINGS = new LinkedHashMap<>();

    /** Control whether debug logging is enabled. */
    public static void setDebug(boolean debug) {
        ButterKnife.debug = debug;
    }


    @NonNull
    @UiThread
    public static Unbinder bind(@NonNull Activity target) {
        View sourceView = target.getWindow().getDecorView();
        return bind(target, sourceView);
    }

    @NonNull @UiThread
    public static Unbinder bind(@NonNull View target) {
        return bind(target, target);
    }

    @NonNull @UiThread
    public static Unbinder bind(@NonNull Dialog target) {
        View sourceView = target.getWindow().getDecorView();
        return bind(target, sourceView);
    }

    @NonNull @UiThread
    public static Unbinder bind(@NonNull Object target, @NonNull Activity source) {
        View sourceView = source.getWindow().getDecorView();
        return bind(target, sourceView);
    }

    @NonNull @UiThread
    public static Unbinder bind(@NonNull Object target, @NonNull Dialog source) {
        View sourceView = source.getWindow().getDecorView();
        return bind(target, sourceView);
    }

    @NonNull @UiThread
    public static Unbinder bind(@NonNull Object target, @NonNull View source) {
        Class<?> targetClass = target.getClass();
        if (debug) Log.d(TAG, "Looking up binding for " + targetClass.getName());
        Constructor<? extends Unbinder> constructor = findBindingConstructorForClass(targetClass);

        if (constructor == null) {
            return Unbinder.EMPTY;
        }

        //noinspection TryWithIdenticalCatches Resolves to API 19+ only type.
        try {
            return constructor.newInstance(target,source);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new RuntimeException("Unable to create binding instance.", cause);
        }
    }

    @Nullable
    @CheckResult
    @UiThread
    private static Constructor<? extends Unbinder> findBindingConstructorForClass(Class<?> cls) {
        Constructor<? extends Unbinder> bindingCtor = BINDINGS.get(cls);
        if (bindingCtor != null || BINDINGS.containsKey(cls)) {
            if (debug) Log.d(TAG, "HIT: Cached in binding map.");
            return bindingCtor;
        }
        String clsName = cls.getName();
        if (clsName.startsWith("android.") || clsName.startsWith("java.")
                || clsName.startsWith("androidx.")) {
            if (debug) Log.d(TAG, "MISS: Reached framework class. Abandoning search.");
            return null;
        }
        try {
            Class<?> bindingClass = cls.getClassLoader().loadClass(clsName + "_ViewBinding");
            //noinspection unchecked
            bindingCtor = (Constructor<? extends Unbinder>) bindingClass.getConstructor(cls,View.class);
            if (debug) Log.d(TAG, "HIT: Loaded binding class and constructor.");
        } catch (ClassNotFoundException e) {
            if (debug) Log.d(TAG, "Not found. Trying superclass " + cls.getSuperclass().getName());
            bindingCtor = findBindingConstructorForClass(cls.getSuperclass());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find binding constructor for " + clsName, e);
        }
        BINDINGS.put(cls, bindingCtor);
        return bindingCtor;
    }

}
