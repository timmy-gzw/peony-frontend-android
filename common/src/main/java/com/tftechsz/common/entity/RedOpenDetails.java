package com.tftechsz.common.entity;

import java.util.List;

public
/**
 *  包 名 : com.tftechsz.common.entity

 *  描 述 : TODO
 */
class RedOpenDetails {

    /**
     * coin : 100
     * create_red_packet_user_id : 327
     * des : 恭喜发财，大吉大利
     * icon : http://user-cdn.peony125.com/user/avatar/6173d99f6331ecf404a609b8dc53be13.jpg?x-oss-process=image/resize,m_lfit,h_800,w_800
     * is_complete : 0
     * is_receive : 1
     * list : [{"age":"19","coin":51,"created_at":"01/25 14:03","icon":"http://user-cdn.peony125.com/user/avatar/6173d99f6331ecf404a609b8dc53be13.jpg?x-oss-process=image/resize,m_lfit,h_800,w_800","nickname":"小白太白","sex":"1","user_id":327}]
     * max_user_id : 0
     * nickname : 小白太白
     * plan_message : 已领取1/3
     */

    public int coin;
    public int get_coin;
    public int create_red_packet_user_id;
    public String des;
    public String icon;
    public int is_complete;
    public int is_receive;
    public int max_user_id;
    public String nickname;
    public String plan_message;
    public List<ListBean> list;

    public static class ListBean {
        /**
         * age : 19
         * coin : 51
         * created_at : 01/25 14:03
         * icon : http://user-cdn.peony125.com/user/avatar/6173d99f6331ecf404a609b8dc53be13.jpg?x-oss-process=image/resize,m_lfit,h_800,w_800
         * nickname : 小白太白
         * sex : 1
         * user_id : 327
         */

        public String age;
        public int coin;
        public String created_at;
        public String icon;
        public String nickname;
        public int sex;
        public int user_id;
    }
}
