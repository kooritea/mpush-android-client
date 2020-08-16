package com.kooritea.mpush.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "message")
public class MessageEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "send-type")
    public String sendType;

    @ColumnInfo(name = "target")
    public String target;

    @ColumnInfo(name = "mid")
    public String mid;

    @ColumnInfo(name = "from-method")
    public String fromMethod;

    @ColumnInfo(name = "from-name")
    public String fromName;

    @ColumnInfo(name = "text")
    public String text;

    @ColumnInfo(name = "desp")
    public String desp;

    @ColumnInfo(name = "extra")
    public String extra;
}
