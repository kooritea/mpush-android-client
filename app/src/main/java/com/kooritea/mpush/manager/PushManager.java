package com.kooritea.mpush.manager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.kooritea.mpush.MainActivity;
import com.kooritea.mpush.R;
import com.kooritea.mpush.activity.DetailActivity;
import com.kooritea.mpush.model.Message;

import org.json.JSONException;

import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;

public class PushManager {

    private Context context;
    NotificationManager notificationManager;

    public PushManager(Context context){
        this.context = context;
        Log.d("debug",context.toString());
        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        this.createNotificationChannel("low","低优先级",NotificationManager.IMPORTANCE_LOW,"priority=low");
        this.createNotificationChannel("default","普通优先级",NotificationManager.IMPORTANCE_DEFAULT,"priority=default");
        this.createNotificationChannel("high","高优先级",NotificationManager.IMPORTANCE_HIGH,"priority=high");
    }

    private void createNotificationChannel(String channelId, String channelName, int importance, String description) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            List<NotificationChannel> channelList = notificationManager.getNotificationChannels();
            for(NotificationChannel item : channelList){
                if(item.getId() == channelId){
                    item.setName(channelName);
                    item.setImportance(importance);
                    item.setDescription(description);
                    return;
                }
            }
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void push(Message message){
        String channelId;
        int importance;
        try{
            switch (message.getData().getExtra().getString("priority")){
                case "default":
                    channelId = "default";
                    importance = NotificationCompat.PRIORITY_DEFAULT;
                    break;
                case "low":
                    channelId = "low";
                    importance = NotificationCompat.PRIORITY_LOW;
                    break;
                default:
                    channelId = "high";
                    importance = NotificationCompat.PRIORITY_HIGH;
            }
        }catch (JSONException ex){
            channelId = "high";
            importance = NotificationCompat.PRIORITY_HIGH;
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.context, channelId)
            .setSmallIcon(R.drawable.ic_launcher);
        if(message.getData().getText().length() > 0){
            builder.setContentTitle(message.getData().getText());
        }
        if(message.getData().getDesp().length() > 0){
            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.bigText(message.getData().getDesp());
            builder.setStyle(bigTextStyle);
        }

        Intent intent;
        try{
            if(message.getData().getExtra().getString("scheme").length() > 0){
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Uri.decode(message.getData().getExtra().getString("scheme"))));
            }else{
                throw new JSONException("scheme is empty String");
            }

        }catch (JSONException ex){
            intent = new Intent(this.context, DetailActivity.class);
            intent.putExtra("message",message.toString());
        }
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this.context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        builder.setPriority(importance)
            .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this.context);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
