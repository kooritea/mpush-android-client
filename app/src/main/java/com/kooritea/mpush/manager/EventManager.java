package com.kooritea.mpush.manager;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class EventManager {

    private class EXObservable<T> extends Observable {
        public void sendChangeMsg(T content) {
            setChanged();
            notifyObservers(content);
        }
    }

    private HashMap<String, EXObservable> observableMap;
    public EventManager(){
        this.observableMap = new HashMap<>();
    }

    public <K> void emit(String event,Object arg){
        if(this.observableMap.get(event) == null){
            this.observableMap.put(event,new EXObservable<K>());
        }
        this.observableMap.get(event).sendChangeMsg(arg);
    }

    public <K> void on(String event, Observer observer){
        if(this.observableMap.get(event) == null){
            this.observableMap.put(event,new EXObservable<K>());
        }
        this.observableMap.get(event).addObserver(observer);
    }
}
