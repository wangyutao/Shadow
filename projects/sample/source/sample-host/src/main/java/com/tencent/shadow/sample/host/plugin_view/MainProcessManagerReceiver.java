package com.jpyy001.tools.sample.host.plugin_view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jpyy001.tools.sample.constant.Constant;
import com.jpyy001.tools.sample.host.HostApplication;

public class MainProcessManagerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        HostApplication.getApp().getPluginManager()
                .enter(context, Constant.FROM_ID_LOAD_VIEW_TO_HOST, intent.getExtras(), null);
    }
}
