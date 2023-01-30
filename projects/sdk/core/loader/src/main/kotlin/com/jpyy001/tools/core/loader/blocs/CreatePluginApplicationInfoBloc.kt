package com.jpyy001.tools.core.loader.blocs

import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import com.jpyy001.tools.core.common.TargetPackage
import com.jpyy001.tools.core.load_parameters.LoadParameters
import com.jpyy001.tools.core.runtime.PluginManifest
import com.jpyy001.tools.core.runtime.ShadowContext
import java.io.File

object CreatePluginApplicationInfoBloc {
    fun create(
        targetPackage: TargetPackage,
        loadParameters: LoadParameters,
        pluginManifest: PluginManifest,
        hostAppContext: Context
    ): ApplicationInfo {
        val result = ApplicationInfo(hostAppContext.applicationInfo)
        result.sourceDir = targetPackage.targetFilePath
        result.nativeLibraryDir = targetPackage.libraryPath
        result.dataDir = makeDataDir(loadParameters, hostAppContext).absolutePath

        result.packageName = pluginManifest.applicationPackageName
        result.className = pluginManifest.applicationClassName
        result.theme = pluginManifest.applicationTheme
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            result.appComponentFactory = pluginManifest.appComponentFactory
        }
        return result
    }

    fun makeDataDir(loadParameters: LoadParameters, hostAppContext: Context): File {
        val tempContext = ShadowContext(hostAppContext, 0).apply {
            setBusinessName(loadParameters.businessName)
        }
        val dataDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tempContext.dataDir
        } else {
            File(tempContext.filesDir, "dataDir")
        }
        dataDir.mkdirs()
        return dataDir
    }
}