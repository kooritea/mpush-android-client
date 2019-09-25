package com.kooritea.mpush.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.kooritea.mpush.MainActivity;
import com.kooritea.mpush.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private static MainActivity mContext;


    public static PlaceholderFragment newInstance(int index, MainActivity context) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        mContext = context;
        return fragment;
    }


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root;
        int index = getArguments().getInt(ARG_SECTION_NUMBER);
        if(index == 1){
            root = ListViewModel.create(inflater.inflate(R.layout.list_main, container, false), mContext);
        }else{
            root = inflater.inflate(R.layout.fragment_main, container, false);
        }
        return root;
    }
}