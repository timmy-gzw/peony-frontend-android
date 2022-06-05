package com.tftechsz.common.entity;

import android.net.Uri;
import android.text.TextUtils;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CircleBean implements MultiItemEntity, Serializable {
    private int blog_id;//每条动态的id
    private int user_id;//用户的id
    private String content;//用户发布的文本
    private ArrayList<String> media;//发布的图片集合
    private ArrayList<String> media_mini;//发布的图片集合
    private int type;//1-视频  2-音频  3-图片
    private int status;
    private String created_at;//创建时间
    private String updated_at;//更新时间
    private String province;//省份
    private String city;//城市
    private String nickname;//昵称
    public int is_accost;  //是否搭讪
    public int browse;  //浏览次数
    private int sex;//性别
    public String age;
    private String icon;//头像
    private int is_real;//是否真人
    private int is_self;
    private int is_praise;//是否点赞过
    private int praises;//点赞数
    private int comments;//评论数
    private List<Integer> video_size;
    public int picture_frame;
    private int is_vip;
    //搭讪缩放动画
    public boolean transitionAnima;

    //我是否关注了对方， 1-关注 0-未关注
    public int is_follow;

    @Override
    public String toString() {
        return "DataBean{" +
                "blog_id=" + blog_id +
                ", user_id=" + user_id +
                ", content='" + content + '\'' +
                ", media=" + media +
                ", type=" + type +
                ", status=" + status +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", nickname='" + nickname + '\'' +
                ", sex=" + sex +
                ", icon='" + icon + '\'' +
                ", is_real=" + is_real +
                ", is_self=" + is_self +
                ", praises=" + praises +
                ", comments=" + comments +
                ", is_follow=" + is_follow +
                '}';
    }

    public boolean isVip() {
        return getIs_vip() == 1;
    }

    public int getIs_vip() {
        return is_vip;
    }

    public void setIs_vip(int is_vip) {
        this.is_vip = is_vip;
    }

    public ArrayList<String> getMedia_mini() {
        return media_mini;
    }

    public void setMedia_mini(ArrayList<String> media_mini) {
        this.media_mini = media_mini;
    }

    public List<Integer> getVideo_size() {
        return video_size;
    }

    public void setVideo_size(List<Integer> video_size) {
        this.video_size = video_size;
    }

    public List<Uri> getImageUriList() {
        List<Uri> imageUriList = new ArrayList<>();
        if (media != null && media.size() > 0) {
            for (String str : media) {
                imageUriList.add(Uri.parse(str));
                //imageUriList.add(Uri.parse("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=165712837,582075724&fm=26&gp=0.jpg"));

            }
        }
        return imageUriList;
    }

    public int getIs_accost() {
        return is_accost;
    }

    public boolean isAccost() {
        return is_accost == 1;
    }

    public void setIs_accost(int is_accost) {
        this.is_accost = is_accost;
    }

    public int getIs_praise() {
        return is_praise;
    }

    public boolean isPraise() {
        return is_praise == 1;
    }

    public void setIs_praise(int is_praise) {
        this.is_praise = is_praise;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public int getPraises() {
        return praises;
    }

    public void setPraises(int praises) {
        this.praises = praises;
    }

    public int getBlog_id() {
        return blog_id;
    }

    public void setBlog_id(int blog_id) {
        this.blog_id = blog_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getIs_real() {
        return is_real;
    }

    public boolean isReal() {
        return is_real == 1;
    }

    public void setIs_real(int is_real) {
        this.is_real = is_real;
    }

    public int getIs_self() {
        return is_self;
    }

    public void setIs_self(int is_self) {
        this.is_self = is_self;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }


    public String getContent() {
        if (TextUtils.isEmpty(content)) {
            return "";
        }
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public ArrayList<String> getMedia() {
        return media;
    }

    public void setMedia(ArrayList<String> media) {
        this.media = media;
    }

    public int getType() {
        return type;
    }

    public boolean getTypeOfVideo() {
        return type == 1;
    }

    public boolean getTypeOfAudio() {
        return type == 2;
    }

    public boolean getTypeOfPic() {
        return type == 3;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public int getItemType() {
        return getType();
    }

}
