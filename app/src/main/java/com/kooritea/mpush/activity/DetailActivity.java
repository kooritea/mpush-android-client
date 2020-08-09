package com.kooritea.mpush.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.kooritea.mpush.R;
import com.kooritea.mpush.model.Message;

import org.json.JSONException;

import java.net.URI;

public class DetailActivity extends AppCompatActivity {

    private String title;
    private String content;
    private String time;
    private Message message;
    private boolean isTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 添加返回键
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        try {
            isTop = intent.getStringExtra("isTop") == null;
            Message message = new Message(intent.getStringExtra("message"));
            this.message = message;
            title = message.getData().getText();
            content = message.getData().getDesp();
            time = message.getTime();
            boolean hasTitle = title != null && title.length()>0;
            boolean hasContent = content != null && content.length()>0;

            setContentView(R.layout.activity_detail);

            TextView titleTextView = findViewById(R.id.title);
            if(title.equals("")){
                titleTextView.setText("无标题");
            }else{
                titleTextView.setText(title);
            }
            titleTextView.setTextIsSelectable(true);

            TextView contentTextView = findViewById(R.id.content);
            if(content.equals("")){
                contentTextView.setText("无正文");
            }else{
                contentTextView.setText(content);
            }
            contentTextView.setTextIsSelectable(true);

            TextView timeTextView = findViewById(R.id.time);
            timeTextView.setText(time);
            timeTextView.setTextIsSelectable(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try{
            if(message.getData().getExtra().getString("scheme")!= null){
                menu.add(0,0,0,"打开链接").setOnMenuItemClickListener(mOnMenuItemClickListener);
                return true;
            }else{
                return false;
            }
        }catch (JSONException ex){
            return false;
        }
    }
    private MenuItem.OnMenuItemClickListener mOnMenuItemClickListener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case 0:
                    try{
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(message.getData().getExtra().getString("scheme"))));
                    }catch (JSONException ex){
                        return false;
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    };
}