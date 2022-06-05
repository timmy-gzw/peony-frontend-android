package com.tftechsz.moment.mvp.entity;

import java.util.List;

/**
 * {"user_id":5,
     "content":"\u53bb\u53bb\u53bbtttt",
 *   "media":["http:www.baidu.com","http:www.baidu.com"],
 *   "type":3,
 *   "status":1,
 *   "created_at":"2020-11-27 20:02:27",
 *   "updated_at":"2020-11-27 20:02:27",
 *   "province":"\u5e7f\u4e1c",
 *   "city":"\u6df1\u5733",
 *   "nickname":"\u6768\u51ef",
 *   "sex":1,
 *   "icon":"https:\/\/peony-app-user.oss-cn-shanghai.aliyuncs.com\/timg.jpg",
 *   "is_real":0,
 *   "is_self":0
 * }
 */

//动态 推荐
public class RecommedUser {
        private String user_id;
        private String content;
        private List<String> media;
        private String type;
        private String status;
        private String created_at;
        private String updated_at;
        private String province;
        private String city;
        private String nickname;
        private String sex;
        private String icon;
        private String is_real;
        private String is_self;

        @Override
        public String toString() {
            return "RecommedUser{" +
                    "user_id='" + user_id + '\'' +
                    ", content='" + content + '\'' +
                    ", media=" + media +
                    ", type='" + type + '\'' +
                    ", status='" + status + '\'' +
                    ", created_at='" + created_at + '\'' +
                    ", updated_at='" + updated_at + '\'' +
                    ", province='" + province + '\'' +
                    ", city='" + city + '\'' +
                    ", nickname='" + nickname + '\'' +
                    ", sex='" + sex + '\'' +
                    ", icon='" + icon + '\'' +
                    ", is_real='" + is_real + '\'' +
                    ", is_self='" + is_self + '\'' +
                    '}';
        }


        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public List<String> getMedia() {
            return media;
        }

        public void setMedia(List<String> media) {
            this.media = media;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
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

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getIs_real() {
            return is_real;
        }

        public void setIs_real(String is_real) {
            this.is_real = is_real;
        }

        public String getIs_self() {
            return is_self;
        }

        public void setIs_self(String is_self) {
            this.is_self = is_self;
        }
    }




