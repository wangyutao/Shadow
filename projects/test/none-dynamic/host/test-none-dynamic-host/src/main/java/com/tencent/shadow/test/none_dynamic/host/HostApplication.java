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

package com.jpyy001.tools.test.none_dynamic.host;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Parcel;
import android.os.StrictMode;

import com.jpyy001.tools.core.common.TargetPackage;
import com.jpyy001.tools.core.common.LoggerFactory;
import com.jpyy001.tools.core.load_parameters.LoadParameters;
import com.jpyy001.tools.core.loader.ShadowPluginLoader;
import com.jpyy001.tools.core.runtime.container.ContentProviderDelegateProviderHolder;
import com.jpyy001.tools.core.runtime.container.DelegateProviderHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class HostApplication extends Application {
    private static Application sApp;

    public final static String PART_MAIN = "partMain";

    private static final PreparePluginApkBloc sPluginPrepareBloc
            = new PreparePluginApkBloc(
            "plugin.apk"
    );

    static {
        detectNonSdkApiUsageOnAndroidP();

        LoggerFactory.setILoggerFactory(new SLoggerFactory());
    }

    private ShadowPluginLoader mPluginLoader;

    private final Map<String, TargetPackage> mPluginMap = new HashMap<>();

    public void loadPlugin(final String partKey, final Runnable completeRunnable) {
        TargetPackage targetPackage = mPluginMap.get(partKey);
        if (targetPackage == null) {
            throw new NullPointerException("partKey == " + partKey);
        }

        if (mPluginLoader.getPluginParts(partKey) == null) {
            // 插件访问宿主类的白名单
            String[] hostWhiteList = new String[]{
                    "androidx.test.espresso",//这个包添加是为了general-cases插件中可以访问测试框架的类
                    "com.jpyy001.tools.test.lib.plugin_use_host_code_lib.interfaces"//测试插件访问宿主白名单类
            };
            LoadParameters loadParameters = new LoadParameters(null,
                    partKey,
                    null,
                    hostWhiteList);

            Parcel parcel = Parcel.obtain();
            loadParameters.writeToParcel(parcel, 0);
            final TargetPackage plugin = new TargetPackage(
                    targetPackage.targetFilePath,
                    targetPackage.oDexPath,
                    targetPackage.libraryPath,
                    parcel.marshall()
            );
            parcel.recycle();

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    ShadowPluginLoader pluginLoader = mPluginLoader;
                    Future<?> future = null;
                    try {
                        future = pluginLoader.loadPlugin(plugin);
                        future.get(10, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("加载失败", e);
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    mPluginLoader.callApplicationOnCreate(partKey);
                    completeRunnable.run();
                }
            }.execute();
        } else {
            completeRunnable.run();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;

        ShadowPluginLoader loader = mPluginLoader = new TestPluginLoader(getApplicationContext());
        loader.onCreate();
        DelegateProviderHolder.setDelegateProvider(loader.getDelegateProviderKey(), loader);
        ContentProviderDelegateProviderHolder.setContentProviderDelegateProvider(loader);

        TargetPackage targetPackage = sPluginPrepareBloc.preparePlugin(this.getApplicationContext());
        mPluginMap.put(PART_MAIN, targetPackage);
    }

    private static void detectNonSdkApiUsageOnAndroidP() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return;
        }
        boolean isRunningEspressoTest;
        try {
            Class.forName("android.support.test.espresso.Espresso");
            isRunningEspressoTest = true;
        } catch (Exception ignored) {
            isRunningEspressoTest = false;
        }
        if (isRunningEspressoTest) {
            return;
        }
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        builder.detectNonSdkApiUsage();
        StrictMode.setVmPolicy(builder.build());
    }

    public static Application getApp() {
        return sApp;
    }

    public ShadowPluginLoader getPluginLoader() {
        return mPluginLoader;
    }
}
