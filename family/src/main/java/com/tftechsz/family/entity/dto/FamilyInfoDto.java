package com.tftechsz.family.entity.dto;

import android.os.Parcel;
import android.os.Parcelable;

import com.tftechsz.family.entity.req.JoinRuleReq;
import com.tftechsz.common.entity.ChatHistoryDto;

import java.util.List;

public class FamilyInfoDto implements Parcelable {

    public String tid;
    public int family_id;
    public String user_count;
    public String prestige;   // 威望
    public String all_prestige;   //   500
    public int week_rank;  //周威望排名
    public int total_rank;  //总威望排名
    public int my_role_id;  //角色id
    public int is_in_family;   //是否在该家族中  0否 1是
    public String intro;
    public String announcement;
    public String icon;
    public int is_apply_status;
    public String family_name;  //名称
    public JoinRuleReq rules;  //加入家族条件
    public String icon_desc;  //头像说明
    public FamilyMemberDto owner_user_info;  //族长
    public List<FamilyMemberDto> member_info_list; //成员列表
    public int is_mute;  //是否开启禁言  1：禁言了
    public FamilyLevel family_level;
    public int status; //1-正常 2-审核中 9-封禁
    public ChatHistoryDto.RoomInfo chat_room_info;
    public int member_info_list_num;
    public String prestige_url;
    public String familysize_url;
    public int is_sign;  //是否签到 0：否 1：是
    public List<Banner> banner;
    public List<Task> task;
    public int label_new;
    public int voice_online; //家族语音房是否在线

    public static class Task implements Parcelable {
        public String icon;
        public String title;
        public String reward_icon;
        public String coin;
        public int status; //0-进行中 1-待发放 2-已完成

        protected Task(Parcel in) {
            icon = in.readString();
            title = in.readString();
            reward_icon = in.readString();
            coin = in.readString();
            status = in.readInt();
        }

        public static final Creator<Task> CREATOR = new Creator<Task>() {
            @Override
            public Task createFromParcel(Parcel in) {
                return new Task(in);
            }

            @Override
            public Task[] newArray(int size) {
                return new Task[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(icon);
            dest.writeString(title);
            dest.writeString(reward_icon);
            dest.writeString(coin);
            dest.writeInt(status);
        }
    }

    public static class Banner implements Parcelable {
        public String url;
        public String link;
        public int is_topbar;
        public String topbar_color;

        protected Banner(Parcel in) {
            url = in.readString();
            link = in.readString();
            is_topbar = in.readInt();
            topbar_color = in.readString();
        }

        public static final Creator<Banner> CREATOR = new Creator<Banner>() {
            @Override
            public Banner createFromParcel(Parcel in) {
                return new Banner(in);
            }

            @Override
            public Banner[] newArray(int size) {
                return new Banner[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.url);
            dest.writeString(this.link);
            dest.writeInt(this.is_topbar);
            dest.writeString(this.topbar_color);
        }
    }

    public static class FamilyLevel implements Parcelable {
        public int level;
        public int diff;
        public int total;
        public String icon;


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.level);
            dest.writeInt(this.diff);
            dest.writeInt(this.total);
            dest.writeString(this.icon);
        }

        public void readFromParcel(Parcel source) {
            this.level = source.readInt();
            this.diff = source.readInt();
            this.total = source.readInt();
            this.icon = source.readString();
        }

        public FamilyLevel() {
        }

        protected FamilyLevel(Parcel in) {
            this.level = in.readInt();
            this.diff = in.readInt();
            this.total = in.readInt();
            this.icon = in.readString();
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tid);
        dest.writeInt(this.family_id);
        dest.writeString(this.user_count);
        dest.writeString(this.prestige);
        dest.writeInt(this.week_rank);
        dest.writeInt(this.total_rank);
        dest.writeInt(this.my_role_id);
        dest.writeInt(this.is_in_family);
        dest.writeString(this.intro);
        dest.writeString(this.announcement);
        dest.writeString(this.icon);
        dest.writeInt(this.is_apply_status);
        dest.writeString(this.family_name);
        dest.writeParcelable(this.rules, flags);
        dest.writeString(this.icon_desc);
        dest.writeParcelable(this.owner_user_info, flags);
        dest.writeTypedList(this.member_info_list);
        dest.writeInt(this.is_mute);
        dest.writeParcelable(this.family_level, flags);
        dest.writeInt(this.status);
        dest.writeParcelable(this.chat_room_info, flags);
    }

    public void readFromParcel(Parcel source) {
        this.tid = source.readString();
        this.family_id = source.readInt();
        this.user_count = source.readString();
        this.prestige = source.readString();
        this.week_rank = source.readInt();
        this.total_rank = source.readInt();
        this.my_role_id = source.readInt();
        this.is_in_family = source.readInt();
        this.intro = source.readString();
        this.announcement = source.readString();
        this.icon = source.readString();
        this.is_apply_status = source.readInt();
        this.family_name = source.readString();
        this.rules = source.readParcelable(JoinRuleReq.class.getClassLoader());
        this.icon_desc = source.readString();
        this.owner_user_info = source.readParcelable(FamilyMemberDto.class.getClassLoader());
        this.member_info_list = source.createTypedArrayList(FamilyMemberDto.CREATOR);
        this.is_mute = source.readInt();
        this.family_level = source.readParcelable(FamilyLevel.class.getClassLoader());
        this.status = source.readInt();
        this.chat_room_info = source.readParcelable(ChatHistoryDto.RoomInfo.class.getClassLoader());
    }

    public FamilyInfoDto() {
    }

    protected FamilyInfoDto(Parcel in) {
        this.tid = in.readString();
        this.family_id = in.readInt();
        this.user_count = in.readString();
        this.prestige = in.readString();
        this.week_rank = in.readInt();
        this.total_rank = in.readInt();
        this.my_role_id = in.readInt();
        this.is_in_family = in.readInt();
        this.intro = in.readString();
        this.announcement = in.readString();
        this.icon = in.readString();
        this.is_apply_status = in.readInt();
        this.family_name = in.readString();
        this.rules = in.readParcelable(JoinRuleReq.class.getClassLoader());
        this.icon_desc = in.readString();
        this.owner_user_info = in.readParcelable(FamilyMemberDto.class.getClassLoader());
        this.member_info_list = in.createTypedArrayList(FamilyMemberDto.CREATOR);
        this.is_mute = in.readInt();
        this.family_level = in.readParcelable(FamilyLevel.class.getClassLoader());
        this.status = in.readInt();
        this.chat_room_info = in.readParcelable(ChatHistoryDto.RoomInfo.class.getClassLoader());
    }

    public static final Creator<FamilyInfoDto> CREATOR = new Creator<FamilyInfoDto>() {
        @Override
        public FamilyInfoDto createFromParcel(Parcel source) {
            return new FamilyInfoDto(source);
        }

        @Override
        public FamilyInfoDto[] newArray(int size) {
            return new FamilyInfoDto[size];
        }
    };
}
