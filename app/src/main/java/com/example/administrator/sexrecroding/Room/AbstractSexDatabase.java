package com.example.administrator.sexrecroding.Room;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.databinding.ObservableBoolean;
import android.support.annotation.NonNull;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * @author uncleWei
 * @date 2018/5/30 0030
 */

@Database(entities = {SexDTO.class}, version = 2, exportSchema = false)
public abstract class AbstractSexDatabase extends RoomDatabase {
    public abstract SexDao sexDao();

    private static AbstractSexDatabase db;
    private static String DATABASE_NAME = "sexRecord.db";
    private ObservableBoolean mIsDatabaseCreated = new ObservableBoolean(false);

    public static AbstractSexDatabase getInstance(Context context) {
        if (db == null) {
            synchronized (AbstractSexDatabase.class) {
                if (db == null) {
                    db = buildDatabase(context);
                    db.updateDatabaseCreated(context);
                }
            }
        }
        return db;
    }

    private static AbstractSexDatabase buildDatabase(Context appContext) {
        return Room.databaseBuilder(appContext, AbstractSexDatabase.class, DATABASE_NAME)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db1) {
                        super.onCreate(db1);
                        AbstractSexDatabase database = AbstractSexDatabase.getInstance(appContext);
                        database.setDatabaseCreated();
                    }

                    @Override
                    public void onOpen(@NonNull SupportSQLiteDatabase db1) {
                        super.onOpen(db1);
                        Log.e(TAG, "onOpen: " + db1.getPath());
                    }
                }).build();
    }

    /**
     * Check whether the database already exists and expose it via
     */
    private void updateDatabaseCreated(final Context context) {
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            setDatabaseCreated();
        }
    }

    private void setDatabaseCreated() {
        mIsDatabaseCreated.set(true);
    }

    public ObservableBoolean getDatabaseCreated() {
        return mIsDatabaseCreated;
    }
}

