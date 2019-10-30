package com.kooritea.mpush;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.kooritea.mpush.model.Message;

import org.json.JSONException;

public class DetailActivity extends AppCompatActivity {

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
            message = new Message(intent.getStringExtra("message"));
            String title = message.getData().getText();
            String content = message.getData().getDesp();
            String time = message.getTime();
            boolean hasTitle = title != null && title.length()>0;
            boolean hasContent = content != null && content.length()>0;

            setContentView(R.layout.activity_detail);

            TextView titleTextView = findViewById(R.id.title);
            titleTextView.setText(title);
            titleTextView.setTextIsSelectable(true);

            TextView contentTextView = findViewById(R.id.content);
            contentTextView.setText(content);
            contentTextView.setTextIsSelectable(true);

            if(hasTitle && !hasContent){
                contentTextView.setVisibility(View.GONE);
            }else if(!hasTitle && hasContent){
                titleTextView.setVisibility(View.GONE);
            }
            TextView timeTextView = findViewById(R.id.time);
            timeTextView.setText(time);
            timeTextView.setTextIsSelectable(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(message.getData().getExtra().getScheme() != null){
            menu.add(0,0,0,"打开链接").setOnMenuItemClickListener(mOnMenuItemClickListener);
            return true;
        }
        else{
            return false;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(isTop){
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private MenuItem.OnMenuItemClickListener mOnMenuItemClickListener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case 0:
                    startActivity(new Intent(Intent.ACTION_VIEW, message.getData().getExtra().getScheme()));
                    break;
                default:
                    break;
            }
            return false;
        }
    };
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if(isTop){
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
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
        System.gc();
    }
}
