package com.kooritea.mpush.service;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.kooritea.mpush.DetailActivity;
import com.kooritea.mpush.MainActivity;
import com.kooritea.mpush.R;
import com.kooritea.mpush.SocketManagerAidl;
import com.kooritea.mpush.model.Message;
import com.kooritea.mpush.module.LocalMsgManager;
import com.kooritea.mpush.module.MpushWSClient;
import com.kooritea.mpush.module.SettingManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketManagerService extends Service {

    public ExecutorService cachedThreadPool;

    private IBinder iBinder = new SocketBinder();
    private Context context = this;
    private String toastText;
    private LocalMsgManager localMsgManager = LocalMsgManager.create(context);
    private SettingManager settingManager;
    private MpushWSClient mpushWSClient;


    private class SocketBinder extends SocketManagerAidl.Stub {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) {
            // Does nothing
        }

        public void reConnection(){
            mpushWSClient.updateSetting(settingManager.get("URL"),settingManager.get("TOKEN"),settingManager.get("NAME"),settingManager.get("GROUP"));
        }
        public void cancelNotif(){
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancelAll();
        }
        public void exit(int status){
            System.exit(status);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        cachedThreadPool = Executors.newCachedThreadPool();
        settingManager = SettingManager.create(this);
        mpushWSClient = new MpushWSClient(settingManager.get("URL"),settingManager.get("TOKEN"),settingManager.get("NAME"),settingManager.get("GROUP"),10000,this);


        AlarmManager aManager=(AlarmManager)getSystemService(Service.ALARM_SERVICE);
        Intent intent=new Intent("com.kooritea.mpush.LIVE");
        PendingIntent pi=PendingIntent.getBroadcast(SocketManagerService.this,0,intent,0);
        aManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),30000,pi);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        int retVal = super.onStartCommand(intent, flags, startId);
        return retVal;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }


    private void pushNotification(Message message){
        NotificationCompat.Builder builder =  new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        switch(message.getViewType()){
            case 1:
                builder.setContentTitle(message.getData().getText());
                break;
            case 2:
                builder.setContentTitle("消息通知");
                builder.setContentText(message.getData().getDesp());
                break;
            case 3:
                builder.setContentTitle(message.getData().getText());
                builder.setContentText(message.getData().getDesp());
                break;
        }
        Intent intent;
        if(message.getData().getExtra().getScheme() != null){
            intent = new Intent(Intent.ACTION_VIEW, message.getData().getExtra().getScheme());
        }else{
            intent = new Intent(this, DetailActivity.class);
        }
        intent.putExtra("message",message.toString());
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);

        //悬浮窗
        builder.setDefaults(~0);
        builder.setPriority(Notification.PRIORITY_HIGH);

        NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        int id = (int) (System.currentTimeMillis());
        nm.notify(id,notification);
    }

    public void newMessage(Message message){
        if(localMsgManager.readLocalMsgList().size() != 0 ){
            Message lastMessage = localMsgManager.readLocalMsgList().get(localMsgManager.readLocalMsgList().size()-1);
            if(message.getMid().equals(lastMessage.getMid())){
               return;
            }
        }
        pushNotification(message);
        localMsgManager.saveLocalMsglist(message);
        context.sendBroadcast(new Intent("com.kooritea.mpush.MESSAGE"));

    }

    public void toast(String text){
        toastText = text;
        Handler handler=new Handler(Looper.getMainLooper());
        handler.post(new Runnable(){
            public void run(){
                Toast toast = Toast.makeText(context, null, Toast.LENGTH_LONG);
                toast.setText(toastText);
                toast.show();
            }
        });
    }
}
