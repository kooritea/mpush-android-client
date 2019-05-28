package com.kooritea.mpush;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.kooritea.mpush.MESSAGE";
    private List<Message> messageList = new ArrayList<Message>();
    private ListView listView;
    private MsgListAdapter listViewAdapter;

    private Intent MsgNotificationService;
    private MsgNotifService msgNotifService;

    private boolean isBound = false; //是否绑定了服务

    private LocalMsgManager localMsgManager;

    private ExecutorService cachedThreadPool;

    private ActivityUpdateReceiver activityUpdateReceiver;

    private boolean active;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        // 服务连接成功回调
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
//            Log.e("test","connection");
            msgNotifService = MsgNotifService.Stub.asInterface(service);
//            msgNotifService.setMsgListener(new OnMsgListener() {
//                @Override
//                public void onMessage(Message msg) {
//                    pushMsg(msg);
//                }
//            });
            try{
                msgNotifService.cancelNotif();
            }catch(Exception e){
                Log.e("ServiceConnection","调用服务方法失败");
            }

        }
        // 服务失去连接回调
        @Override
        public void onServiceDisconnected(ComponentName name) {
            toast("与消息服务失去连接");
        }
    };

    public class MainActivityListener extends ActivityListener.Stub
    {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
            // Does nothing
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("main","test");
        active = true;
        super.onCreate(savedInstanceState);
        localMsgManager = new LocalMsgManager(this);
        setContentView(R.layout.activity_main);
        MsgNotificationService = new Intent(this, MsgNotificationService.class);
        startService(MsgNotificationService);
        getListData();
        showList();
        onBindServiceClick();

        cachedThreadPool = Executors.newCachedThreadPool();

        //注册接收广播收到通知时即时刷新列表
        activityUpdateReceiver = new ActivityUpdateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.kooritea.mpush.MESSAGE");
        registerReceiver(activityUpdateReceiver, filter);

//        AlarmManager aManager=(AlarmManager)getSystemService(Service.ALARM_SERVICE);
//        Intent intent = new Intent(this,
//                CheckLiveReceiver.class);
//        intent.setAction("com.kooritea.mpush.LIVE");
//        PendingIntent sender = PendingIntent.getBroadcast(
//                this, 0, intent, 0);
//        // And cancel the alarm.
//        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
//        am.cancel(sender);


//        NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//        nm.cancelAll();
//        startService(new Intent(this, MessagePullService.class));//接收通知服务
    }
    public class ActivityUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.kooritea.mpush.MESSAGE"))
            {
                getListData();
                showList();
                if(active){
                    cachedThreadPool.execute(new Runnable() {
                        public void run() {
                            try{
                                Thread.sleep(6000);
                            }catch (Exception e ){

                            }
//                            Looper.prepare();
                            try{
                                msgNotifService.cancelNotif();
                            }catch (Exception e ){

                            }
//                            Looper.loop();
                        }
                    });
                }
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //当Activity被销毁时解绑服务，因为如果已经绑定服务不显式解绑会报异常。
        unregisterReceiver(activityUpdateReceiver);
        onUnbindServiceClick();
    }
    //绑定服务
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
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                finish();
//                break;
                return true;//拦截事件
            default:
                break;
        }

        return super.onKeyDown(keyCode, event);

    }
    private void pushMsg(Message msg){

    }
    private void getListData(){
        messageList = localMsgManager.readLocalMsgList();
        Collections.reverse(messageList);

//        List<HashMap<String,String>> data = new ArrayList<>();
//        for(int i=0;i<messageList.size();i++){
//            HashMap<String,String> map = new HashMap<>();
//            map.put("title",messageList.get(i).getTitle());
//            map.put("content",messageList.get(i).getContent());
//            map.put("time",messageList.get(i).getTime());
//            data.add(map);
//        }
//        Collections.reverse(data);
//        return data;
    }
    private void showList() {
        listView = findViewById(R.id.mainListView);
//        listViewAdapter = new SimpleAdapter(
//                MainActivity.this,
//                ListViewdata,
//                R.layout.list_item,
//                new String []{"title","content","time"},
//                new int []{R.id.title,R.id.content,R.id.time}
//        );
        listViewAdapter = new MsgListAdapter(
                MainActivity.this,
                messageList
        );
        listView.setAdapter(listViewAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                int index = position;
                intent.putExtra("title",messageList.get(index).getTitle());
                intent.putExtra("content",messageList.get(index).getContent());
                intent.putExtra("time",messageList.get(index).getTime());
                startActivity(intent);
            }
        });
        listView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        listView
            .setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                    int index = info.position;
                    Message message = messageList.get(index);
                    if(message.hasTitle()){
                        menu.add(0, 0, 0, "复制标题").setOnMenuItemClickListener(mOnMenuItemClickListener);
                    }
                    if(message.hasContent()){
                        menu.add(0, 1, 0, "复制内容").setOnMenuItemClickListener(mOnMenuItemClickListener);
                    }
                    menu.add(0, 2, 0, "删除").setOnMenuItemClickListener(mOnMenuItemClickListener);
                }
            });
    }
    private void clipString(String data){
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData;
        mClipData = ClipData.newPlainText("Label", data);
        if(mClipData != null){
            cm.setPrimaryClip(mClipData);
        }
    }
