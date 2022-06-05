package com.tftechsz.common.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.netease.nim.uikit.common.ConfigInfo;
import com.tftechsz.common.event.RecruitBaseDto;

import java.util.List;

public class ChatHistoryDto {

    public String family_user_num;
    public FamilyInfo my_family_info;
    public RoomInfo chat_room_info;
    public List<Banner> banner_list;
    public RecruitBaseDto recruit;

    public static class Banner implements Parcelable {
        public String img;
        public String link;
        public ConfigInfo.Option option;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.img);
            dest.writeString(this.link);
            dest.writeParcelable(this.option, flags);
        }

        public void readFromParcel(Parcel source) {
            this.img = source.readString();
            this.link = source.readString();
            this.option = source.readParcelable(ConfigInfo.Option.class.getClassLoader());
        }

        public Banner() {
        }

        protected Banner(Parcel in) {
            this.img = in.readString();
            this.link = in.readString();
            this.option = in.readParcelable(ConfigInfo.Option.class.getClassLoader());
        }

        public static final Creator<Banner> CREATOR = new Creator<Banner>() {
            @Override
            public Banner createFromParcel(Parcel source) {
                return new Banner(source);
            }

            @Override
            public Banner[] newArray(int size) {
                return new Banner[size];
            }
        };
    }

    public static class FamilyInfo implements Parcelable {
        public String family_name;
        public String tid;
        public String icon;
        public int family_id;
        public FamilyLevel family_level;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.family_name);
            dest.writeString(this.tid);
            dest.writeString(this.icon);
            dest.writeInt(this.family_id);
            dest.writeParcelable(this.family_level, flags);
        }

        public void readFromParcel(Parcel source) {
            this.family_name = source.readString();
            this.tid = source.readString();
            this.icon = source.readString();
            this.family_id = source.readInt();
            this.family_level = source.readParcelable(FamilyLevel.class.getClassLoader());
        }

        public FamilyInfo() {
        }

        protected FamilyInfo(Parcel in) {
            this.family_name = in.readString();
            this.tid = in.readString();
            this.icon = in.readString();
            this.family_id = in.readInt();
            this.family_level = in.readParcelable(FamilyLevel.class.getClassLoader());
        }

        public static final Creator<FamilyInfo> CREATOR = new Creator<FamilyInfo>() {
            @Override
            public FamilyInfo createFromParcel(Parcel source) {
                return new FamilyInfo(source);
            }

            @Override
            public FamilyInfo[] newArray(int size) {
                return new FamilyInfo[size];
            }
        };
    }

    public static class FamilyLevel implements Parcelable{
        public String icon;
        public String level;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.icon);
            dest.writeString(this.level);
        }

        public void readFromParcel(Parcel source) {
            this.icon = source.readString();
            this.level = source.readString();
        }

        public FamilyLevel() {
        }

        protected FamilyLevel(Parcel in) {
            this.icon = in.readString();
            this.level = in.readString();
        }

        public static final Creator<FamilyLevel> CREATOR = new Creator<FamilyLevel>() {
            @Override
            public FamilyLevel createFromParcel(Parcel source) {
                return new FamilyLevel(source);
            }

            @Override
            public FamilyLevel[] newArray(int size) {
                return new FamilyLevel[size];
            }
        };
    }


    public static class RoomInfo implements Parcelable{
        public String room_name;
        public String room_id;
        public String icon;
        public String room_des;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.room_name);
            dest.writeString(this.room_id);
            dest.writeString(this.icon);
            dest.writeString(this.room_des);
        }

        public void readFromParcel(Parcel source) {
            this.room_name = source.readString();
            this.room_id = source.readString();
            this.icon = source.readString();
            this.room_des = source.readString();
        }

        public RoomInfo() {
        }

        protected RoomInfo(Parcel in) {
            this.room_name = in.readString();
            this.room_id = in.readString();
            this.icon = in.readString();
            this.room_des = in.readString();
        }

        public static final Creator<RoomInfo> CREATOR = new Creator<RoomInfo>() {
            @Override
            public RoomInfo createFromParcel(Parcel source) {
                return new RoomInfo(source);
            }

            @Override
            public RoomInfo[] newArray(int size) {
                return new RoomInfo[size];
            }
        };
    }


}
