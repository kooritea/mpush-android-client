package com.kooritea.mpush.model;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    private String sendType;
    private String target;
    private String mid;
    private From from;
    private Data data;
    private JSONObject origin;

    public class From {
        private String method;
        private String name;

        public From(JSONObject from) {
            try{
                method = from.getString("method");
                name = from.getString("name");
                if(method == null){
                    method = "unknown";
                }
                if(name == null){
                    name = "anonymous";
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        public String getMethod() {
            return method == null ? "unknown" : method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getName() {
            return name == null ? "anonymous" : name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    public class Data {
        private String text;
        private String desp;
        private JSONObject extra;

        public Data(JSONObject data) {
            try{
                text = data.getString("text");
                desp = data.getString("desp");
                extra = data.getJSONObject("extra");
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        public String getText() {
            return text == null ? "" : text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getDesp() {
            return desp == null ? "" : desp;
        }

        public void setDesp(String desp) {
            this.desp = desp;
        }

        public JSONObject getExtra() {
            return extra;
        }

        public void setExtra(JSONObject extra) {
            this.extra = extra;
        }
    }

    public Message(JSONObject msg) throws JSONException {
        origin = msg;
        sendType = msg.getString("sendType");
        target = msg.getString("target");
        mid = msg.getString("mid");
        from = new From(msg.getJSONObject("from"));
        data = new Data(msg.getJSONObject("message"));
    }

    public Message(String originString) throws JSONException {
        origin = new JSONObject(originString);
        sendType = origin.getString("sendType");
        target = origin.getString("target");
        mid = origin.getString("mid");
        from = new From(origin.getJSONObject("from"));
        data = new Data(origin.getJSONObject("message"));
    }


    public String getSendType() {
        return sendType;
    }

    public String getTarget() {
        return target;
    }

    public From getFrom() {
        return from;
    }

    public Data getData() {
        return data;
    }

    public String getMid() {
        return mid;
    }

    private boolean hasTitle(){
        return data.getText().length() > 0;
    }

    private boolean hasContent(){
        return data.getDesp().length() > 0;
    }

    public int getViewType() {
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

    public String getTime() {
        try{
            SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d1=new Date(Long.valueOf(mid));
            return format.format(d1);
        }catch(Exception e){
            return "时间戳格式化错误";
        }
    }

    @NonNull
    @Override
    public String toString() {
        return origin.toString();
    }
}
