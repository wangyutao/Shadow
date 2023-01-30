package com.jpyy001.tools.dynamic.impl;

import android.content.Context;

import com.jpyy001.tools.dynamic.host.ManagerFactory;
import com.jpyy001.tools.dynamic.host.PluginManagerImpl;
import com.jpyy001.tools.sample.manager.SamplePluginManager;

/**
 * 此类包名及类名固定
 */
public final class ManagerFactoryImpl implements ManagerFactory {
    @Override
    public PluginManagerImpl buildManager(Context context) {
        return new SamplePluginManager(context);
    }
}
