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

package com.jpyy001.tools.test.cases.plugin_main;

import android.content.Intent;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class ActivityReCreateTest extends PluginMainAppTest {

    @Override
    protected Intent getLaunchIntent() {
        Intent pluginIntent = new Intent();
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        pluginIntent.setClassName(
                packageName,
                "com.jpyy001.tools.test.plugin.general_cases.lib.usecases.activity.TestActivityReCreate"
        );
        pluginIntent.putExtra("reCreateMsg", "beforeReCreate");
        return pluginIntent;
    }

    @Test
    public void testOnCreate() {
        matchTextWithViewTag("tv_msg", "reCreateMsg:beforeReCreate");
    }

    @Test
    public void testReCreate() {
        matchTextWithViewTag("tv_msg", "reCreateMsg:beforeReCreate");

        Espresso.onView(ViewMatchers.withTagValue(Matchers.<Object>is("button"))).perform(ViewActions.click());

        matchTextWithViewTag("tv_msg", "reCreateMsg:afterReCreate");
    }
}
