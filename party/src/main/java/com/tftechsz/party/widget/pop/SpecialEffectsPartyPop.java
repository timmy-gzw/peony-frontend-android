package com.tftechsz.party.widget.pop;


import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.widget.pop.BaseBottomPop;
import com.tftechsz.party.R;
import com.tftechsz.party.api.PartyApiService;
import com.tftechsz.party.entity.SpecialEffectsBean;

import io.reactivex.disposables.CompositeDisposable;

/**
 * 特效dialog
 */
public class SpecialEffectsPartyPop extends BaseBottomPop implements LeaveOnSeatPopWindow.OnSelectListener {
    Context mContext;
    int dressSwitch;
    int giftSwitch;
    ImageView switchCompatGift, switchCompatAuto;
    protected CompositeDisposable mCompositeDisposable;
    private final PartyApiService chatApiService;
    private boolean mIsNet;
    private final IOnSpecialEffectsListener iOnSpecialEffectsListener;
    private LeaveOnSeatPopWindow confirmDialogFragment;
    private int isDialog;

    public SpecialEffectsPartyPop(Context context, int dressSwitch, int giftSwitch, IOnSpecialEffectsListener iOnSpecialEffectsListener) {
        super(context);
        mContext = context;
        this.dressSwitch = dressSwitch;
        this.giftSwitch = giftSwitch;
        this.iOnSpecialEffectsListener = iOnSpecialEffectsListener;
        mCompositeDisposable = new CompositeDisposable();
        chatApiService = new RetrofitManager().createPartyApi(PartyApiService.class);
        initUI();
    }

    public void setSwitch(int giftSwitch, int dressSwitch) {
        this.dressSwitch = dressSwitch;
        this.giftSwitch = giftSwitch;
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_special_effects_set);
    }

    @Override
    public void showPopupWindow() {
        super.showPopupWindow();
        if (switchCompatGift != null) {
            switchCompatGift.setImageResource(giftSwitch == 1 ? R.mipmap.ic_switch_selector : R.mipmap.ic_switch_normal);
            switchCompatAuto.setImageResource(dressSwitch == 1 ? R.mipmap.ic_switch_selector : R.mipmap.ic_switch_normal);
        }
    }

    private void initUI() {
        switchCompatGift = findViewById(R.id.my_switch);
        switchCompatAuto = findViewById(R.id.my_switch1);

        switchCompatGift.setImageResource(giftSwitch == 1 ? R.mipmap.ic_switch_selector : R.mipmap.ic_switch_normal);
        switchCompatAuto.setImageResource(dressSwitch == 1 ? R.mipmap.ic_switch_selector : R.mipmap.ic_switch_normal);
        switchCompatGift.setOnClickListener(v -> {
            if (giftSwitch != 1) {
                showCommitDialog(1);
            } else {
                net(1, 2);
            }

        });
        switchCompatAuto.setOnClickListener(v -> {
            if (dressSwitch != 1) {
                showCommitDialog(2);
            } else {
                net(2, 2);
            }
        });


    }


    /**
     * @param isDialog 1 gift  2座驾
     */
    private void showCommitDialog(int isDialog) {
        this.isDialog = isDialog;
        String strSpecial = mContext.getString(R.string.special_effects_party_hind2);
        String strGift = mContext.getString(R.string.special_effects_party_gift_hind2);
        if (null == confirmDialogFragment) {
            confirmDialogFragment = new LeaveOnSeatPopWindow(mContext, isDialog == 1 ? strGift : strSpecial);
        }
        confirmDialogFragment.setTextMsg(isDialog == 1 ? strGift : strSpecial);
        confirmDialogFragment.addOnClickListener(this);
        confirmDialogFragment.showPopupWindow();
    }

    private void net(int type, int status) {
        if (mIsNet) {
            return;
        }
        mIsNet = true;
        mCompositeDisposable.add(chatApiService
                .specialEffectsSwitch(new SpecialEffectsBean(type, status))
                .compose(BasePresenter.applySchedulers())
                .subscribeWith(new ResponseObserver<BaseResponse<Integer>>() {
                    @Override
                    public void onSuccess(BaseResponse<Integer> response) {
                        mIsNet = false;
                        if (response != null && response.getData() != null) {
                            if (type == 1) {
                                giftSwitch = response.getData();
                            } else {
                                dressSwitch = response.getData();
                            }
                            iOnSpecialEffectsListener.specialEffectsListener(type, status);
                            switchCompatGift.setImageResource(giftSwitch == 1 ? R.mipmap.ic_switch_selector : R.mipmap.ic_switch_normal);
                            switchCompatAuto.setImageResource(dressSwitch == 1 ? R.mipmap.ic_switch_selector : R.mipmap.ic_switch_normal);
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        mIsNet = false;
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mIsNet = false;
                    }
                }));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void leaveOnSeat() {
        if (isDialog == 1) {
            net(1, 1);
        } else if (isDialog == 2) {
            net(2, 1);
        }
    }


    public interface IOnSpecialEffectsListener {
        void specialEffectsListener(int type, int status);
    }
}
