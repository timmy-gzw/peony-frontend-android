package com.tftechsz.im.widget.pop;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nimlib.sdk.team.model.Team;
import com.tftechsz.im.R;
import com.tftechsz.im.adapter.CoupleBagAdapter;
import com.tftechsz.im.api.ChatApiService;
import com.tftechsz.im.model.dto.CoupleBagDetailDto;
import com.tftechsz.im.model.dto.CoupleBagDto;
import com.tftechsz.common.entity.FamilyIdDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.common.widget.pop.BaseCenterPop;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

/**
 * 情侣礼包
 */
public class CoupleGiftBagPop extends BaseCenterPop {

    private final RecyclerView mRvCoupleGiftPop;
    private ChatApiService chatApiService;
    private String mSessionId;
    private final CompositeDisposable mCompositeDisposable;
    private CoupleGiftBagDetailPop coupleGiftBagDetailPop;

    public CoupleGiftBagPop(Context context) {
        super(context);
        chatApiService = RetrofitManager.getInstance().createFamilyApi(ChatApiService.class);
        mCompositeDisposable = new CompositeDisposable();
        mRvCoupleGiftPop = findViewById(R.id.rv_couple_gift_bag);
        mRvCoupleGiftPop.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_couple_gift_bag);
    }


    public void setData(String sessionId, List<CoupleBagDto> data) {
        mSessionId = sessionId;
        CoupleBagAdapter adapter = new CoupleBagAdapter();
        mRvCoupleGiftPop.setAdapter(adapter);
        adapter.setList(data);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter1, @NonNull View view, int position) {
                getFamilyId(adapter.getData().get(position).id);
            }
        });
    }

    /**
     * 获取群id
     */
    private void getFamilyId(int bagId) {
        Team t = NimUIKit.getTeamProvider().getTeamById(mSessionId);
        if (t == null) return;
        mCompositeDisposable.add(new RetrofitManager().createFamilyApi(ChatApiService.class).getFamilyId(t.getId())
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<FamilyIdDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<FamilyIdDto> response) {
                        if (null != response.getData()) {
                            getAirdrop(response.getData().family_id, bagId);
                        }
                    }
                }));
    }


    /**
     * 获取礼包详情
     */
    public void getAirdrop(int familyId, int bagId) {
        mCompositeDisposable.add(chatApiService.getGiftBagDetail(familyId, bagId).compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<CoupleBagDetailDto>>() {
            @Override
            public void onSuccess(BaseResponse<CoupleBagDetailDto> response) {
                if (response.getData() != null) {
                    if (coupleGiftBagDetailPop == null)
                        coupleGiftBagDetailPop = new CoupleGiftBagDetailPop(mContext);
                    coupleGiftBagDetailPop.setData(familyId, bagId,response.getData());
                    coupleGiftBagDetailPop.showPopupWindow();
                    dismiss();
                }
            }
        }));
    }

}
