package com.kooritea.mpush.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kooritea.mpush.service.SocketManagerService;

public class KeepLiveReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if("com.kooritea.mpush.LIVE".equals(intent.getAction())){
            Intent intent1=new Intent(context, SocketManagerService.class);
            context.startService(intent1);
        }
    }
}