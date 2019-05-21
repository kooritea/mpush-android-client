// MsgNotifService.aidl
package com.kooritea.mpush;

// Declare any non-default types here with import statements
//import com.kooritea.mpush.MainActivity.MainActivityListener;

interface MsgNotifService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void pushMsg(String title, String content, String time);
    void cancelNotif();
    void reConnection();
//    boolean pullMsg();
    void exit(int status);

    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
}
