package com.kooritea.mpush.ui.main.model;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.kooritea.mpush.manager.EventManager;
import com.kooritea.mpush.manager.LocalMessageManager;
import com.kooritea.mpush.model.Message;
import com.kooritea.mpush.model.TitleItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageListModel extends ViewModel {

    private static MessageListModel instance;
    public static MessageListModel getInstance(Context context) {
        if(instance == null){
            instance = new MessageListModel(context);
        }
        return instance;
    }


    private List<TitleItem> titleListData = new ArrayList();
    private HashMap<String,List<Message>> contentListMap = new HashMap<>();
    public EventManager ebus = new EventManager();
    private LocalMessageManager localMessageManager;

    private MessageListModel(Context context){
        localMessageManager = LocalMessageManager.getInstance(context);
        this.setData(localMessageManager.readLocalMsgList());
    }

    public void setData(List<Message> data){
        this.titleListData = new ArrayList();
        this.contentListMap = new HashMap<>();
        for(Message message : data){
            this.addMessage(message);
        }
    }


    public void addMessage(Message message){
        String title = message.getData().getText();
        if(this.contentListMap.get(title) == null){
            List<Message> contentList = new ArrayList();
            contentList.add(0,message);
            this.contentListMap.put(title,contentList);
            titleListData.add(0,new TitleItem(title,message.isNew));
        }else{
            List<Message> contentList = this.contentListMap.get(title);
            contentList.add(0,message);
            for(TitleItem item : titleListData){
                if(item.title.equals(title)){
                    titleListData.remove(item);
                    titleListData.add(0,item);
                    if(message.isNew){
                        item.hasNew = true;
                    }
                    break;
                }
            }
        }
        this.ebus.emit("data-change",message.getData().getText());
    }

    public void delMessage(Message message){
        String title = message.getData().getText();
        List<Message> contentList = this.getContentList(title);
        contentList.remove(message);
        this.localMessageManager.deleteMsg(message.getMid(),true);
        if(contentList.size() == 0){
            this.contentListMap.remove(title);
            for(TitleItem titleItem : this.titleListData){
                if(title.equals(titleItem.title)){
                    this.titleListData.remove(titleItem);
                    break;
                }
            }
        }
        this.ebus.emit("data-change",message.getData().getText());

    }

    public void delMessageByTitle(String title){
        List<Message> contentList = this.contentListMap.get(title);
        if(contentList != null){
            for(Message message : contentList){
                this.localMessageManager.deleteMsg(message.getMid(),false);
            }
            this.localMessageManager.saveToFile();
            this.contentListMap.remove(title);
            for(TitleItem titleItem : this.titleListData){
                if(title.equals(titleItem.title)){
                    this.titleListData.remove(titleItem);
                    break;
                }
            }
        }
        this.ebus.emit("data-change",title);
    }


    public List<TitleItem> getTitleList() {
        return this.titleListData;
    }

    public List<Message> getContentList(String title){
        return this.contentListMap.get(title);
    }
}