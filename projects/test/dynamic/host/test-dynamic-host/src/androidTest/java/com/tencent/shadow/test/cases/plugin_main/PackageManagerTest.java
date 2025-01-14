package com.jpyy001.tools.test.cases.plugin_main;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;

import com.jpyy001.tools.test.lib.test_manager.TestManager;

import org.junit.Test;

public class PackageManagerTest extends PluginMainAppTest {

    @Override
    protected Intent getLaunchIntent() {
        Intent pluginIntent = new Intent();
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        pluginIntent.setClassName(
                packageName,
                "com.jpyy001.tools.test.plugin.general_cases.lib.usecases.packagemanager.TestPackageManagerActivity"
        );
        return pluginIntent;
    }

    @Test
    public void testGetApplicationInfoClassName() {
        matchTextWithViewTag("getApplicationInfo/className", "com.jpyy001.tools.test.plugin.general_cases.lib.gallery.TestApplication");
    }

    @Test
    public void testGetApplicationInfoNativeLibraryDir() {
        matchSubstringWithViewTag("getApplicationInfo/nativeLibraryDir", TestManager.uuid + "_lib");
    }

    @Test
    public void testGetApplicationInfoMetaData() {
        matchTextWithViewTag("getApplicationInfo/metaData", "test_value");
    }

    @Test
    public void testQueryContentProvidersName() {
        matchTextWithViewTag("queryContentProviders/size", ">0");
    }

    @Test
    public void testResolveActivityByExplicitIntent() {
        matchTextWithViewTag("resolveActivity/explicit", "com.jpyy001.tools.test.plugin.general_cases.lib.usecases.packagemanager.TestPackageManagerActivity");
    }

    @Test
    public void testGetServiceInfoName() {
        matchTextWithViewTag("getServiceInfo/name", "com.jpyy001.tools.test.plugin.general_cases.lib.usecases.service.TestService");
    }

    @Test
    public void testGetServiceInfoPackageName() {
        matchTextWithViewTag("getServiceInfo/packageName", "com.jpyy001.tools.test.hostapp");
    }
}
