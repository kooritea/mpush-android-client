package com.kooritea.mpush.model;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthPacket {

    public JSONObject origin;
    public int code;
    public String msg;
    public String fcmProjectId = "";
    public String fcmApplicationId = "";
    public String fcmApiKey = "";

    public AuthPacket(JSONObject data) {
        this.origin = data;
        try{
            this.code = data.getInt("code");
            this.msg = data.getString("msg");
            this.fcmProjectId = data.getString("fcmProjectId");
            this.fcmApplicationId = data.getString("fcmApplicationId");
            this.fcmApiKey = data.getString("fcmApiKey");
        }catch (JSONException ex){

        }

    }
    @Override
    public String toString() {
        return origin.toString();
    }
}
