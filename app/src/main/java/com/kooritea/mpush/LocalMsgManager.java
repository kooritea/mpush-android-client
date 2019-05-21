package com.kooritea.mpush;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LocalMsgManager extends FileManager {
    private static String MSGFILE = "data";
    public LocalMsgManager(Context context){
        super(context);
    }
    public List<Message> readLocalMsgList(){
        List<Message> messageList = new ArrayList<Message>();
        String raw = readFileData(MSGFILE);
        String[] data = raw.split("\n");
        for (int i = 0; i < data.length; i++) {
            try{
                JSONObject msg = new JSONObject(data[i]);
                Message message = new Message(msg);//给实体类赋值
                messageList.add(message);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return messageList;
    }

    public void saveLocalMsglist(String data){
        appendFileData(MSGFILE,data + "\n");
    }

    public void deleteMsg(String mid){
        List<Message> msgList = readLocalMsgList();
        for(int i=0;i<msgList.size();i++){
            if(msgList.get(i).getMid().equals(mid)){
                msgList.remove(i--);
                break;
            }
        }
        clearData();
        for(int i=0;i<msgList.size();i++){
            saveLocalMsglist(msgList.get(i).getJSON().toString());
        }
    }

    public void clearData(){
        writeFileData(MSGFILE,"");
    }
}
