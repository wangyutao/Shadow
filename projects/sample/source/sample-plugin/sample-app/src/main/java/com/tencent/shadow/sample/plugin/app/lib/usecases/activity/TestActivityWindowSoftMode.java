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

package com.jpyy001.tools.sample.plugin.app.lib.usecases.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.jpyy001.tools.sample.plugin.app.lib.R;
import com.jpyy001.tools.sample.plugin.app.lib.gallery.BaseActivity;
import com.jpyy001.tools.sample.plugin.app.lib.gallery.cases.entity.UseCase;


public class TestActivityWindowSoftMode extends BaseActivity {

    public static class Case extends UseCase {
        @Override
        public String getName() {
            return "windowSoftInputMode测试";
        }

        @Override
        public String getSummary() {
            return "测试插件中设置windowSoftInputMode是否生效";
        }

        @Override
        public Class getPageClass() {
            return TestActivityWindowSoftMode.class;
        }
    }

    private EditText mEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_softmode);

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        boolean is_state_visible = layoutParams.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE;

        TextView textView = findViewById(R.id.result);
        textView.setText("SOFT_INPUT_STATE_VISIBLE:" + is_state_visible);

        mEditText = findViewById(R.id.edit_view);
        mEditText.requestFocus();


    }


}
