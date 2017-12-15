package com.example.dell.testapp_2;

import android.app.Application;

/**
 * Created by dell on 2017/12/12.
 */

public class MyApplication extends Application{
    private int RidingTime;
    private int RidingCount;
    private int RidingLength;
    private int Level;

    @Override
    public void onCreate(){
        super.onCreate();
        RidingTime=0;
        RidingCount=0;
        RidingLength=0;
        Level=1;
    }

    public void addRidingTime(int time){
        this.RidingTime+=time;
    }
    public void addRidingCount(){
        this.RidingCount++;
    }
    public void addRidingLength(int length){
        this.RidingLength+=length;
    }
    public void LevelUp(){
        this.Level++;
    }
    public int getRidingTime(){
        return this.RidingTime;
    }
    public int getRidingCount(){
        return this.RidingCount;
    }
    public int getRidingLength(){
        return this.RidingLength;
    }
    public int getLevel(){
        return this.Level;
    }
}
