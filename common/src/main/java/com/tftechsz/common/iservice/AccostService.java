package com.tftechsz.common.iservice;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.netease.nim.uikit.bean.AccostDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;

/**
 * 搭讪
 */
public interface AccostService extends IProvider {
    /**
     * 搭讪用户
     * * @return accost_type  1 喜欢 2 搭讪
     * * accost_from  const TYPE_SYSTEM = 1; // 系统 月老逻辑
     * * const TYPE_HOME = 2; // 首页搭讪
     * * const TYPE_DETAIL = 3; // 个人资料页搭讪
     * * const TYPE_BLOG = 4; // 动态搭讪
     * * const TYPE_PHOTO = 5; // 相册搭讪
     * * const TYPE_SEARCH = 6; // 搜索搭讪
     * * const TYPE_TIPS = 7; // 私聊TIPS搭讪
     * * const TYPE_ALERT = 8; // 首页日今弹窗搭讪
     * * TYPE_PARTY_ACCOST = 9 //派对在线搭讪
     */
    void accostUser(String userId, int accost_type, int accost_from, ResponseObserver<BaseResponse<AccostDto>> responseObserver);


}
