package com.jpyy001.tools.dynamic.loader.impl;

import android.content.Context;

import com.jpyy001.tools.core.loader.ShadowPluginLoader;
import com.jpyy001.tools.sample.loader.SamplePluginLoader;

import org.jetbrains.annotations.NotNull;

/**
 * 这个类的包名类名是固定的。
 * <p>
 * 见com.jpyy001.tools.dynamic.loader.impl.DynamicPluginLoader#CORE_LOADER_FACTORY_IMPL_NAME
 */
public class CoreLoaderFactoryImpl implements CoreLoaderFactory {

    @NotNull
    @Override
    public ShadowPluginLoader build(@NotNull Context context) {
        return new SamplePluginLoader(context);
    }
}
