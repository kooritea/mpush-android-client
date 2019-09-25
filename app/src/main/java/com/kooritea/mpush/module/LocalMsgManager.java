package com.kooritea.mpush.module;

import android.content.Context;
import android.util.Log;

import com.kooritea.mpush.model.Message;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LocalMsgManager extends FileManager {
    private static String FILENAME = "messages.json";
    private static LocalMsgManager instance;
    private List<Message> messageListCache;

    private LocalMsgManager(Context context) {
        super(context);
    }

    public static LocalMsgManager create(Context context){
        if(instance == null){
            instance = new LocalMsgManager(context);
        }
        return instance;
    }

    public List<Message> readLocalMsgList(){
        if(messageListCache == null){
            messageListCache = new ArrayList<>();
            String raw = readFileData(FILENAME);
            String[] data = raw.split("\n");
            for (int i = 0; i < data.length; i++) {
                try{
                    JSONObject msg = new JSONObject(data[i]);
                    Message message = new Message(msg);//给实体类赋值
                    messageListCache.add(message);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        return messageListCache;
    }

    public void saveLocalMsglist(Message message){
        if(messageListCache == null){
            readLocalMsgList();
        }
        messageListCache.add(message);
        appendFileData(FILENAME,message.toString() + "\n");
    }

    public void deleteMsg(String mid){
        for(int i=0;i<messageListCache.size();i++){
            if(messageListCache.get(i).getMid().equals(mid)){
                messageListCache.remove(i--);
                break;
            }
        }
        writeFileData(FILENAME,"");
        for(int i=0;i<messageListCache.size();i++){
            appendFileData(FILENAME,messageListCache.get(i).toString() + "\n");
        }
    }

    public void clearData(){
        messageListCache = new ArrayList<>();
        writeFileData(FILENAME,"");
    }
}
