package com.kooritea.mpush;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingManager extends FileManager {
    private static String SETTINGFILE = "setting";
    public SettingManager(Context context){
        super(context);
    }
    public HashMap<String,String> getLocalSetting(){
        HashMap<String,String> settings = new HashMap<>();
        String raw = readFileData(SETTINGFILE);
        String[] data = raw.split("\n");
        for (int i = 0; i < data.length; i++) {
            String[] settingStr = data[i].split("=");
            if(settingStr.length>1){
                settings.put(settingStr[0],settingStr[1]);
            }
        }

        return settings;
    }
    public String getSetting(String key){
        HashMap<String,String> settings = getLocalSetting();
        return settings.get(key);
    }
    public void saveLocalSetting(HashMap<String,String> settings){
        String raw = "";
        Iterator iter = settings.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String)entry.getKey();
            String val = (String)entry.getValue();
            raw += key + "=" + val + "\n";
        }
        writeFileData(SETTINGFILE,raw);
    }
}
