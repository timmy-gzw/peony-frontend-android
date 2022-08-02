package com.tftechsz.mine.entity.dto;

import android.os.Parcel;
import android.os.Parcelable;

public class GiftDto implements Parcelable {

    public String title;   //描述
    public String image;  //图
    public String coin;  //金币数
    public String name;  //name
    public int number;  //数量


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.image);
        dest.writeString(this.coin);
        dest.writeString(this.name);
        dest.writeInt(this.number);
    }

    public void readFromParcel(Parcel source) {
        this.title = source.readString();
        this.image = source.readString();
        this.coin = source.readString();
        this.name = source.readString();
        this.number = source.readInt();
    }

    public GiftDto() {
    }

    protected GiftDto(Parcel in) {
        this.title = in.readString();
        this.image = in.readString();
        this.coin = in.readString();
        this.name = in.readString();
        this.number = in.readInt();
    }

    public static final Parcelable.Creator<GiftDto> CREATOR = new Parcelable.Creator<GiftDto>() {
        @Override
        public GiftDto createFromParcel(Parcel source) {
            return new GiftDto(source);
        }

        @Override
        public GiftDto[] newArray(int size) {
            return new GiftDto[size];
        }
    };
}
