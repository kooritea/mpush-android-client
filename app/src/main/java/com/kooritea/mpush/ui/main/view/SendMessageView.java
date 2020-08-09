package com.kooritea.mpush.ui.main.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kooritea.mpush.R;
import com.kooritea.mpush.ui.main.model.SendMessageModel;

public class SendMessageView {

    View view;
    SendMessageModel sendMessageModel;

    public SendMessageView(@NonNull LayoutInflater inflater, ViewGroup container, SendMessageModel sendMessageModel){
        view = inflater.inflate(R.layout.send_message, container, false);
        this.sendMessageModel = sendMessageModel;
        final TextView textView = view.findViewById(R.id.section_label);
        textView.setText("开发中");
    }

    public View getView() {
        return view;
    }
}
