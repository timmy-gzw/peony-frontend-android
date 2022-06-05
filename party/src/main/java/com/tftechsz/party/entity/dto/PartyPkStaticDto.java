package com.tftechsz.party.entity.dto;

import android.os.Parcel;
import android.os.Parcelable;

public class PartyPkStaticDto implements Parcelable {
    public String win;
    public String draw;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.win);
        dest.writeString(this.draw);
    }

    public void readFromParcel(Parcel source) {
        this.win = source.readString();
        this.draw = source.readString();
    }

    public PartyPkStaticDto() {
    }

    protected PartyPkStaticDto(Parcel in) {
        this.win = in.readString();
        this.draw = in.readString();
    }

    public static final Creator<PartyPkStaticDto> CREATOR = new Creator<PartyPkStaticDto>() {
        @Override
        public PartyPkStaticDto createFromParcel(Parcel source) {
            return new PartyPkStaticDto(source);
        }

        @Override
        public PartyPkStaticDto[] newArray(int size) {
            return new PartyPkStaticDto[size];
        }
    };
}
