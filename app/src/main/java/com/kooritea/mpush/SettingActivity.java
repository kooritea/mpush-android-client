package com.kooritea.mpush;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.kooritea.mpush.module.LocalMsgManager;
import com.kooritea.mpush.module.SettingManager;
import com.kooritea.mpush.receiver.KeepLiveReceiver;
import com.kooritea.mpush.service.SocketManagerService;

import java.util.HashMap;

public class SettingActivity extends AppCompatActivity {

    private SettingManager settingManager;
    private HashMap<String,String> settings;

    private EditText setting_url;
    private EditText setting_token;
    private EditText setting_name;
    private EditText setting_group;

    public SocketManagerAidl socketManagerService;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        // 服务连接成功回调
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            socketManagerService = SocketManagerAidl.Stub.asInterface(service);
        }
        // 服务失去连接回调
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        settingManager = SettingManager.create(this);
        setting_url = findViewById (R.id.setting_url);
        setting_token = findViewById (R.id.setting_token);
        setting_name = findViewById (R.id.setting_name);
        setting_group = findViewById (R.id.setting_group);
        load();
        bindService(new Intent(this, SocketManagerService.class), serviceConnection, BIND_AUTO_CREATE);
    }

    private void load(){
        setting_url.setText(settingManager.get("URL"));
        setting_token.setText(settingManager.get("TOKEN"));
        setting_name.setText(settingManager.get("NAME"));
        setting_group.setText(settingManager.get("GROUP"));
        clearFocus();
    }

    public void reset(View view){
        load();
        clearFocus();
    }

    public void clear(View view){
        new AlertDialog.Builder(this).setTitle("确认清除所有消息吗？")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LocalMsgManager.create(SettingActivity.this).clearData();
                        toast("清除完成");
                    }
                }).setNegativeButton("返回", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 点击“返回”后的操作,这里不设置没有任何操作
                toast("取消");
            }
        }).show();
    }

    public void save(View view){
        settingManager.set("URL",setting_url.getText().toString());
        settingManager.set("TOKEN",setting_token.getText().toString());
        settingManager.set("NAME",setting_name.getText().toString());
        settingManager.set("GROUP",setting_group.getText().toString());
        settingManager.saveLocalSetting();
        clearFocus();
        try {
            socketManagerService.reConnection();
        } catch (RemoteException e) {
            toast("重启服务失败");
        }
    }

    public void exit(View view){
        Intent intent = new Intent(this, SocketManagerService.class);
        stopService(intent);
        try{
            socketManagerService.exit(0);
        }catch(Exception e){

        }

        //取消心跳检查
        AlarmManager aManager=(AlarmManager)getSystemService(Service.ALARM_SERVICE);
        Intent intent2 = new Intent("com.kooritea.mpush.LIVE");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent2,0);
        aManager.cancel(pendingIntent);
        System.exit(0);
    }

    private void clearFocus(){
        setting_url.clearFocus();
        setting_token.clearFocus();
        setting_name.clearFocus();
        setting_group.clearFocus();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        System.gc();
    }

    public void toast(String text){
        Toast toast = Toast.makeText(this, null, Toast.LENGTH_LONG);
        toast.setText(text);
        toast.show();
    }
}
