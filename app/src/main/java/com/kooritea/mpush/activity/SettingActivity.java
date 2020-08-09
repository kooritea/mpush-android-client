package com.kooritea.mpush.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.kooritea.mpush.MainActivity;
import com.kooritea.mpush.R;
import com.kooritea.mpush.manager.SettingManager;
import com.kooritea.mpush.manager.WebsocketManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;
import java.util.Observer;

public class SettingActivity extends AppCompatActivity {

    private SettingManager settingManager;
    private EditText setting_url;
    private EditText setting_token;
    private EditText setting_name;
    private EditText setting_group;
    private WebsocketManager websocketManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        settingManager = SettingManager.getInstance(this);
        setting_url = findViewById (R.id.setting_url);
        setting_token = findViewById (R.id.setting_token);
        setting_name = findViewById (R.id.setting_name);
        setting_group = findViewById (R.id.setting_group);
        loadSetting();
        clearFocus();
        this.websocketManager = WebsocketManager.getInstance(getApplicationContext());
    }

    public void reset(View view){
        loadSetting();
        clearFocus();
    }

    private void loadSetting(){
        setting_url.setText(settingManager.get("URL"));
        setting_token.setText(settingManager.get("TOKEN"));
        setting_name.setText(settingManager.get("NAME"));
        setting_group.setText(settingManager.get("GROUP"));
    }

    public void saveSetting(View view){
        settingManager.set("URL",setting_url.getText().toString());
        settingManager.set("TOKEN",setting_token.getText().toString());
        settingManager.set("NAME",setting_name.getText().toString());
        settingManager.set("GROUP",setting_group.getText().toString());
        clearFocus();

        websocketManager.tryConnect();

    }

    private void clearFocus(){
        setting_url.clearFocus();
        setting_token.clearFocus();
        setting_name.clearFocus();
        setting_group.clearFocus();
    }

    public void exit(View view){

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
