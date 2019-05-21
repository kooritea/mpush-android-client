package com.kooritea.mpush;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

public class CheckLiveReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if("com.kooritea.mpush.LIVE".equals(intent.getAction())){
            Intent intent1=new Intent(context,MsgNotificationService.class);
            //广播跳Activity
            context.startService(intent1);
//            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
}
