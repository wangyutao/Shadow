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

package com.jpyy001.tools.test.dynamic.host;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;
import android.webkit.WebView;

import com.jpyy001.tools.core.common.LoggerFactory;
import com.jpyy001.tools.dynamic.host.DynamicRuntime;
import com.jpyy001.tools.dynamic.host.PluginManager;
import com.jpyy001.tools.test.dynamic.host.manager.Shadow;

import java.io.File;

public class HostApplication extends Application {
    private static HostApplication sApp;

    private PluginManager mPluginManager;

    final public SimpleIdlingResourceImpl mIdlingResource = new SimpleIdlingResourceImpl();

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;

        detectNonSdkApiUsageOnAndroidP();

        LoggerFactory.setILoggerFactory(new AndroidLogLoggerFactory());

        //在全动态架构中，Activity组件没有打包在宿主而是位于被动态加载的runtime，
        //为了防止插件crash后，系统自动恢复crash前的Activity组件，此时由于没有加载runtime而发生classNotFound异常，导致二次crash
        //因此这里恢复加载上一次的runtime
        DynamicRuntime.recoveryRuntime(this);

        PluginHelper.getInstance().init(this);

        //Using WebView from more than one process at once with the same data directory is not supported.
        //https://crbug.com/558377
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WebView.setDataDirectorySuffix(Application.getProcessName());
        }
    }

    private static void detectNonSdkApiUsageOnAndroidP() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return;
        }
        boolean isRunningEspressoTest;
        try {
            Class.forName("androidx.test.espresso.Espresso");
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

    public static HostApplication getApp() {
        return sApp;
    }

    public void loadPluginManager(File apk) {
        if (mPluginManager == null) {
            mPluginManager = Shadow.getPluginManager(apk);
        }
    }

    public PluginManager getPluginManager() {
        return mPluginManager;
    }
}
