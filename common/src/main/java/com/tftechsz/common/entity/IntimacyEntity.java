package com.tftechsz.common.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "intimacy")
public class IntimacyEntity {
    private long id;
    @ColumnInfo(name = "self_id")
    private String selfId;  //自己的用户Id
    @ColumnInfo(name = "user_id")
    @NonNull
    @PrimaryKey
    private String userId;  //用户ID
    @ColumnInfo(name = "is_show")
    private int isShow;  //是否显示过了亲密度   0否 1是
    @ColumnInfo(name = "end_time")
    private int endTime;  //跑马灯结束时间

    public IntimacyEntity(@NotNull String userId, String selfId, int isShow,int endTime) {
        this.userId = userId;
        this.selfId = selfId;
        this.isShow = isShow;
        this.endTime = endTime;
    }

    @Ignore
    public IntimacyEntity() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSelfId() {
        return selfId;
    }

    public void setSelfId(String selfId) {
        this.selfId = selfId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getIsShow() {
        return isShow;
    }

    public void setIsShow(int isShow) {
        this.isShow = isShow;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }
}
