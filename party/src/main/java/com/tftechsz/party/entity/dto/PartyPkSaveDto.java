package com.tftechsz.party.entity.dto;

import android.os.Parcel;
import android.os.Parcelable;

public class PartyPkSaveDto implements Parcelable {
    public int pk_info_id;
    public int status;  // '2进行中 0已结束 ,1准备阶段，3进入pk结束阶段4惩罚开始 ',
    public int punish_time;
    public int duration;
    public int minute;
    public int count_down;  //倒计时


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.pk_info_id);
        dest.writeInt(this.status);
        dest.writeInt(this.punish_time);
        dest.writeInt(this.duration);
        dest.writeInt(this.minute);
        dest.writeInt(this.count_down);
    }

    public void readFromParcel(Parcel source) {
        this.pk_info_id = source.readInt();
        this.status = source.readInt();
        this.punish_time = source.readInt();
        this.duration = source.readInt();
        this.minute = source.readInt();
        this.count_down = source.readInt();
    }

    public PartyPkSaveDto() {
    }

    protected PartyPkSaveDto(Parcel in) {
        this.pk_info_id = in.readInt();
        this.status = in.readInt();
        this.punish_time = in.readInt();
        this.duration = in.readInt();
        this.minute = in.readInt();
        this.count_down = in.readInt();
    }

    public static final Creator<PartyPkSaveDto> CREATOR = new Creator<PartyPkSaveDto>() {
        @Override
        public PartyPkSaveDto createFromParcel(Parcel source) {
            return new PartyPkSaveDto(source);
        }

        @Override
        public PartyPkSaveDto[] newArray(int size) {
            return new PartyPkSaveDto[size];
        }
    };
}
