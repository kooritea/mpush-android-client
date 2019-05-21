package com.kooritea.mpush;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;
import java.util.regex.Pattern;

public class SettingActivity extends AppCompatActivity {

    private SettingManager settingManager = new SettingManager(this);
    private EditText setting_url;
    private EditText setting_token;
    private EditText setting_device;

    private MsgNotifService msgNotifService;
    private Intent MsgNotificationService;
    private boolean isBound = false; //是否绑定了服务
    private ServiceConnection serviceConnection = new ServiceConnection() {
        // 服务连接成功回调
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            msgNotifService = MsgNotifService.Stub.asInterface(service);

        }
        // 服务失去连接回调
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MsgNotificationService = new Intent(this, MsgNotificationService.class);
        startService(MsgNotificationService);
        onBindServiceClick();
        setContentView(R.layout.activity_setting);
        Button btn1 = (Button) findViewById(R.id.save_button);
        setting_url = findViewById (R.id.setting_url);
        setting_token = findViewById (R.id.setting_token);
        setting_device = findViewById (R.id.setting_device);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        init();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //当Activity被销毁时解绑服务，因为如果已经绑定服务不显式解绑会报异常。
        onUnbindServiceClick();
    }
    private void onBindServiceClick() {
        bindService(MsgNotificationService, serviceConnection, BIND_AUTO_CREATE);// 绑定时如果没有创建服务则自动创建Service。
        isBound = true;
    }
    //解绑服务
    private void onUnbindServiceClick() {
        if (!isBound) {
            return;
        }
        try {
            unbindService(serviceConnection);//注意：ServiceConnection要传绑定时的ServiceConnection对象，否则会报错。
        } catch (Exception e) {
            e.printStackTrace();
        }
        isBound = false;
    }
    private void init(){
        HashMap<String,String> settings = settingManager.getLocalSetting();
//        Log.e("s",settings.get("URL").length()>0?settings.get("URL"):"");
        setting_url.setText(settings.get("URL"));
        setting_token.setText(settings.get("TOKEN"));
        setting_device.setText(settings.get("DEVICE"));
    }
    private void save(){
        HashMap<String,String> settings = new HashMap<>();
        settings.put("URL",setting_url.getText().toString());
        settings.put("TOKEN",setting_token.getText().toString());
        settings.put("DEVICE",setting_device.getText().toString());
        clearFocus();
        settingManager.saveLocalSetting(settings);
        if(isBound){
            try{
                msgNotifService.reConnection();
            }catch(Exception e){

            }
        }
    }
    private void clearFocus(){
        setting_url.clearFocus();
        setting_token.clearFocus();
        setting_device.clearFocus();

    }
}
