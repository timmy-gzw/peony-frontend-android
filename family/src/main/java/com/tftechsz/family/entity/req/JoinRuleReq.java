package com.tftechsz.family.entity.req;

import android.os.Parcel;
import android.os.Parcelable;

public class JoinRuleReq implements Parcelable {

    public int type;  // "type": 0,   // 对应的类型 0=所有人可以加入 ，1=设置自定义规则   2 自动加入
    public JoinRule rules;

    public static class JoinRule implements Parcelable {
        public String boy_rich_level; // 男生需要的土豪值 【必填】
        public String girl_charm_level;  // 女生需要的 【必填】
        public int is_auto;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.boy_rich_level);
            dest.writeString(this.girl_charm_level);
            dest.writeInt(this.is_auto);
        }

        public void readFromParcel(Parcel source) {
            this.boy_rich_level = source.readString();
            this.girl_charm_level = source.readString();
            this.is_auto = source.readInt();
        }

        public JoinRule() {
        }

        protected JoinRule(Parcel in) {
            this.boy_rich_level = in.readString();
            this.girl_charm_level = in.readString();
            this.is_auto = in.readInt();
        }

        public static final Creator<JoinRule> CREATOR = new Creator<JoinRule>() {
            @Override
            public JoinRule createFromParcel(Parcel source) {
                return new JoinRule(source);
            }

            @Override
            public JoinRule[] newArray(int size) {
                return new JoinRule[size];
            }
        };
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeParcelable(this.rules, flags);
    }

    public void readFromParcel(Parcel source) {
        this.type = source.readInt();
        this.rules = source.readParcelable(JoinRule.class.getClassLoader());
    }

    public JoinRuleReq() {
    }

    protected JoinRuleReq(Parcel in) {
        this.type = in.readInt();
        this.rules = in.readParcelable(JoinRule.class.getClassLoader());
    }

    public static final Creator<JoinRuleReq> CREATOR = new Creator<JoinRuleReq>() {
        @Override
        public JoinRuleReq createFromParcel(Parcel source) {
            return new JoinRuleReq(source);
        }

        @Override
        public JoinRuleReq[] newArray(int size) {
            return new JoinRuleReq[size];
        }
    };
}
