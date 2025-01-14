package com.jpyy001.tools.test;

import com.jpyy001.tools.test.cases.plugin_androidx.AppCompatActivityTest;
import com.jpyy001.tools.test.cases.plugin_main.ThemeTest;
import com.jpyy001.tools.test.cases.plugin_main.ViewIdTest;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * com.jpyy001.tools.core.loader.blocs.CreateResourceBloc
 * 改动相关测试
 */
@RunWith(Suite.class)
@Ignore("避免自动化全量测试时重复这些测试")
@Suite.SuiteClasses({
        ViewIdTest.class,
        ThemeTest.class,
        AppCompatActivityTest.class,
})
public class CreateResourceTest {
}
