package com.jpyy001.tools.test.cases.plugin_main.application_info;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;

import com.jpyy001.tools.test.cases.plugin_main.PluginMainAppTest;
import com.jpyy001.tools.test.lib.test_manager.TestManager;

import org.junit.Test;

abstract class CommonApplicationInfoTest extends PluginMainAppTest {
    private static final String PLUGIN_APK_FILENAME = "test-plugin-general-cases-plugin-debug.apk";

    protected abstract String getTag();

    @Override
    protected Intent getLaunchIntent() {
        Intent pluginIntent = new Intent();
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        pluginIntent.setClassName(
                packageName,
                "com.jpyy001.tools.test.plugin.general_cases.lib.usecases.application.TestGetApplicationInfoActivity"
        );
        return pluginIntent;
    }

    @Test
    public void testSourceDir() throws Exception {
        matchSubstringWithViewTag("TAG_sourceDir_" + getTag(), PLUGIN_APK_FILENAME);
    }

    @Test
    public void testNativeLibraryDir() throws Exception {
        matchSubstringWithViewTag("TAG_nativeLibraryDir_" + getTag(), TestManager.uuid + "_lib");
    }

    @Test
    public abstract void testMetaData();

    @Test
    public void testClassName() throws Exception {
        matchSubstringWithViewTag("TAG_className_" + getTag(),
                "com.jpyy001.tools.test.plugin.general_cases.lib.gallery.TestApplication");
    }
}
