package com.kooritea.mpush;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.kooritea.mpush.service.SocketManagerService;
import com.kooritea.mpush.ui.main.SectionsPagerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    public ExecutorService cachedThreadPool;
    public List<BroadcastReceiver> receivers = new ArrayList<>();
    public SocketManagerAidl socketManagerService;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        // 服务连接成功回调
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            socketManagerService = SocketManagerAidl.Stub.asInterface(service);
            try{
                socketManagerService.cancelNotif();
            }catch(Exception e){
                Log.e("ServiceConnection","调用服务方法失败");
            }

        }
        // 服务失去连接回调
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cachedThreadPool = Executors.newCachedThreadPool();
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(MainActivity.this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        startService(new Intent(this, SocketManagerService.class));
        onBindServiceClick();
    }

    private void onBindServiceClick() {
        bindService(new Intent(this, SocketManagerService.class), serviceConnection, BIND_AUTO_CREATE);
    }

    public void addReceiver(BroadcastReceiver receiver ){
        this.receivers.add(receiver);
    }

    public void SettingButtonClick(View view){
        Intent intent = new Intent(this,SettingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        for(BroadcastReceiver receiver : receivers){
            unregisterReceiver(receiver);
        }
        try {
            unbindService(serviceConnection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }
}