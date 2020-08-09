package com.kooritea.mpush.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kooritea.mpush.manager.PushManager;
import com.kooritea.mpush.manager.SettingManager;
import com.kooritea.mpush.model.Message;

public class FCMService extends FirebaseMessagingService {

    private PushManager pushManager;

    /**
     * Notification Message 只有应用在前台才会调用这个方法，在后台时会自动发送通知
     * Data Message 不管应用在前后台都调用这个方法，没有默认行为
     * @param remoteMessage
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if(this.pushManager == null){
            this.pushManager = new PushManager(this);
        }
        if (remoteMessage.getData().size() > 0) {
            // 推送中所含的键值对都可以在这里进行获取
            try{
                Message message = new Message(remoteMessage.getData().get("data"));
                this.pushManager.push(message);
            }catch (Exception ex){
                ex.printStackTrace();
            }

        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        SettingManager.getInstance(this).set("FCM-TOKEN",s);
    }
}