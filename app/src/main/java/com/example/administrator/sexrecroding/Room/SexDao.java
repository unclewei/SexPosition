package com.example.administrator.sexrecroding.Room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * @author uncleWei
 * @date 2018/5/30 0030
 */
@Dao
public interface SexDao {
    @Query("SELECT * FROM sexdto")
    List<SexDTO> getAll();

    @Insert
    void insertOne(SexDTO... sexDTOS);

    @Delete
    void deleteOne(SexDTO sexDTO);
}