//    private void pushMsg(String title, String content, String time){
//        try{
//            msgNotifService.pushMsg(title,content,time);
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public void onStart(){
        super.onStart();
        active = true;
    }
    @Override
    public void onStop(){
        super.onStop();
        active = false;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings) {
            Intent settingActivity = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(settingActivity);
            return true;
        }
        if (id == R.id.clear) {
            clear();
            return true;
        }
        if (id == R.id.exit) {
            //退出服务
            Intent intent2 = new Intent(this, MsgNotificationService.class);
            stopService(intent2);
            try{
                msgNotifService.exit(0);
            }catch(Exception e){
                toast("退出消息服务失败\n请从任务管理器中结束进程");
            }

            //取消心跳检查
            AlarmManager aManager=(AlarmManager)getSystemService(Service.ALARM_SERVICE);
            Intent intent = new Intent(this, CheckLiveReceiver.class);
            intent.setAction("com.kooritea.mpush.LIVE");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);
            aManager.cancel(pendingIntent);

            System.exit(0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void clear(){
//        Toast.makeText(MainActivity.this, "准备清理", Toast.LENGTH_SHORT).show();
        new AlertDialog.Builder(this).setTitle("确认清除所有消息吗？")
            .setIcon(R.mipmap.ic_launcher)
            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 点击“确认”后的操作
//                    SettingManager settingManager = new SettingManager(MainActivity.this);
//                    settingManager.writeFileData("setting","");
                    localMsgManager.clearData();
                    messageList = new ArrayList<>();
                    showList();
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
    private MenuItem.OnMenuItemClickListener mOnMenuItemClickListener = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            ContextMenu.ContextMenuInfo menuInfo = (ContextMenu.ContextMenuInfo) item.getMenuInfo();
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
            int index = info.position;
            Message message = messageList.get(index);
            switch (item.getItemId()) {
                case 0:
                    clipString(message.getTitle());
                    toast("已复制标题");
                    break;
                case 1:
                    clipString(message.getContent());
                    toast("已复制内容");
                    break;
                case 2:
                    localMsgManager.deleteMsg(message.getMid());
                    messageList.remove(info.position);
                    listViewAdapter.notifyDataSetChanged();
                    toast("已删除");
                    break;
                default:
                    break;
            }
            return false;
        }
    };
    private class MsgListAdapter extends BaseAdapter {
        private List<Message> msgList = null;
        private View list_item_1 = null;//只有标题
        private View list_item_2 = null;//只有内容
        private View list_item_3 = null;//都有
        private LayoutInflater mInflater;

        MsgListAdapter(Context context, List data){
            mInflater = LayoutInflater.from(context);
            msgList = data;
        }

        @Override
        public int getCount() {
            return msgList.size();
        }

        @Override
        public Object getItem(int position) {
            return msgList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Message msg = msgList.get(position);
            boolean hasTitle = msg.hasTitle();
            boolean hasContent = msg.hasContent();

            View view;
//            if(convertView == null){
//                view = mInflater.inflate(R.layout.list_item_1,null);
//            }else{
//                view = convertView;
//            }
            TextView titleTextView;
            TextView contentTextView;
            switch(msg.getStatus()){
                case 1:
                    view = mInflater.inflate(R.layout.list_item_1,null);
                    titleTextView = view.findViewById(R.id.title);
                    titleTextView.setText(msg.getTitle());
                    break;
                case 2:
                    view = mInflater.inflate(R.layout.list_item_2,null);
                    contentTextView = view.findViewById(R.id.content);
                    contentTextView.setText(msg.getContent());
                    break;
                default:
                    view = mInflater.inflate(R.layout.list_item_3,null);
                    titleTextView = view.findViewById(R.id.title);
                    titleTextView.setText(msg.getTitle());
                    contentTextView = view.findViewById(R.id.content);
                    contentTextView.setText(msg.getContent());
                    break;

            }
            TextView timeTextView = view.findViewById(R.id.time);
            timeTextView.setText(msg.getTime());
            return view;
        }
    }

    public void reConnection(){
        try{
            msgNotifService.reConnection();
        }catch(Exception e){
            toast("重启消息服务失败");
        }

    }

    private void toast(String text){
        Toast toast = Toast.makeText(this, null, Toast.LENGTH_LONG);
        toast.setText(text);
        toast.show();
    }
}
