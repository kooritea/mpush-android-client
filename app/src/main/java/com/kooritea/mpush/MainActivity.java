package com.kooritea.mpush;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.kooritea.mpush.activity.SettingActivity;
import com.kooritea.mpush.manager.LocalMessageManager;
import com.kooritea.mpush.manager.SettingManager;
import com.kooritea.mpush.manager.WebsocketManager;
import com.kooritea.mpush.model.Message;
import com.kooritea.mpush.service.FCMService;
import com.kooritea.mpush.ui.main.SectionsPagerAdapter;
import com.kooritea.mpush.ui.main.model.MessageListModel;
import com.kooritea.mpush.ui.main.view.MessageTitleListView;

import org.json.JSONException;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity {

    private SettingManager settingManager;
    private LocalMessageManager localMessageManager;
    private WebsocketManager websocketManager;
    public  MessageListModel messageListModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settingManager = SettingManager.getInstance(this);
        localMessageManager = LocalMessageManager.getInstance(this);
        messageListModel = MessageListModel.getInstance(this);
        websocketManager = WebsocketManager.getInstance(getApplicationContext());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);


        websocketManager.onMessage(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                Message message = (Message)o;
                localMessageManager.saveLocalMsglist(message);
                message.isNew = true;
                messageListModel.addMessage(message);
            }
        });
        websocketManager.onAuth(new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                toast("Websocket 已连接");
                if(settingManager.get("fcmProjectId") != null && settingManager.get("fcmApplicationId") != null && settingManager.get("fcmApiKey") != null){
                    FirebaseOptions options = new FirebaseOptions.Builder()
                        .setProjectId(settingManager.get("fcmProjectId"))
                        .setApplicationId(settingManager.get("fcmApplicationId"))
                        .setApiKey(settingManager.get("fcmApiKey"))
                        .build();
                    FirebaseApp.initializeApp(MainActivity.this /* Context */, options, "Instance");
                    FirebaseApp firebaseApp = FirebaseApp.getInstance("Instance");
                    FirebaseInstanceId.getInstance(firebaseApp)
                        .getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if(task.isSuccessful()){
                                settingManager.set("FCM-TOKEN",task.getResult().getToken());
                            }else{
                                Log.w("Firebase", "getInstanceId failed", task.getException());
                            }
                            if(settingManager.get("FCM-TOKEN")!=null){
                                try {
                                    websocketManager.send("REGISTER_FCM","{" +
                                            "" + "token:'"+task.getResult().getToken()+"'" +
                                            "}");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            }
                        });
                }
            }
        });
        websocketManager.tryConnect();
    }
    public void SettingButtonClick(View view){
        Intent intent = new Intent(this,SettingActivity.class);
        startActivity(intent);
    }

    private void toast(final String text){
        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.websocketManager.close();
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}