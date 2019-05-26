package com.kooritea.mpush;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    private String title;
    private String content;
    private String mid;

    public Message(String title,  String content,  String mid){
        this.title = title;
        this.content = content;
        this.mid = mid;
    }
    public Message(JSONObject msg){
        try{
            mid = msg.getString("mid");
            try{
                title = msg.getString("title");
            }catch(Exception e){
                title = null;
            }
            try{
                content = msg.getString("content");
            }catch(Exception e){
                content = null;
            }
        }catch(Exception e){
            content = null;
        }

    }

    public boolean hasTitle(){
        return title != null && title.length()>0;
    }

    public boolean hasContent(){
        return content != null&& content.length()>0;
    }

    public int getStatus() {
        if(hasTitle() && !hasContent()){
            return 1;//只有title
        }else if(!hasTitle() && hasContent()){
            return 2;//只有content
        }else if(hasTitle() && hasContent()){
            return 3;//都有
        }else{
            return 0;//都没
        }
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public String getMid() {
        return mid;
    }

    public String getTime() {
        try{
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d1=new Date(Long.valueOf(mid));
            return format.format(d1);
        }catch(Exception e){
            return null;
        }

    }
    public JSONObject getJSON(){
        JSONObject result = new JSONObject();
        try{
            if(title != null && title.length()>0){
                result.put("title",title);
            }
            if(content != null && content.length()>0){
                result.put("content",content);
            }
            result.put("mid",mid);
        }catch(Exception e){

        }
        return result;

    }
}
