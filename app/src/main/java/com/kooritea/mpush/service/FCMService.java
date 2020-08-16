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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class FCMService extends FirebaseMessagingService {

    private PushManager pushManager;
    private SettingManager settingManager = SettingManager.getInstance(this);

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
        this.updateToken(settingManager.get("FCM-TOKEN"));
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
        this.settingManager.set("FCM-TOKEN",s);
        this.updateToken(s);
    }

    private void updateToken(final String token){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                String result = "";
                try {
                    URL url = new URL(settingManager.get("URL"));
                    connection = (HttpURLConnection) url.openConnection();
                    //设置请求方法
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("authorization",settingManager.get("TOKEN"));
                    connection.setRequestProperty("Content-type","application/json");
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    PrintWriter out = new PrintWriter(connection.getOutputStream());
                    out.print(
                            "{" +
                            "    \"cmd\": \"REGISTER_FCM\"," +
                            "    \"auth\": \""+settingManager.get("authorization")+"\"," +
                            "    \"data\": {" +
                                     "\"token\":\""+token+"\"" +
                                 "}" +
                            "}"
                    );
                    out.flush();
                    //设置连接超时时间（毫秒）
                    connection.setConnectTimeout(5000);
                    //设置读取超时时间（毫秒）
                    connection.setReadTimeout(5000);
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {//关闭连接
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}