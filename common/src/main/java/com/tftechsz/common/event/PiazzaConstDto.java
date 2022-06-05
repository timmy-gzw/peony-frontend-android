package com.tftechsz.common.event;

import com.netease.nim.uikit.common.ConfigInfo;

import java.util.List;

/**
 * 广场联系信息
 */
public class PiazzaConstDto {
    /**
     * $content = [
     * 'title' => '申请创建家族后，请添加以下联系方式，与相关人员取得联系后，才能为您通过审核哦',
     * 'wechat_contact' => [
     * [
     * 'msg' => sprintf('%s（公众号）', implode(',', $wechat_official)),
     * 'msg_copy' => implode(',', $wechat_official),
     * ],
     * [
     * 'msg' => sprintf('1901988895（QQ号）'),
     * 'msg_copy' => '1901988895',
     * ],
     * ]
     * ];
     */
    public String title;
    public List<ConfigInfo.FeedbackContactNew> wechat_contact;

}
