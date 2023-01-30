package com.jpyy001.tools.test.cases.plugin_androidx;

import com.jpyy001.tools.test.PluginTest;
import com.jpyy001.tools.test.lib.constant.Constant;

public abstract class PluginAndroidxAppTest extends PluginTest {

    @Override
    protected String getPartKey() {
        return Constant.PART_KEY_PLUGIN_ANDROIDX;
    }
}
