package com.tftechsz.party.entity.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.netease.nim.uikit.common.ChatMsgActivity;
import com.tftechsz.common.nertcvoiceroom.model.VoiceRoomSeat;

import java.util.List;

public class JoinPartyDto implements Parcelable {
    private PartyInfoDto room;
    private List<WatcherRankDto> rank;
    private PartyUserInfoDto user;
    private List<VoiceRoomSeat> microphone;  //获取所有麦位信息
    private List<ChatMsgActivity> activity;  //活动

    public List<ChatMsgActivity> getActivity() {
        return activity;
    }

    public void setActivity(List<ChatMsgActivity> activity) {
        this.activity = activity;
    }

    public PartyInfoDto getRoom() {
        return room;
    }

    public void setRoom(PartyInfoDto room) {
        this.room = room;
    }


    public List<WatcherRankDto> getRank() {
        return rank;
    }

    public void setRank(List<WatcherRankDto> rank) {
        this.rank = rank;
    }

    public PartyUserInfoDto getUser() {
        return user;
    }

    public void setUser(PartyUserInfoDto user) {
        this.user = user;
    }

    public List<VoiceRoomSeat> getMicrophone() {
        return microphone;
    }

    public void setMicrophone(List<VoiceRoomSeat> microphone) {
        this.microphone = microphone;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.room, flags);
        dest.writeTypedList(this.rank);
        dest.writeParcelable(this.user, flags);
        dest.writeTypedList(this.microphone);
        dest.writeTypedList(this.activity);
    }

    public void readFromParcel(Parcel source) {
        this.room = source.readParcelable(PartyInfoDto.class.getClassLoader());
        this.rank = source.createTypedArrayList(WatcherRankDto.CREATOR);
        this.user = source.readParcelable(PartyUserInfoDto.class.getClassLoader());
        this.microphone = source.createTypedArrayList(VoiceRoomSeat.CREATOR);
        this.activity = source.createTypedArrayList(ChatMsgActivity.CREATOR);
    }

    public JoinPartyDto() {
    }

    protected JoinPartyDto(Parcel in) {
        this.room = in.readParcelable(PartyInfoDto.class.getClassLoader());
        this.rank = in.createTypedArrayList(WatcherRankDto.CREATOR);
        this.user = in.readParcelable(PartyUserInfoDto.class.getClassLoader());
        this.microphone = in.createTypedArrayList(VoiceRoomSeat.CREATOR);
        this.activity = in.createTypedArrayList(ChatMsgActivity.CREATOR);
    }

    public static final Creator<JoinPartyDto> CREATOR = new Creator<JoinPartyDto>() {
        @Override
        public JoinPartyDto createFromParcel(Parcel source) {
            return new JoinPartyDto(source);
        }

        @Override
        public JoinPartyDto[] newArray(int size) {
            return new JoinPartyDto[size];
        }
    };
}
