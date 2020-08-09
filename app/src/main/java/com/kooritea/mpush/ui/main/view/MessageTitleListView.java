package com.kooritea.mpush.ui.main.view;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kooritea.mpush.MainActivity;
import com.kooritea.mpush.R;
import com.kooritea.mpush.activity.ContentListActivity;
import com.kooritea.mpush.model.TitleItem;
import com.kooritea.mpush.ui.main.model.MessageListModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class MessageTitleListView {

    private View view;
    private TitleListAdapter titleListAdapter;
    private Context context;
    private ListView listView;
    private MessageListModel messageListModel;

    public MessageTitleListView(@NonNull LayoutInflater inflater, ViewGroup container, final Context context){
        view = inflater.inflate(R.layout.message_list, container, false);
        this.context = context;
        listView = view.findViewById(R.id.listView);
        this.messageListModel =  MessageListModel.getInstance(context);
        this.titleListAdapter = new TitleListAdapter(
                context,
                this.messageListModel.getTitleList()
        );
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener( new MyMultiChoiceModeListener());
        listView.setAdapter(this.titleListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Intent intent = new Intent(context, ContentListActivity.class);
                intent.putExtra("title",titleListAdapter.getItem(position).title);
                context.startActivity(intent);
            }
        });
        this.messageListModel.ebus.on("data-change", new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        titleListAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    public View getView() {
        return view;
    }
    private class MyMultiChoiceModeListener implements ListView.MultiChoiceModeListener{
        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            final int checkedCount = listView.getCheckedItemCount();
            mode.setTitle("已选中" + checkedCount + "个");
            titleListAdapter.notifyDataSetChanged();
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu, menu);
            titleListAdapter.setIsActionMode(true);
            titleListAdapter.notifyDataSetChanged();
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
                        ArrayList<String> needDel = new ArrayList<>();
                        for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                            if (checked.get(i)) {
                                // Do something
                                needDel.add(titleListAdapter.getItem(i).title);
                            }
                        }
                        for(String title : needDel){
                            messageListModel.delMessageByTitle(title);
                        }
                        mode.finish();
                        titleListAdapter.notifyDataSetChanged();
                    }
                }).show();

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            titleListAdapter.setIsActionMode(false);
            titleListAdapter.notifyDataSetChanged();
        }
    }

    private class TitleListAdapter extends BaseAdapter {
        private List<TitleItem> msgList;
        private LayoutInflater mInflater;
        private Boolean isActionMode = false;

        TitleListAdapter(Context context, List<TitleItem> data){
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
        public TitleItem getItem(int position) {
            return msgList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TitleItem msg = getItem(position);
            View view = mInflater.inflate(R.layout.message_title_list_item,null);
            CheckedTextView titleTextView = view.findViewById(R.id.title);
            if(msg.title.equals("")){
                titleTextView.setText("无标题");
            }else{
                titleTextView.setText(msg.title);
            }
            titleTextView.setChecked(((ListView) parent).isItemChecked(position));
            if (!isActionMode) {
                titleTextView.setCheckMarkDrawable(null);
            }
            if(msg.hasNew){
                titleTextView.setTextColor(0xff000000);
            }else{
                titleTextView.setTextColor(0xffaaaaaa);
            }
            return view;
        }
    }
}
