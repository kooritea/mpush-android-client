package com.kooritea.mpush.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kooritea.mpush.MainActivity;
import com.kooritea.mpush.R;
import com.kooritea.mpush.model.Message;
import com.kooritea.mpush.model.TitleItem;
import com.kooritea.mpush.ui.main.model.MessageListModel;
import com.kooritea.mpush.ui.main.view.MessageTitleListView;

import org.json.JSONException;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class ContentListActivity extends AppCompatActivity {

    private MessageListAdapter messageListAdapter;
    private MessageListModel messageListModel;
    private ListView listView;
    private Context context;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        // 添加返回键
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.message_list);
        Intent intent = getIntent();
        final String title = intent.getStringExtra("title");
        setTitle(title);
        listView = findViewById(R.id.listView);
        this.messageListModel =  MessageListModel.getInstance(this);
        this.messageListAdapter = new MessageListAdapter(
                this,
                this.messageListModel.getContentList(title)
        );
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener( new MyMultiChoiceModeListener());
        listView.setAdapter(this.messageListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Intent intent = new Intent(ContentListActivity.this, DetailActivity.class);
                intent.putExtra("message",messageListAdapter.getItem(position).toString());
                ContentListActivity.this.startActivity(intent);
            }
        });
        this.messageListModel.ebus.on("data-change", new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if(((String)arg).equals(title)){
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            messageListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
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

    private class MyMultiChoiceModeListener implements ListView.MultiChoiceModeListener{
        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            final int checkedCount = listView.getCheckedItemCount();
            mode.setTitle("已选中" + checkedCount + "个");
            messageListAdapter.notifyDataSetChanged();
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu, menu);
            messageListAdapter.setIsActionMode(true);
            messageListAdapter.notifyDataSetChanged();
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
            final SparseBooleanArray checked = listView.getCheckedItemPositions();
            new AlertDialog.Builder(context)
                    .setIcon(R.drawable.ic_launcher)//这里是显示提示框的图片信息，我这里使用的默认androidApp的图标
                    .setTitle("删除")
                    .setMessage("确认删除选中的消息？")
                    .setNegativeButton("取消",null)
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                                if (checked.get(i)) {
                                    // Do something
                                    messageListModel.delMessage(messageListAdapter.getItem(i));
                                }
                            }
                            mode.finish();
                            if(listView.getAdapter().getCount() == 0){
                                ContentListActivity.this.finish();
                            }else{
                                messageListAdapter.notifyDataSetChanged();
                            }
                        }
                    }).show();

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            messageListAdapter.setIsActionMode(false);
            messageListAdapter.notifyDataSetChanged();
        }
    }

    private class MessageListAdapter extends BaseAdapter {
        private List<Message> msgList;
        private LayoutInflater mInflater;
        private Boolean isActionMode = false;

        MessageListAdapter(Context context, List<Message> data){
            mInflater = LayoutInflater.from(context);
            msgList = data;
        }

        public void setIsActionMode(Boolean isActionMode){
            this.isActionMode = isActionMode;
        }

        @Override
        public int getCount() {
            return msgList.size();
        }

        @Override
        public Message getItem(int position) {
            return msgList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Message msg = getItem(position);
            View view = mInflater.inflate(R.layout.message_content_list_item,null);
            CheckedTextView contentTextView = view.findViewById(R.id.content);
            if(msg.getData().getDesp().equals("")){
                contentTextView.setText("无正文");
            }else{
                contentTextView.setText(msg.getData().getDesp());
            }
            contentTextView.setChecked(((ListView) parent).isItemChecked(position));
            if (!isActionMode) {
                contentTextView.setCheckMarkDrawable(null);
            }
            TextView timeTextView = view.findViewById(R.id.time);
            timeTextView.setText(msg.getTime());
            if(msg.isNew){
                contentTextView.setTextColor(0xff000000);
                timeTextView.setTextColor(0xff000000);
            }else{
                contentTextView.setTextColor(0xffaaaaaa);
                timeTextView.setTextColor(0xffaaaaaa);
            }
            return view;
        }
    }
}
