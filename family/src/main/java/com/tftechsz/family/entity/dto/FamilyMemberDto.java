package com.tftechsz.family.entity.dto;

import android.os.Parcel;
import android.os.Parcelable;

public class FamilyMemberDto implements Parcelable {

    public int id;
    public String age;
    public int sex;
    public int user_id;
    public String offer;   //贡献值
    public String icon;
    public String nickname;
    public int is_real;
    //    public String role_title;
    public int role_id;  //id" => 4, "title" => "长老" ],[ "id" => 32, "title" => "副族长" ],[ "id" => 64, "title" => "族长" ],
    public String active_time;
    private String letters;
    public String sortString;
    public String sortKey;
    public String content;
    public long created_at;
    public int isSelected;

    public String getLetters() {
        return letters;
    }

    public void setLetters(String letters) {
        this.letters = letters;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.age);
        dest.writeInt(this.sex);
        dest.writeInt(this.user_id);
        dest.writeString(this.offer);
        dest.writeString(this.icon);
        dest.writeString(this.nickname);
        dest.writeInt(this.is_real);
        dest.writeInt(this.role_id);
        dest.writeString(this.active_time);
        dest.writeString(this.letters);
        dest.writeString(this.sortString);
        dest.writeString(this.sortKey);
        dest.writeString(this.content);
        dest.writeLong(this.created_at);
        dest.writeInt(this.isSelected);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readInt();
        this.age = source.readString();
        this.sex = source.readInt();
        this.user_id = source.readInt();
        this.offer = source.readString();
        this.icon = source.readString();
        this.nickname = source.readString();
        this.is_real = source.readInt();
        this.role_id = source.readInt();
        this.active_time = source.readString();
        this.letters = source.readString();
        this.sortString = source.readString();
        this.sortKey = source.readString();
        this.content = source.readString();
        this.created_at = source.readLong();
        this.isSelected = source.readInt();
    }

    public FamilyMemberDto() {
    }

    protected FamilyMemberDto(Parcel in) {
        this.id = in.readInt();
        this.age = in.readString();
        this.sex = in.readInt();
        this.user_id = in.readInt();
        this.offer = in.readString();
        this.icon = in.readString();
        this.nickname = in.readString();
        this.is_real = in.readInt();
        this.role_id = in.readInt();
        this.active_time = in.readString();
        this.letters = in.readString();
        this.sortString = in.readString();
        this.sortKey = in.readString();
        this.content = in.readString();
        this.created_at = in.readLong();
        this.isSelected = in.readInt();
    }

    public static final Creator<FamilyMemberDto> CREATOR = new Creator<FamilyMemberDto>() {
        @Override
        public FamilyMemberDto createFromParcel(Parcel source) {
            return new FamilyMemberDto(source);
        }

        @Override
        public FamilyMemberDto[] newArray(int size) {
            return new FamilyMemberDto[size];
        }
    };
}
