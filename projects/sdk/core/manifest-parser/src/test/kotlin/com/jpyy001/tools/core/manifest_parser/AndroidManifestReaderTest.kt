package com.jpyy001.tools.core.manifest_parser

import org.junit.Assert
import org.junit.Test
import java.io.File

class AndroidManifestReaderTest {
    @Test
    fun testReadXml() {
        val testFile = File(javaClass.classLoader.getResource("sample-app.xml")!!.toURI())
        val androidManifest = AndroidManifestReader().read(testFile)
        Assert.assertEquals(
            "com.jpyy001.tools.sample.host",
            androidManifest[AndroidManifestKeys.`package`]
        )
        Assert.assertEquals(
            "com.jpyy001.tools.sample.plugin.app.lib.UseCaseApplication",
            androidManifest[AndroidManifestKeys.name]
        )
        Assert.assertEquals(
            "com.jpyy001.tools.test.plugin.androidx_cases.lib.TestComponentFactory",
            androidManifest[AndroidManifestKeys.appComponentFactory]
        )
        Assert.assertEquals(
            "@ref/0x01030006",
            androidManifest[AndroidManifestKeys.theme]
        )
    }
}