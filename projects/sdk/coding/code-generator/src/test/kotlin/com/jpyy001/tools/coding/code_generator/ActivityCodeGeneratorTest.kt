package com.jpyy001.tools.coding.code_generator

import org.junit.Test


internal class ActivityCodeGeneratorTest {
    @Test
    fun testLoadAndroidClass() {
        ActivityCodeGenerator.classPool.get("android.app.Activity")
    }
}