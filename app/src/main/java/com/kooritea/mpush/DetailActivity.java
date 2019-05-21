package com.kooritea.mpush;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private String title;
    private String content;
    private String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        content = intent.getStringExtra("content");
        time = intent.getStringExtra("time");
        boolean hasTitle = title != null && title.length()>0;
        boolean hasContent = content != null && content.length()>0;
        if(hasTitle && !hasContent){
            setContentView(R.layout.activity_detail_1);
            TextView titleTextView = findViewById(R.id.title);
            titleTextView.setText(title);
            titleTextView.setTextIsSelectable(true);
        }else if(!hasTitle && hasContent){
            setContentView(R.layout.activity_detail_2);
            TextView contentTextView = findViewById(R.id.content);
            contentTextView.setText(content);
            contentTextView.setTextIsSelectable(true);
        }else{
            setContentView(R.layout.activity_detail_3);
            TextView titleTextView = findViewById(R.id.title);
            titleTextView.setText(title);
            titleTextView.setTextIsSelectable(true);
            TextView contentTextView = findViewById(R.id.content);
            contentTextView.setText(content);
            contentTextView.setTextIsSelectable(true);
        }
        TextView timeTextView = findViewById(R.id.time);
        timeTextView.setText(time);
        timeTextView.setTextIsSelectable(true);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in Action Bar clicked; go home
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
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;//拦截事件
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);

    }
}
