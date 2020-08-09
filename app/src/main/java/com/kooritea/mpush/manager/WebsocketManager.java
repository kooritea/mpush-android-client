package com.kooritea.mpush.manager;

import android.content.Context;
import android.util.Log;

import com.kooritea.mpush.model.AuthPacket;
import com.kooritea.mpush.model.Message;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;


public class WebsocketManager {
    private class MpushSocketClient extends WebSocketClient {
        private String url;
        private String token;
        private String name;
        private String group;
        private EventManager eventManager;
        private boolean isDestory;

        public MpushSocketClient(String url,String token,String name,String group,EventManager eventManager){
            super(URI.create(url));
            this.isDestory = false;
            this.url = url;
            this.token = token;
            this.name = name;
            this.group = group;
            this.eventManager = eventManager;
            this.connect();
        }

        @Override
        public void onOpen(ServerHandshake handshakeData) {
            try {
                send(new JSONObject("{" +
                        "    cmd: 'AUTH'," +
                        "    data: {" +
                        "        token: '" + token + "'," +
                        "        name: '" + name + "'," +
                        "        group: '" + group + "'" +
                        "    }" +
                        "}").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onMessage(String originMessage) {
            try {
                JSONObject packet = new JSONObject(originMessage);
                String cmd = packet.getString("cmd");
                JSONObject data = packet.getJSONObject("data");
                switch(cmd){
                    case "AUTH":
                        this.emit("mpush-auth",new AuthPacket(data));
                        break;
                    case "MESSAGE":
                        Message message = new Message(data);
                        this.emit("mpush-message",message);
                        this.send(new JSONObject("{" +
                                "    cmd: 'MESSAGE_CALLBACK'," +
                                "    data: {" +
                                "        mid: '" + message.getMid() + "'" +
                                "    }" +
                                "}").toString());
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            this.emit("ws-close",code);
        }

        @Override
        public void onError(Exception ex) {
            this.emit("ws-error",ex);
        }

        private void emit(String event,Object o){
            if(!this.isDestory){
                this.eventManager.emit(event,o);
            }
        }

        public void destory(){
            this.isDestory = true;
            this.close();
        }
    }
    private static WebsocketManager instance;

    public static WebsocketManager getInstance(Context context){
        if(instance == null){
            synchronized(WebsocketManager.class){
                if(instance == null){
                    instance = new WebsocketManager(context);
                }
            }
        }
        return instance;
    }

    private MpushSocketClient mpushSocketClient;
    private EventManager wsEventManager;
    private EventManager wsManagerEventManager;
    private SettingManager settingManager;
    private boolean userClose;
    private boolean needReConnect;

    private WebsocketManager(Context context){
        this.wsEventManager = new EventManager();
        this.wsManagerEventManager = new EventManager();
        this.settingManager = SettingManager.getInstance(context);
        this.userClose = false;
        this.needReConnect = false;
        this.wsEventManager.on("mpush-auth", new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                whenAuth((AuthPacket)o);
            }
        });
        this.wsEventManager.on("mpush-message", new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                whenNewMessage((Message)o);
            }
        });
        this.wsEventManager.on("ws-close", new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                whenClose((int)o);
            }
        });
        this.wsEventManager.on("ws-error", new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                whenError((Exception)o);
            }
        });
    }

    private void whenAuth(AuthPacket authPacket){
        if(authPacket.code == 200){
            this.settingManager.set("fcmProjectId",authPacket.fcmProjectId);
            this.settingManager.set("fcmApplicationId",authPacket.fcmApplicationId);
            this.settingManager.set("fcmApiKey",authPacket.fcmApiKey);
            this.needReConnect = true;
            this.wsManagerEventManager.emit("AUTH",authPacket);
            new Timer("websocket-ping").schedule(new TimerTask() {
                @Override
                public void run() {
                    if(mpushSocketClient.isOpen()){
                        mpushSocketClient.sendPing();
                    }
                }
            }, 60000, 60000);
        }else{
            this.wsManagerEventManager.emit("ERROR",new Exception(authPacket.msg));
        }

    }
    private void whenNewMessage(Message message){
        this.wsManagerEventManager.emit("MESSAGE",message);
    }
    private void whenClose(int code){
        if(this.needReConnect && !this.userClose){
            new Timer("websocket-ping").schedule(new TimerTask() {
                 @Override
                 public void run() {
                     mpushSocketClient.reconnect();
                 }
             }, 5000);
        }else{
            this.wsManagerEventManager.emit("CLOSE",null);
        }
    }
    private void whenError(Exception ex){
        ex.printStackTrace();
        if(this.needReConnect && !this.userClose){
            new Timer("websocket-ping").schedule(new TimerTask() {
                @Override
                public void run() {
                    mpushSocketClient.reconnect();
                }
            }, 5000);
        }else{
            this.wsManagerEventManager.emit("ERROR",ex);
        }
    }




    public void tryConnect(){
        if(this.mpushSocketClient != null){
            this.mpushSocketClient.destory();
        }
        this.mpushSocketClient = new MpushSocketClient(this.settingManager.get("URL"),this.settingManager.get("TOKEN"),this.settingManager.get("NAME"),this.settingManager.get("GROUP"),this.wsEventManager);
    }

    /**
     * 认证成功
     */
    public void onAuth(Observer observer){
        this.wsManagerEventManager.on("AUTH",observer);
    }

    /**
     * cmd MESSAGE
     * @param observer
     */

    public void onMessage(Observer observer){
        this.wsManagerEventManager.on("MESSAGE",observer);
    }

    /**
     * 连接已关闭，并不重连
     * @param observer
     */
    public void onClose(Observer observer){
        this.wsManagerEventManager.on("CLOSE",observer);
    }

    /**
     * 可能是websocket层出错也可能是Mpush层出错
     * @param observer
     */
    public void onError(Observer observer){
        this.wsManagerEventManager.on("ERROR",observer);
    }

    public void send(String cmd,String data) throws JSONException {
        this.mpushSocketClient.send((new JSONObject(
                "{" +
                "    cmd: '"+cmd+"'," +
                "    data: " + data +
                "}"
        )).toString());
    }

    /**
     * 关闭连接，并且不重连
     */
    public void close(){
        this.userClose = true;
        this.mpushSocketClient.destory();
    }

}
