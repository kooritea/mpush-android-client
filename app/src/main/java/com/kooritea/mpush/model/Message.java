package com.kooritea.mpush.model;

import com.kooritea.mpush.database.entity.MessageEntity;

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
    public boolean isNew = false;

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

        public From(String method, String name){
            if(method == null){
                this.method = "unknown";
            }else{
                this.method = method;
            }
            if(name == null){
                this.name = "anonymous";
            }else{
                this.name = name;
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
        public Data(String text, String desp, String extra){
            this.text = text == null ?"":text;
            this.desp = desp == null ?"":desp;
            try{
                this.extra = new JSONObject(extra);
                if(this.extra == null){
                    this.extra = new JSONObject();
                }
            }catch (JSONException ex){
                this.extra = new JSONObject();
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

    public Message(MessageEntity entity){
        this.sendType = entity.sendType == null ?"":entity.sendType;
        this.target = entity.target == null ?"":entity.target;
        this.mid = entity.mid == null ?"":entity.mid;
        this.from = new From(entity.fromMethod,entity.fromName);
        this.data = new Data(entity.text,entity.desp,entity.extra);
        this.origin = new JSONObject();
        try {
            this.origin.put("sendType",this.sendType);
            this.origin.put("target",this.target);
            this.origin.put("mid",this.mid);
            JSONObject from = new JSONObject();
            from.put("method",this.from.method);
            from.put("name",this.from.name);
            this.origin.put("from",from);
            JSONObject data = new JSONObject();
            data.put("text",this.data.text);
            data.put("desp",this.data.desp);
            data.put("extra",this.data.extra);
            this.origin.put("message",data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

    @Override
    public String toString() {
        return origin.toString();
    }

    public MessageEntity toEntity(){
        MessageEntity entity = new MessageEntity();
        entity.sendType = this.sendType;
        entity.target = this.target;
        entity.mid = this.mid;
        entity.fromMethod = this.from.method;
        entity.fromName = this.from.name;
        entity.text = this.data.getText();
        entity.desp = this.data.getDesp();
        entity.extra = this.data.getExtra().toString();
        return entity;
    }
}
