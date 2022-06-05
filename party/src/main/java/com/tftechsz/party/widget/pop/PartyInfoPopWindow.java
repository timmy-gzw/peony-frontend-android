package com.tftechsz.party.widget.pop;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.netease.nim.uikit.common.DensityUtils;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.common.widget.pop.BaseCenterPop;
import com.tftechsz.party.R;
import com.tftechsz.party.api.PartyApiService;
import com.tftechsz.party.entity.dto.JoinPartyDto;
import com.tftechsz.party.entity.dto.PartyInfoDto;
import com.tftechsz.party.entity.dto.PartyUserInfoDto;

import io.reactivex.disposables.CompositeDisposable;

/**
 * 派对信息
 */
public class PartyInfoPopWindow extends BaseCenterPop implements View.OnClickListener {

    private PartyUserInfoDto userInfoDto;
    private final PartyApiService partyApiService;
    private final UserProviderService service;
    private ImageView mIvPartyThumb, mIvPartyOwner;
    private TextView mTvPartyName, mTvPartyCode;  //派对名称 code
    private TextView mTvAnnounceTitle, mTvAnnouncement;
    private TextView mTvOwner, mTvDesc;
    private ImageView mIvClassify;
    private NestedScrollView mNsAnnouncement;
    private final CompositeDisposable mCompositeDisposable;

    public PartyInfoPopWindow(Context context) {
        super(context);
        service = ARouter.getInstance().navigation(UserProviderService.class);
        partyApiService = RetrofitManager.getInstance().createPartyApi(PartyApiService.class);
        mCompositeDisposable = new CompositeDisposable();
        initView();
    }


    private void initView() {
        findViewById(R.id.iv_close).setOnClickListener(this);
        findViewById(R.id.cl_party_owner).setOnClickListener(this);
        mIvPartyOwner = findViewById(R.id.iv_party_owner);
        mTvAnnounceTitle = findViewById(R.id.tv_title);
        mTvAnnouncement = findViewById(R.id.tv_announcement_content);
        mIvPartyThumb = findViewById(R.id.iv_party_thumb);
        mTvPartyName = findViewById(R.id.tv_party_name);
        mTvPartyCode = findViewById(R.id.tv_party_code);
        mIvClassify = findViewById(R.id.iv_classify);
        mTvOwner = findViewById(R.id.tv_party_owner_name);
        mTvDesc = findViewById(R.id.tv_desc);
        mNsAnnouncement = findViewById(R.id.ns_announcement);
    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_party_info);
    }


    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.iv_close) {
            dismiss();
        } else if (id == R.id.cl_party_owner) {   //房主信息
            if (userInfoDto == null) return;
            if (service.getUserId() == userInfoDto.getUser_id()) {
                ARouterUtils.toMineDetailActivity("");
            } else {
                ARouterUtils.toMineDetailActivity("" + userInfoDto.getUser_id());
            }
            dismiss();
        }
    }

    /**
     * 获取派对信息
     */
    public void getPartyInfo(int id) {
        mCompositeDisposable.add(partyApiService.getPartyInfo(id)
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<JoinPartyDto>>() {
                    @Override
                    public void onSuccess(BaseResponse<JoinPartyDto> response) {
                        if (response.getData() != null) {
                            PartyInfoDto partyInfoDto = response.getData().getRoom();
                            userInfoDto = response.getData().getUser();
                            //派对信息
                            if (partyInfoDto != null) {
                                GlideUtils.loadRoundImage(getContext(), mIvPartyThumb, partyInfoDto.getIcon());
                                String partyName = partyInfoDto.getRoom_name();
                                if (partyInfoDto.getRoom_name().length() > 10) {
                                    partyName = partyName.substring(0, 10) + "...";
                                }
                                mTvPartyName.setText(partyName);
                                Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.party_ic_pretty_code);
                                if (partyInfoDto.getRoom_code_pretty() == 0) {
                                    mTvPartyCode.setCompoundDrawablesWithIntrinsicBounds(null,
                                            null, null, null);
                                    mTvPartyCode.setText("ID " + partyInfoDto.getRoom_code());
                                } else {
                                    mTvPartyCode.setCompoundDrawablesWithIntrinsicBounds(drawable,
                                            null, null, null);
                                    mTvPartyCode.setCompoundDrawablePadding(DensityUtils.dp2px(mContext, 4));
                                    mTvPartyCode.setText("ID " + partyInfoDto.getRoom_code_pretty());
                                }
                                GlideUtils.loadRoundImage(getContext(), mIvClassify, partyInfoDto.getClassify_icon());
                                if (!TextUtils.isEmpty(partyInfoDto.getDesc())) {
                                    mTvAnnouncement.setText(partyInfoDto.getDesc());
                                }
                                if (!TextUtils.isEmpty(partyInfoDto.getTitle())) {
                                    mTvAnnounceTitle.setText(partyInfoDto.getTitle());
                                }
                                //派对公告
                                if (!TextUtils.isEmpty(partyInfoDto.getDesc()) && !TextUtils.isEmpty(partyInfoDto.getTitle())) {
                                    mNsAnnouncement.setVisibility(View.VISIBLE);
                                } else {
                                    mNsAnnouncement.setVisibility(View.GONE);
                                }
                            }
                            if (userInfoDto != null) {
                                GlideUtils.loadCircleImage(getContext(), mIvPartyOwner, userInfoDto.getIcon(), R.mipmap.ic_default_avatar);
                                mTvOwner.setText(userInfoDto.getNickname());
                                if (!TextUtils.isEmpty(userInfoDto.getDesc())) {
                                    mTvDesc.setText(userInfoDto.getDesc());
                                }
                            }
                        }
                    }
                }));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
        }
    }

}
