package com.jpyy001.tools.sample.loader;

import android.content.Context;

import com.jpyy001.tools.core.loader.ShadowPluginLoader;
import com.jpyy001.tools.core.loader.managers.ComponentManager;
import com.jpyy001.tools.sample.loader.SampleComponentManager;

/**
 * 这里的类名和包名需要固定
 * com.jpyy001.tools.sdk.pluginloader.PluginLoaderImpl
 */
public class SamplePluginLoader extends ShadowPluginLoader {

    private final static String TAG = "shadow";

    private ComponentManager componentManager;

    public SamplePluginLoader(Context hostAppContext) {
        super(hostAppContext);
        componentManager = new SampleComponentManager(hostAppContext);
    }

    @Override
    public ComponentManager getComponentManager() {
        return componentManager;
    }

}
