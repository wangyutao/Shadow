/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.jpyy001.tools.dynamic.host;

import android.content.Context;

import com.jpyy001.tools.core.common.TargetPackage;
import com.jpyy001.tools.dynamic.apk.ApkClassLoader;
import com.jpyy001.tools.dynamic.apk.ChangeApkContextWrapper;
import com.jpyy001.tools.dynamic.apk.ImplLoader;

import java.io.File;

final class ManagerImplLoader extends ImplLoader {
    private static final String MANAGER_FACTORY_CLASS_NAME = "com.jpyy001.tools.dynamic.impl.ManagerFactoryImpl";
    private static final String[] REMOTE_PLUGIN_MANAGER_INTERFACES = new String[]
            {
                    "com.jpyy001.tools.core.common",
                    "com.jpyy001.tools.dynamic.host"
            };
    final private Context applicationContext;
    final private TargetPackage targetPackage;

    ManagerImplLoader(Context context, File apk) {
        applicationContext = context.getApplicationContext();
        File root = new File(applicationContext.getFilesDir(), "ManagerImplLoader");
        File odexDir = new File(root, Long.toString(apk.lastModified(), Character.MAX_RADIX));
        odexDir.mkdirs();
        targetPackage = new TargetPackage(apk.getAbsolutePath(), odexDir.getAbsolutePath(), null);
    }

    PluginManagerImpl load() {
        ApkClassLoader apkClassLoader = new ApkClassLoader(
                targetPackage,
                getClass().getClassLoader(),
                loadWhiteList(targetPackage),
                1
        );

        Context pluginManagerContext = new ChangeApkContextWrapper(
                applicationContext,
                targetPackage.targetFilePath,
                apkClassLoader
        );

        try {
            ManagerFactory managerFactory = apkClassLoader.getInterface(
                    ManagerFactory.class,
                    MANAGER_FACTORY_CLASS_NAME
            );
            return managerFactory.buildManager(pluginManagerContext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    protected String[] getCustomWhiteList() {
        return REMOTE_PLUGIN_MANAGER_INTERFACES;
    }
}
