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

package com.jpyy001.tools.sample.apk.hello;

import android.content.Context;

import com.jpyy001.tools.core.common.TargetPackage;
import com.jpyy001.tools.dynamic.apk.ApkClassLoader;
import com.jpyy001.tools.dynamic.apk.ChangeApkContextWrapper;
import com.jpyy001.tools.dynamic.apk.ImplLoader;
import com.jpyy001.tools.sample.api.hello.HelloFactory;
import com.jpyy001.tools.sample.api.hello.IHelloWorldImpl;

import java.io.File;

final class HelloImplLoader extends ImplLoader {
    //指定实现类在apk中的路径
    private static final String FACTORY_CLASS_NAME = "com.jpyy001.tools.dynamic.impl.HelloFactoryImpl";
    private static final String[] REMOTE_PLUGIN_MANAGER_INTERFACES = new String[]
            {
                    "com.jpyy001.tools.core.common",
                    //注意将宿主自定义接口加入白名单
                    "com.jpyy001.tools.sample.api.hello"
            };
    final private Context applicationContext;
    final private TargetPackage targetPackage;

    HelloImplLoader(Context context, File apk) {
        applicationContext = context.getApplicationContext();
        File root = new File(applicationContext.getFilesDir(), "HelloImplLoader");
        File odexDir = new File(root, Long.toString(apk.lastModified(), Character.MAX_RADIX));
        odexDir.mkdirs();
        targetPackage = new TargetPackage(apk.getAbsolutePath(), odexDir.getAbsolutePath(), null);
    }

    IHelloWorldImpl load() {
        ApkClassLoader apkClassLoader = new ApkClassLoader(
                targetPackage,
                getClass().getClassLoader(),
                loadWhiteList(targetPackage),
                1
        );

        Context contextForApi = new ChangeApkContextWrapper(
                applicationContext,
                targetPackage.targetFilePath,
                apkClassLoader
        );

        try {
            HelloFactory factory = apkClassLoader.getInterface(
                    HelloFactory.class,
                    FACTORY_CLASS_NAME
            );
            return factory.build(contextForApi);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String[] getCustomWhiteList() {
        return REMOTE_PLUGIN_MANAGER_INTERFACES;
    }
}
