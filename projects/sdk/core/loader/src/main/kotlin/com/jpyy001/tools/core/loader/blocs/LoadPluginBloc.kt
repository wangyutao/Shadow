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

package com.jpyy001.tools.core.loader.blocs

import android.content.Context
import com.jpyy001.tools.core.common.TargetPackage
import com.jpyy001.tools.core.load_parameters.LoadParameters
import com.jpyy001.tools.core.loader.exceptions.LoadPluginException
import com.jpyy001.tools.core.loader.infos.PluginParts
import com.jpyy001.tools.core.loader.managers.ComponentManager
import com.jpyy001.tools.core.loader.managers.PluginPackageManagerImpl
import com.jpyy001.tools.core.runtime.PluginPartInfo
import com.jpyy001.tools.core.runtime.PluginPartInfoManager
import com.jpyy001.tools.core.runtime.ShadowAppComponentFactory
import java.io.File
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

object LoadPluginBloc {
    @Throws(LoadPluginException::class)
    fun loadPlugin(
        executorService: ExecutorService,
        componentManager: ComponentManager,
        lock: ReentrantLock,
        pluginPartsMap: MutableMap<String, PluginParts>,
        hostAppContext: Context,
        targetPackage: TargetPackage,
        loadParameters: LoadParameters
    ): Future<*> {
        if (targetPackage.targetFilePath == null) {
            throw LoadPluginException("targetFilePath==null")
        } else {
            val buildClassLoader = executorService.submit(Callable {
                lock.withLock {
                    LoadApkBloc.loadPlugin(targetPackage, loadParameters, pluginPartsMap)
                }
            })

            val buildPluginManifest = executorService.submit(Callable {
                val pluginClassLoader = buildClassLoader.get()
                val pluginManifest = pluginClassLoader.loadPluginManifest()
                CheckPackageNameBloc.check(pluginManifest, hostAppContext)
                pluginManifest
            })

            val buildPluginApplicationInfo = executorService.submit(Callable {
                val pluginManifest = buildPluginManifest.get()
                val pluginApplicationInfo = CreatePluginApplicationInfoBloc.create(
                    targetPackage,
                    loadParameters,
                    pluginManifest,
                    hostAppContext
                )
                pluginApplicationInfo
            })

            val buildPackageManager = executorService.submit(Callable {
                val pluginApplicationInfo = buildPluginApplicationInfo.get()
                val hostPackageManager = hostAppContext.packageManager
                PluginPackageManagerImpl(
                    pluginApplicationInfo,
                    targetPackage.targetFilePath,
                    componentManager,
                    hostPackageManager,
                )
            })

            val buildResources = executorService.submit(Callable {
                CreateResourceBloc.create(targetPackage.targetFilePath, hostAppContext)
            })

            val buildAppComponentFactory = executorService.submit(Callable {
                val pluginClassLoader = buildClassLoader.get()
                val pluginManifest = buildPluginManifest.get()
                val appComponentFactory = pluginManifest.appComponentFactory
                if (appComponentFactory != null) {
                    val clazz = pluginClassLoader.loadClass(appComponentFactory)
                    ShadowAppComponentFactory::class.java.cast(clazz.newInstance())
                } else ShadowAppComponentFactory()
            })

            val buildApplication = executorService.submit(Callable {
                val pluginClassLoader = buildClassLoader.get()
                val resources = buildResources.get()
                val appComponentFactory = buildAppComponentFactory.get()
                val pluginManifest = buildPluginManifest.get()
                val pluginApplicationInfo = buildPluginApplicationInfo.get()

                CreateApplicationBloc.createShadowApplication(
                    pluginClassLoader,
                    loadParameters,
                    pluginManifest,
                    resources,
                    hostAppContext,
                    componentManager,
                    pluginApplicationInfo,
                    appComponentFactory
                )
            })

            val buildRunningPlugin = executorService.submit {
                if (File(targetPackage.targetFilePath).exists().not()) {
                    throw LoadPluginException("文件不存在.targetFilePath==" + targetPackage.targetFilePath)
                }
                val pluginPackageManager = buildPackageManager.get()
                val pluginClassLoader = buildClassLoader.get()
                val resources = buildResources.get()
                val shadowApplication = buildApplication.get()
                val appComponentFactory = buildAppComponentFactory.get()
                val pluginManifest = buildPluginManifest.get()
                lock.withLock {
                    componentManager.addPluginApkInfo(
                        pluginManifest,
                        loadParameters,
                        targetPackage.targetFilePath,
                    )
                    pluginPartsMap[loadParameters.partKey] = PluginParts(
                        appComponentFactory,
                        shadowApplication,
                        pluginClassLoader,
                        resources,
                        pluginPackageManager
                    )
                    PluginPartInfoManager.addPluginInfo(
                        pluginClassLoader, PluginPartInfo(
                            shadowApplication, resources,
                            pluginClassLoader, pluginPackageManager
                        )
                    )
                }
            }

            return buildRunningPlugin
        }
    }


}