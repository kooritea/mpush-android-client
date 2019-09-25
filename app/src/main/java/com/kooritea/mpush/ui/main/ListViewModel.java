package com.kooritea.mpush.ui.main;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kooritea.mpush.DetailActivity;
import com.kooritea.mpush.MainActivity;
import com.kooritea.mpush.R;
import com.kooritea.mpush.model.Message;
import com.kooritea.mpush.module.LocalMsgManager;

import java.util.List;


public class ListViewModel {

    private List<Message> messageList;
    private MainActivity context;
    private View view;
    private MsgListAdapter listViewAdapter;
    private LocalMsgManager localMsgManager;
    private ActivityUpdateReceiver activityUpdateReceiver;



    public static View create(View view, final MainActivity context){
        new ListViewModel(view, context);
        return view;
    }

    private ListViewModel(View view, final MainActivity context){
        this.context = context;
        this.view = view;
        this.localMsgManager = LocalMsgManager.create(context);
        ListView listView = view.findViewById(R.id.listView);
        messageList = localMsgManager.readLocalMsgList();

        listViewAdapter = new MsgListAdapter(
                context,
                messageList
        );
        listView.setAdapter(listViewAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("message",listViewAdapter.getItem(position).toString());
                intent.putExtra("isTop","false");
                context.startActivity(intent);
            }
        });
        listView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                        Message message = listViewAdapter.getItem(info.position);
                        int status = message.getViewType();
                        if(status == 1 || status == 3){
                            menu.add(0, 0, 0, "复制标题").setOnMenuItemClickListener(mOnMenuItemClickListener);
                        }
                        if(status == 2 || status == 3){
                            menu.add(0, 1, 0, "复制内容").setOnMenuItemClickListener(mOnMenuItemClickListener);
                        }
                        menu.add(0, 2, 0, "删除").setOnMenuItemClickListener(mOnMenuItemClickListener);
                    }
                });
        activityUpdateReceiver = new ActivityUpdateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.kooritea.mpush.MESSAGE");
        context.registerReceiver(activityUpdateReceiver, filter);
        context.addReceiver(activityUpdateReceiver);
    }

    public class ActivityUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context RecContext, Intent intent) {
            if (intent.getAction().equals("com.kooritea.mpush.MESSAGE")) {
                listViewAdapter.notifyDataSetChanged();
                context.cachedThreadPool.execute(new Runnable() {
                    public void run() {
                        try{
                            Thread.sleep(6000);
                            context.socketManagerService.cancelNotif();
                        }catch (Exception e ){

                        }
                    }
                });
            }
        }
    }

    private MenuItem.OnMenuItemClickListener mOnMenuItemClickListener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
            Message message = listViewAdapter.getItem(info.position);
            switch (item.getItemId()) {
                case 0:
                    clipString(message.getData().getText());
                    showSnackbar("已复制标题");
                    break;
                case 1:
                    clipString(message.getData().getDesp());
                    showSnackbar("已复制内容");
                    break;
                case 2:
                    localMsgManager.deleteMsg(message.getMid());
                    listViewAdapter.notifyDataSetChanged();
                    showSnackbar("已删除");
                    break;
                default:
                    break;
            }
            return false;
        }
    };

    private void clipString(String data){
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData;
        mClipData = ClipData.newPlainText("Label", data);
        if(mClipData != null){
            cm.setPrimaryClip(mClipData);
        }
    }

    private class MsgListAdapter extends BaseAdapter {
        private List<Message> msgList;
        private LayoutInflater mInflater;

        MsgListAdapter(Context context, List<Message> data){
            mInflater = LayoutInflater.from(context);
            msgList = data;
        }

        @Override
        public int getCount() {
            return msgList.size();
        }

        @Override
        public Message getItem(int position) {
            return msgList.get(msgList.size() -1 -position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Message msg = getItem(position);
            View view = mInflater.inflate(R.layout.list_item,null);
            TextView titleTextView = view.findViewById(R.id.title);
            TextView contentTextView = view.findViewById(R.id.content);
            titleTextView.setText(msg.getData().getText());
            contentTextView.setText(msg.getData().getDesp());
            switch(msg.getViewType()){
                case 1:
                    contentTextView.setVisibility(View.GONE);
                    break;
                case 2:
                    titleTextView.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
            TextView timeTextView = view.findViewById(R.id.time);
            timeTextView.setText(msg.getTime());
            return view;
        }
    }

    private void showSnackbar(String text){
        Snackbar.make(view, text, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

}