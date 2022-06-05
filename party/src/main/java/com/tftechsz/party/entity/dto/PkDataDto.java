package com.tftechsz.party.entity.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.netease.nim.uikit.common.ChatMsg;

public class PkDataDto implements Parcelable {

    private int pk_info_id;
    private int blue_cnt;  //蓝队
    private int red_cnt;   //红队
    private int win_side;
    private int minute;
    private PartyPkStaticDto victory_static;
    private PartyPkSaveDto pk_info;
    private ChatMsg.PkBank pk_bank;

    public int getPk_info_id() {
        return pk_info_id;
    }

    public void setPk_info_id(int pk_info_id) {
        this.pk_info_id = pk_info_id;
    }

    public PartyPkSaveDto getPk_info() {
        return pk_info;
    }

    public void setPk_info(PartyPkSaveDto pk_info) {
        this.pk_info = pk_info;
    }

    public int getBlue_cnt() {
        return blue_cnt;
    }

    public void setBlue_cnt(int blue_cnt) {
        this.blue_cnt = blue_cnt;
    }

    public int getRed_cnt() {
        return red_cnt;
    }

    public void setRed_cnt(int red_cnt) {
        this.red_cnt = red_cnt;
    }

    public int getWin_side() {
        return win_side;
    }

    public void setWin_side(int win_side) {
        this.win_side = win_side;
    }

    public PartyPkStaticDto getVictory_static() {
        return victory_static;
    }

    public void setVictory_static(PartyPkStaticDto victory_static) {
        this.victory_static = victory_static;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public ChatMsg.PkBank getPk_bank() {
        return pk_bank;
    }

    public void setPk_bank(ChatMsg.PkBank pk_bank) {
        this.pk_bank = pk_bank;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.pk_info_id);
        dest.writeInt(this.blue_cnt);
        dest.writeInt(this.red_cnt);
        dest.writeInt(this.win_side);
        dest.writeInt(this.minute);
        dest.writeParcelable(this.victory_static, flags);
        dest.writeParcelable(this.pk_info, flags);
        dest.writeParcelable(this.pk_bank, flags);
    }

    public void readFromParcel(Parcel source) {
        this.pk_info_id = source.readInt();
        this.blue_cnt = source.readInt();
        this.red_cnt = source.readInt();
        this.win_side = source.readInt();
        this.minute = source.readInt();
        this.victory_static = source.readParcelable(PartyPkStaticDto.class.getClassLoader());
        this.pk_info = source.readParcelable(PartyPkSaveDto.class.getClassLoader());
        this.pk_bank = source.readParcelable(ChatMsg.PkBank.class.getClassLoader());
    }

    public PkDataDto() {
    }

    protected PkDataDto(Parcel in) {
        this.pk_info_id = in.readInt();
        this.blue_cnt = in.readInt();
        this.red_cnt = in.readInt();
        this.win_side = in.readInt();
        this.minute = in.readInt();
        this.victory_static = in.readParcelable(PartyPkStaticDto.class.getClassLoader());
        this.pk_info = in.readParcelable(PartyPkSaveDto.class.getClassLoader());
        this.pk_bank = in.readParcelable(ChatMsg.PkBank.class.getClassLoader());
    }

    public static final Creator<PkDataDto> CREATOR = new Creator<PkDataDto>() {
        @Override
        public PkDataDto createFromParcel(Parcel source) {
            return new PkDataDto(source);
        }

        @Override
        public PkDataDto[] newArray(int size) {
            return new PkDataDto[size];
        }
    };
}
