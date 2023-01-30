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

import android.app.Application;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.jpyy001.tools.core.common.TargetPackage;

import java.io.File;

import static com.jpyy001.tools.dynamic.host.FailedException.ERROR_CODE_FILE_NOT_FOUND_EXCEPTION;
import static com.jpyy001.tools.dynamic.host.FailedException.ERROR_CODE_RELOAD_LOADER_EXCEPTION;
import static com.jpyy001.tools.dynamic.host.FailedException.ERROR_CODE_RELOAD_RUNTIME_EXCEPTION;
import static com.jpyy001.tools.dynamic.host.FailedException.ERROR_CODE_RESET_UUID_EXCEPTION;
import static com.jpyy001.tools.dynamic.host.FailedException.ERROR_CODE_RUNTIME_EXCEPTION;
import static com.jpyy001.tools.dynamic.host.FailedException.ERROR_CODE_UUID_MANAGER_DEAD_EXCEPTION;
import static com.jpyy001.tools.dynamic.host.FailedException.ERROR_CODE_UUID_MANAGER_NULL_EXCEPTION;


public class PluginProcessService extends BasePluginProcessService {

    private final PpsBinder mPpsControllerBinder = new PpsBinder(this);

    static final ActivityHolder sActivityHolder = new ActivityHolder();

    public static Application.ActivityLifecycleCallbacks getActivityHolder() {
        return sActivityHolder;
    }

    public static PpsController wrapBinder(IBinder ppsBinder) {
        return new PpsController(ppsBinder);
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("onBind:" + this);
        }
        return mPpsControllerBinder;
    }

    private UuidManager mUuidManager;

    private PluginLoaderImpl mPluginLoader;

    private boolean mRuntimeLoaded = false;

    /**
     * 当前的Uuid。一旦设置不可修改。
     */
    private String mUuid = "";

    private void setUuid(String uuid) throws FailedException {
        if (mUuid.isEmpty()) {
            mUuid = uuid;
        } else if (!mUuid.equals(uuid)) {
            throw new FailedException(ERROR_CODE_RESET_UUID_EXCEPTION, "已设置过uuid==" + mUuid + "试图设置uuid==" + uuid);
        }
    }

    private void checkUuidManagerNotNull() throws FailedException {
        if (mUuidManager == null) {
            throw new FailedException(ERROR_CODE_UUID_MANAGER_NULL_EXCEPTION, "mUuidManager == null");
        }
    }

    void loadRuntime(String uuid) throws FailedException {
        checkUuidManagerNotNull();
        setUuid(uuid);
        if (mRuntimeLoaded) {
            throw new FailedException(ERROR_CODE_RELOAD_RUNTIME_EXCEPTION
                    , "重复调用loadRuntime");
        }
        try {
            if (mLogger.isInfoEnabled()) {
                mLogger.info("loadRuntime uuid:" + uuid);
            }
            TargetPackage targetPackage;
            try {
                targetPackage = mUuidManager.getRuntime(uuid);
            } catch (RemoteException e) {
                throw new FailedException(ERROR_CODE_UUID_MANAGER_DEAD_EXCEPTION, e.getMessage());
            } catch (NotFoundException e) {
                throw new FailedException(ERROR_CODE_FILE_NOT_FOUND_EXCEPTION, "uuid==" + uuid + "的Runtime没有找到。cause:" + e.getMessage());
            }

            TargetPackage installedRuntimeApk = new TargetPackage(targetPackage.targetFilePath, targetPackage.oDexPath, targetPackage.libraryPath);
            boolean loaded = DynamicRuntime.loadRuntime(installedRuntimeApk);
            if (loaded) {
                DynamicRuntime.saveLastRuntimeInfo(this, installedRuntimeApk);
            }
            mRuntimeLoaded = true;
        } catch (RuntimeException e) {
            if (mLogger.isErrorEnabled()) {
                mLogger.error("loadRuntime发生RuntimeException", e);
            }
            throw new FailedException(e);
        }
    }

    void loadPluginLoader(String uuid) throws FailedException {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("loadPluginLoader uuid:" + uuid + " mPluginLoader:" + mPluginLoader);
        }
        checkUuidManagerNotNull();
        setUuid(uuid);
        if (mPluginLoader != null) {
            throw new FailedException(ERROR_CODE_RELOAD_LOADER_EXCEPTION
                    , "重复调用loadPluginLoader");
        }
        try {
            TargetPackage targetPackage;
            try {
                targetPackage = mUuidManager.getPluginLoader(uuid);
                if (mLogger.isInfoEnabled()) {
                    mLogger.info("取出uuid==" + uuid + "的Loader target:" + targetPackage.targetFilePath);
                }
            } catch (RemoteException e) {
                if (mLogger.isErrorEnabled()) {
                    mLogger.error("获取Loader target失败", e);
                }
                throw new FailedException(ERROR_CODE_UUID_MANAGER_DEAD_EXCEPTION, e.getMessage());
            } catch (NotFoundException e) {
                throw new FailedException(ERROR_CODE_FILE_NOT_FOUND_EXCEPTION, "uuid==" + uuid + "的PluginLoader没有找到。cause:" + e.getMessage());
            }
            File file = new File(targetPackage.targetFilePath);
            if (!file.exists()) {
                throw new FailedException(ERROR_CODE_FILE_NOT_FOUND_EXCEPTION, file.getAbsolutePath() + "文件不存在");
            }

            PluginLoaderImpl pluginLoader = new LoaderImplLoader().load(targetPackage, uuid, getApplicationContext());
            pluginLoader.setUuidManager(mUuidManager);
            mPluginLoader = pluginLoader;
        } catch (RuntimeException e) {
            if (mLogger.isErrorEnabled()) {
                mLogger.error("loadPluginLoader发生RuntimeException", e);
            }
            throw new FailedException(e);
        } catch (FailedException e) {
            throw e;
        } catch (Exception e) {
            if (mLogger.isErrorEnabled()) {
                mLogger.error("loadPluginLoader发生Exception", e);
            }
            String msg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            throw new FailedException(ERROR_CODE_RUNTIME_EXCEPTION, "加载失败 cause：" + msg);
        }
    }

    void setUuidManager(UuidManager uuidManager) {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("setUuidManager uuidManager==" + uuidManager);
        }
        mUuidManager = uuidManager;
        if (mPluginLoader != null) {
            if (mLogger.isInfoEnabled()) {
                mLogger.info("更新mPluginLoader的uuidManager");
            }
            mPluginLoader.setUuidManager(uuidManager);
        }
    }

    void exit() {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("exit ");
        }
        PluginProcessService.sActivityHolder.finishAll();
        System.exit(0);
        try {
            wait();
        } catch (InterruptedException ignored) {
        }
    }

    PpsStatus getPpsStatus() {
        return new PpsStatus(mUuid, mRuntimeLoaded, mPluginLoader != null, mUuidManager != null);
    }

    IBinder getPluginLoader() {
        return mPluginLoader;
    }
}
