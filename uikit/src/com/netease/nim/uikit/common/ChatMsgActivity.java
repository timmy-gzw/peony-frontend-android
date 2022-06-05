package com.netease.nim.uikit.common;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 包 名 : com.netease.nim.uikit.common
 * 描 述 : TODO
 */
public class ChatMsgActivity implements Parcelable {
    private String icon;
    private String link;
    private int open;
    private float alpha;
    private Option option;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
        this.option = option;
    }

    public static class Option implements Parcelable {
        private int is_topbar;    //0不显示
        private String topbar_color;

        public int getIs_topbar() {
            return is_topbar;
        }

        public void setIs_topbar(int is_topbar) {
            this.is_topbar = is_topbar;
        }

        public String getTopbar_color() {
            return topbar_color;
        }

        public void setTopbar_color(String topbar_color) {
            this.topbar_color = topbar_color;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.is_topbar);
            dest.writeString(this.topbar_color);
        }

        public void readFromParcel(Parcel source) {
            this.is_topbar = source.readInt();
            this.topbar_color = source.readString();
        }

        public Option() {
        }

        protected Option(Parcel in) {
            this.is_topbar = in.readInt();
            this.topbar_color = in.readString();
        }

        public static final Creator<Option> CREATOR = new Creator<Option>() {
            @Override
            public Option createFromParcel(Parcel source) {
                return new Option(source);
            }

            @Override
            public Option[] newArray(int size) {
                return new Option[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.icon);
        dest.writeString(this.link);
        dest.writeInt(this.open);
        dest.writeFloat(this.alpha);
        dest.writeParcelable(this.option, flags);
    }

    public void readFromParcel(Parcel source) {
        this.icon = source.readString();
        this.link = source.readString();
        this.open = source.readInt();
        this.alpha = source.readFloat();
        this.option = source.readParcelable(Option.class.getClassLoader());
    }

    public ChatMsgActivity() {
    }

    protected ChatMsgActivity(Parcel in) {
        this.icon = in.readString();
        this.link = in.readString();
        this.open = in.readInt();
        this.alpha = in.readFloat();
        this.option = in.readParcelable(Option.class.getClassLoader());
    }

    public static final Creator<ChatMsgActivity> CREATOR = new Creator<ChatMsgActivity>() {
        @Override
        public ChatMsgActivity createFromParcel(Parcel source) {
            return new ChatMsgActivity(source);
        }

        @Override
        public ChatMsgActivity[] newArray(int size) {
            return new ChatMsgActivity[size];
        }
    };
}
