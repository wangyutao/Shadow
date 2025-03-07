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

package com.jpyy001.tools.dynamic.impl;

/**
 * 此类包名及类名固定
 * classLoader的白名单
 * hello.apk 可以加载宿主中位于白名单内的类
 */
public interface WhiteList {
    String[] sWhiteList = new String[]
            {
                    "com.tencent.host.shadow",
                    "com.jpyy001.tools.test.lib.constant",
            };
}
