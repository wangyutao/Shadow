package com.jpyy001.tools.dynamic.impl;

import android.content.Context;

import com.jpyy001.tools.sample.api.hello.HelloFactory;
import com.jpyy001.tools.sample.api.hello.IHelloWorldImpl;
import com.jpyy001.tools.sample.api.hello.SampleHelloWorld;

/**
 * @author 林学渊
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2021/9/6
 * @description 此类包名及类名固定
 * @usage null
 */
public final class HelloFactoryImpl implements HelloFactory {
    @Override
    public IHelloWorldImpl build(Context context) {
        return new SampleHelloWorld();
    }
}
