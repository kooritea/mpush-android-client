package com.kooritea.mpush.database;

import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RoomDatabase;

import com.kooritea.mpush.database.entity.MessageEntity;

import java.util.List;


@Database(entities = {MessageEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    @Dao
    public interface MessageDao {
        @Query("SELECT * FROM message ORDER BY id ASC")
        List<MessageEntity> getAll();

        @Query("SELECT * FROM message WHERE id IN (:userIds)")
        List<MessageEntity> loadAllByIds(int[] userIds);

        @Query("SELECT * FROM message WHERE mid LIKE :mid LIMIT 1")
        MessageEntity findByMid(String mid);

        @Insert
        void insertAll(MessageEntity... message);

        @Delete
        void delete(MessageEntity message);
    }
    public abstract MessageDao messageDao();
}