package com.kooritea.mpush.ui.main.model;

import android.content.Context;

import androidx.arch.core.util.Function;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class SendMessageModel extends ViewModel {

    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    private LiveData<String> mText = Transformations.map(mIndex, new Function<Integer, String>() {
        @Override
        public String apply(Integer input) {
            return "this is SendMessageModel";
        }
    });


    public LiveData<String> getText() {
        return mText;
    }
}