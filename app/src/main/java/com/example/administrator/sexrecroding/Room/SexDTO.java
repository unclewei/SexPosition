package com.example.administrator.sexrecroding.Room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Relation;

import com.example.administrator.sexrecroding.Bean.Position;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author uncleWei
 * @date 2018/5/30 0030
 */

@Entity
public class SexDTO {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String date;

    private String sexTime;

    private String position;

    private String positionTime;


    public String getPositionTime() {
        return positionTime;
    }

    public ArrayList<String> getPositionTimeList() {
        Type listType = new TypeToken<ArrayList<String>>() {
        }.getType();
        return new Gson().fromJson(positionTime, listType);
    }

    public void setPositionTime(ArrayList<String> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        this.positionTime = json;
    }

    public ArrayList<Position> getPositionList() {
        Type listType = new TypeToken<ArrayList<Position>>() {
        }.getType();
        return new Gson().fromJson(position, listType);
    }

    public void setPosition(ArrayList<Position> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        position = json;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setPositionTime(String positionTime) {
        this.positionTime = positionTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSexTime() {
        return sexTime;
    }

    public void setSexTime(String sexTime) {
        this.sexTime = sexTime;
    }
}
