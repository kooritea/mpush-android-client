package com.kooritea.mpush.module;

import android.app.Activity;
import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileManager extends Activity {
    private Context context;
    public FileManager(Context context){
        this.context = context;
    }
    public void appendFileData(String filename, String content) {
        try {
            FileOutputStream outStream=context.openFileOutput(filename,MODE_APPEND);
            outStream.write(content.getBytes());
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void writeFileData(String filename, String content) {
        try {
            FileOutputStream outStream=context.openFileOutput(filename,MODE_PRIVATE);
            outStream.write(content.getBytes());
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String readFileData(String fileName){
        String result="";
        try{
            FileInputStream fis = context.openFileInput(fileName);
            int lenght = fis.available();
            byte[] buffer = new byte[lenght];
            fis.read(buffer);
            result = new String(buffer, "UTF-8");
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
            writeFileData(fileName,"");
        }
        return  result;
    }
}