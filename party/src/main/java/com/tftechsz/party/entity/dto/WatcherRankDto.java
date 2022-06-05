package com.tftechsz.party.entity.dto;

import android.os.Parcel;
import android.os.Parcelable;

public class WatcherRankDto  implements Parcelable {
    private String value;
    private String icon;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.value);
        dest.writeString(this.icon);
    }

    public void readFromParcel(Parcel source) {
        this.value = source.readString();
        this.icon = source.readString();
    }

    public WatcherRankDto() {
    }

    protected WatcherRankDto(Parcel in) {
        this.value = in.readString();
        this.icon = in.readString();
    }

    public static final Creator<WatcherRankDto> CREATOR = new Creator<WatcherRankDto>() {
        @Override
        public WatcherRankDto createFromParcel(Parcel source) {
            return new WatcherRankDto(source);
        }

        @Override
        public WatcherRankDto[] newArray(int size) {
            return new WatcherRankDto[size];
        }
    };
}
