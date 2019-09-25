package com.kooritea.mpush.module;

import com.kooritea.mpush.model.Message;
import com.kooritea.mpush.service.SocketManagerService;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

public class MpushWSClient {

    private String serverUri;
    private String token;
    private String name;
    private String group;
    private int heartInterval;
    private SocketManagerService context;
    private WebSocketClient socket;

    public MpushWSClient(String serverUri, String token, String name, String group, int heartInterval, SocketManagerService context) {
        this.serverUri = serverUri;
        this.token = token;
        this.name = name;
        this.group = group;
        this.heartInterval = heartInterval;
        this.context = context;
        if(serverUri!=null){
            connect();
            sendHeart();
        }

    }

    private void sendHeart(){
        context.cachedThreadPool.execute(new Runnable() {
            public void run() {
                try{
                    if(socket.isOpen()){
                        socket.sendPing();
                    }else{
                        socket.reconnect();
                    }
                    Thread.sleep(heartInterval);
                }catch (Exception e ){

                }
                sendHeart();
            }
        });
    }

    private void sendReply(String mid) throws JSONException {
        socket.send(new JSONObject("{" +
                "    cmd: 'MESSAGE_CALLBACK'," +
                "    data: {" +
                "        mid: '" + mid + "'" +
                "    }" +
                "}").toString());
    }

    private void connect(){
        socket = new WebSocketClient(URI.create(serverUri)){

            @Override
            public void onOpen(ServerHandshake handshakedata) {
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
                            if(data.getInt("code") == 200){
                                context.toast("消息服务已在后台启动");
                            }else{
                                context.toast("身份验证失败: "+data.getString("msg"));
                            }
                            break;
                        case "MESSAGE":
                            Message message = new Message(data);
                            context.newMessage(message);
                            sendReply(message.getMid());
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onClose(int code, String reason, boolean remote) {

            }

            @Override
            public void onError(Exception ex) {
                context.toast("连接失败: "+ex.toString());
            }
        };
        socket.connect();
    }
    public void updateSetting(String serverUri, String token, String name, String group){
        this.serverUri = serverUri;
        this.token = token;
        this.name = name;
        this.group = group;
        if(socket != null){
            socket.close();
        }
        connect();
    }

}
