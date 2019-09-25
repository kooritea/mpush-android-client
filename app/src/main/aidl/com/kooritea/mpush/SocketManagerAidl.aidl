// SocketManagerAidl.aidl
package com.kooritea.mpush;

// Declare any non-default types here with import statements

interface SocketManagerAidl {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
    void reConnection();
    void cancelNotif();
    void exit(int status);
}
