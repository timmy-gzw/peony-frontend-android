package com.tftechsz.common.manager;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.tftechsz.common.entity.IntimacyEntity;

@Database(entities = {IntimacyEntity.class}, version = 1)
public abstract class DbDatabase extends RoomDatabase {

    private static final String DB_NAME = "peony";
    private static volatile DbDatabase instance;

    public static synchronized DbDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static DbDatabase create(final Context context) {
        return Room.databaseBuilder(
                context,
                DbDatabase.class,
                DB_NAME)
                .build();
    }

    public abstract IntimacyDao getIntimacyDao();

}
