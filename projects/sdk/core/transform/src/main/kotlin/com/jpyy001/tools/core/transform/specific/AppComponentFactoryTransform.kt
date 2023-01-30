package com.jpyy001.tools.core.transform.specific

class AppComponentFactoryTransform : SimpleRenameTransform(
    mapOf(
        "android.app.AppComponentFactory"
                to "com.jpyy001.tools.core.runtime.ShadowAppComponentFactory"
    )
)
