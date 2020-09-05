package com.kooritea.mpush.manager;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SettingManager extends FileManager {
    private static String FILENAME = "settings.json";
    private static SettingManager instance;
    private HashMap<String,String> settings;


    private SettingManager(Context context){
        super(context);
        settings = getLocalSetting();
    }

    public static SettingManager getInstance(Context context){
        if(instance == null){
            instance = new SettingManager(context);
        }
        return instance;
    }

    private HashMap<String,String> getLocalSetting(){
        if (settings == null) {
            settings = new HashMap<>();
            String raw = readFileData(FILENAME);
            String[] data = raw.split("\n");
            for (int i = 0; i < data.length; i++) {
                String[] settingStr = data[i].split("=");
                if(settingStr.length>1){
                    settings.put(settingStr[0],settingStr[1]);
                }
            }

        }

        return settings;
    }
    public String get(String key){
        return settings.get(key);
    }
    public void set(String key,String value){
        settings.put(key,value);
        this.saveLocalSetting();
    }
    private void saveLocalSetting(){
        String raw = "";
        Iterator iter = settings.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String)entry.getKey();
            String val = (String)entry.getValue();
            raw += key + "=" + val + "\n";
        }
        writeFileData(FILENAME,raw);
    }
}