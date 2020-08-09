package com.kooritea.mpush.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import com.kooritea.mpush.manager.LocalMessageManager;
import com.kooritea.mpush.ui.main.model.MessageListModel;
import com.kooritea.mpush.ui.main.model.SendMessageModel;
import com.kooritea.mpush.ui.main.view.MessageTitleListView;
import com.kooritea.mpush.ui.main.view.SendMessageView;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private LocalMessageManager localMessageManager;


    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        View root;
        switch (index){
            case 2:
                SendMessageView sendMessageView = new SendMessageView(inflater,container,ViewModelProviders.of(this).get(SendMessageModel.class));
                root = sendMessageView.getView();
                break;
            default:
                MessageTitleListView messageTitleListView = new MessageTitleListView(inflater,container,getContext());
                root = messageTitleListView.getView();
        }
        return root;
    }
}