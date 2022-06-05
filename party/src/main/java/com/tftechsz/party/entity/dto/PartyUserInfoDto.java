package com.tftechsz.party.entity.dto;

import android.os.Parcel;
import android.os.Parcelable;

public class PartyUserInfoDto implements Parcelable {
    private int role_id; //128：超管 64：房主 32：管理员 0：普通用户
    private String nickname;
    private String icon;
    private String birthday;
    private String age;
    private String desc;
    private int user_id;
    private int shutup;  //0：正常 1：禁言
    private String shutup_toast_msg;//禁言提示

    public String getShutup_toast_msg() {
        return shutup_toast_msg;
    }

    public void setShutup_toast_msg(String shutup_toast_msg) {
        this.shutup_toast_msg = shutup_toast_msg;
    }

    public int getRole_id() {
        return role_id;
    }

    public void setRole_id(int role_id) {
        this.role_id = role_id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getShutup() {
        return shutup;
    }

    public void setShutup(int shutup) {
        this.shutup = shutup;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.role_id);
        dest.writeString(this.nickname);
        dest.writeString(this.icon);
        dest.writeString(this.birthday);
        dest.writeString(this.age);
        dest.writeString(this.desc);
        dest.writeInt(this.user_id);
        dest.writeInt(this.shutup);
        dest.writeString(this.shutup_toast_msg);
    }

    public void readFromParcel(Parcel source) {
        this.role_id = source.readInt();
        this.nickname = source.readString();
        this.icon = source.readString();
        this.birthday = source.readString();
        this.age = source.readString();
        this.desc = source.readString();
        this.user_id = source.readInt();
        this.shutup = source.readInt();
        this.shutup_toast_msg = source.readString();
    }

    public PartyUserInfoDto() {
    }

    protected PartyUserInfoDto(Parcel in) {
        this.role_id = in.readInt();
        this.nickname = in.readString();
        this.icon = in.readString();
        this.birthday = in.readString();
        this.age = in.readString();
        this.desc = in.readString();
        this.user_id = in.readInt();
        this.shutup = in.readInt();
        this.shutup_toast_msg = in.readString();
    }

    public static final Creator<PartyUserInfoDto> CREATOR = new Creator<PartyUserInfoDto>() {
        @Override
        public PartyUserInfoDto createFromParcel(Parcel source) {
            return new PartyUserInfoDto(source);
        }

        @Override
        public PartyUserInfoDto[] newArray(int size) {
            return new PartyUserInfoDto[size];
        }
    };
}
