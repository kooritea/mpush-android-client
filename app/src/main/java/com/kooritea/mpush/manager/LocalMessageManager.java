package com.kooritea.mpush.manager;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;

import com.kooritea.mpush.database.AppDatabase;
import com.kooritea.mpush.database.entity.MessageEntity;
import com.kooritea.mpush.model.Message;

import java.util.ArrayList;
import java.util.List;

public class LocalMessageManager {
    private static LocalMessageManager instance;
    private AppDatabase.MessageDao messageDao;
    private List<Message> messageListCache;

    private LocalMessageManager(Context context) {
        this.messageDao = Room.databaseBuilder(context,
                AppDatabase.class, "mpush-database").allowMainThreadQueries().fallbackToDestructiveMigration().build().messageDao();
    }

    public static LocalMessageManager getInstance(Context context){
        if(instance == null){
            instance = new LocalMessageManager(context);
        }
        return instance;
    }

    public List<Message> readLocalMsgList(){
        if(this.messageListCache == null){
            this.messageListCache = new ArrayList<>();
            List<MessageEntity> entityList = this.messageDao.getAll();
            for(MessageEntity entity : entityList){
                messageListCache.add(new Message(entity));
            }
        }
        return messageListCache;
    }

    public void saveLocalMsglist(Message message){
        messageListCache.add(message);
        this.messageDao.insertAll(message.toEntity());
    }


    public void deleteMsg(String mid){
        this.messageDao.delete(this.messageDao.findByMid(mid));
        for(Message message : this.messageListCache){
            this.messageListCache.remove(message);
            break;
        }
    }
}
