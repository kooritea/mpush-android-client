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
        String channelId = "default";
        String channelName = "default";
        String description = "default";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        try{
            if(message.getData().getExtra().getString("channelId").length() > 0){
                channelId = message.getData().getExtra().getString("channelId");
                channelName = channelId;
                description = "消息中定义的频道";
            }
        }catch (JSONException ex){}
        this.createNotificationChannel(channelId,channelName,importance,description);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.context, channelId)
            .setSmallIcon(R.drawable.ic_launcher);
        if(message.getData().getText().length() > 0){
            builder.setContentTitle(message.getData().getText());
        }
        if(message.getData().getDesp().length() > 0){
            builder.setContentText(message.getData().getDesp());
        }

        Intent intent = new Intent(this.context, DetailActivity.class);;
        try{
            if(message.getData().getExtra().getString("scheme").length() > 0){
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(message.getData().getExtra().getString("scheme")));
            }
        }catch (JSONException ex){

        }
        intent.putExtra("message",message.toString());
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this.context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);



        builder.setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this.context);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
