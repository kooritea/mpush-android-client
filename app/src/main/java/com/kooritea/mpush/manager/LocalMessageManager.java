package com.kooritea.mpush.manager;

import android.content.Context;
import android.util.Log;

import com.kooritea.mpush.model.Message;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LocalMessageManager  extends FileManager {
    private static String FILENAME = "messages.json";
    private static LocalMessageManager instance;
    private List<Message> messageListCache;

    private LocalMessageManager(Context context) {
        super(context);
    }

    public static LocalMessageManager getInstance(Context context){
        if(instance == null){
            instance = new LocalMessageManager(context);
        }
        return instance;
    }

    public List<Message> readLocalMsgList(){
        if(messageListCache == null){
            messageListCache = new ArrayList<>();
            String raw = readFileData(FILENAME);
            String[] data = raw.split("\n");
            if(!data[0].equals("")){
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


    public void deleteMsg(String mid,Boolean save){
        for(int i=0;i<messageListCache.size();i++){
            if(messageListCache.get(i).getMid().equals(mid)){
                messageListCache.remove(i--);
                break;
            }
        }
        if(save){
            this.saveToFile();
        }
    }

    public void saveToFile(){
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
