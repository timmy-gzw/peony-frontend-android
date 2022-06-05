package com.tftechsz.im.widget.pop;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.netease.nim.uikit.common.ChatMsg;
import com.netease.nim.uikit.common.ChatMsgUtil;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.tftechsz.im.R;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.entity.GiftDto;
import com.tftechsz.common.event.KeyBordEvent;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.PublicService;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.pop.BaseCenterPop;
import com.tftechsz.common.widget.pop.GiftNumberPopWindow;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

/**
 * 继续发送礼物
 */
public class ContinueSendGiftPopWindow extends BaseCenterPop implements View.OnClickListener {
    private ImageView mIvGift;
    private TextView mTvGiftName;
    private TextView mTvGiftCoin;
    private TextView mTvSendGift;
    private TextView mTvNumber;
    private GiftNumberPopWindow mNumPop;
    private ChatMsg.Gift mGift;
    private int mNum = 1;
    private IMMessage message;
    private ChatMsg chatMsg;
    private final CompositeDisposable mCompositeDisposable;
    private UserProviderService service;
    private ImageView mIvBottom;

    public ContinueSendGiftPopWindow(Context context) {
        super(context);
        mCompositeDisposable = new CompositeDisposable();
        service = ARouter.getInstance().navigation(UserProviderService.class);
        initView();
    }


    private void initView() {
        mIvGift = findViewById(R.id.iv_gift);
        mTvGiftName = findViewById(R.id.tv_gift_name);
        mTvGiftCoin = findViewById(R.id.tv_gift_coin);
        mTvSendGift = findViewById(R.id.tv_send_gift);
        mTvNumber = findViewById(R.id.tv_num);
        mIvBottom = findViewById(R.id.iv_bottom);
        mTvSendGift.setOnClickListener(this);
        findViewById(R.id.iv_close).setOnClickListener(this);
        mIvBottom.setOnClickListener(this);
        initBus();
    }


    public void initData(IMMessage message) {
        this.message = message;
        ChatMsg chatMsg = ChatMsgUtil.parseMessage(message);
        if (chatMsg == null)
            return;
        this.chatMsg = chatMsg;
        ChatMsg.Gift gift = JSON.parseObject(chatMsg.content, ChatMsg.Gift.class);
        if (gift != null) {
            mGift = gift;
            mNum = 1;
            GlideUtils.loadRouteImage(mContext, mIvGift, gift.gift_info.image);
            mTvGiftName.setText(gift.gift_info.name);
            if (!TextUtils.isEmpty(gift.gift_info.coin) && !TextUtils.equals("0", gift.gift_info.coin) && (TextUtils.isEmpty(gift.gift_info.cate) || Utils.numberFormat(mGift.gift_info.cate) != 11)) {
                mTvGiftCoin.setText(String.format("%s金币", mNum * Utils.numberFormat(gift.gift_info.coin)));
                mTvGiftCoin.setVisibility(View.VISIBLE);
            } else {
                mTvGiftCoin.setVisibility(View.GONE);
            }
            mTvNumber.setText(mNum + "个");
            mIvBottom.setVisibility(gift.gift_info.is_choose_num == 1 ? View.VISIBLE : View.INVISIBLE);
            if (message.getSessionType() == SessionTypeEnum.P2P) {
                if (TextUtils.equals(chatMsg.from, String.valueOf(service.getUserId()))) {
                    mTvSendGift.setText("继续送礼");
                } else {
                    mTvSendGift.setText("给TA回礼");
                }
            } else {
                if (TextUtils.equals(chatMsg.from, String.valueOf(service.getUserId()))) {
                    mTvSendGift.setText("继续送礼");
                } else if (gift.to.contains(String.valueOf(service.getUserId()))) {
                    mTvSendGift.setText("给TA回礼");
                } else {
                    mTvSendGift.setText("我也要送");
                }
            }
        }

    }

    private void initBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(KeyBordEvent.class)
                .subscribe(
                        event -> {
                            mNum = Integer.parseInt(event.num);
                            mTvNumber.setText(String.format("%s个", event.num));
                            if (mGift != null && mGift.gift_info != null && !TextUtils.isEmpty(mGift.gift_info.coin) && !TextUtils.equals(mGift.gift_info.coin, "0"))
                                mTvGiftCoin.setText(String.format("%s金币", mNum * Utils.numberFormat(mGift.gift_info.coin)));
                        }
                ));
    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_continue_send_gift);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
        }
    }

    /**
     * @param v <img width="720" height="717" src="https://pic4.zhimg.com/v2-f161ec8f85b81d18cd3e9bba179056eb_b.jpg" alt="http://www.baidu.com">
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_send_gift) {
            if (listener != null && message != null && mGift != null && mGift.gift_info != null) {
                GiftDto giftDto = new GiftDto();
                if (!TextUtils.isEmpty(mGift.gift_info.coin))
                    giftDto.coin = Integer.parseInt(mGift.gift_info.coin);
                giftDto.animation = mGift.gift_info.animation;
                giftDto.animations = mGift.gift_info.animations;
                giftDto.name = mGift.gift_info.name;
                giftDto.image = mGift.gift_info.image;
                giftDto.combo = mGift.gift_info.combo;
                giftDto.id = mGift.gift_info.id;
                giftDto.is_choose_num = mGift.gift_info.is_choose_num;
                giftDto.animationType = mGift.gift_info.animationType;
                giftDto.tag_value = mGift.gift_info.tag_value;
                giftDto.cate = Utils.numberFormat(mGift.gift_info.cate);

                if (giftDto.cate == 11) { //如果是背包礼物
                    mCompositeDisposable.add(RetrofitManager.getInstance()
                            .createUserApi(PublicService.class)
                            .packGiftCHeck(giftDto.id, mNum)
                            .compose(BasePresenter.applySchedulers())
                            .subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                                @Override
                                public void onSuccess(BaseResponse<Boolean> response) {
                                    if (response != null && response.getData()) {
                                        send(giftDto);
                                    }
                                }
                            }));
                    return;
                }
                send(giftDto);
            }
        } else if (id == R.id.iv_bottom) {
            mNumPop = new GiftNumberPopWindow(mContext, new GiftNumberPopWindow.OnSelectListener() {
                @Override
                public void onCheckNumber(int number) {
                    mNum = number;
                    mTvNumber.setText(String.format("%s个", mNum));
                    if (mGift != null && mGift.gift_info != null && !TextUtils.isEmpty(mGift.gift_info.coin) && !TextUtils.equals(mGift.gift_info.coin, "0"))
                        mTvGiftCoin.setText(String.format("%s金币", mNum * Utils.numberFormat(mGift.gift_info.coin)));
                }

                @Override
                public void onSendAll() {

                }
            }, false, false, mGift.gift_info != null && mGift.gift_info.tag_value == 4);
            mNumPop.setLayoutParams();
            mNumPop.showPopupWindow();
        } else if (id == R.id.iv_close) {
            dismiss();
        }
    }

    private void send(GiftDto giftDto) {
        if (message.getSessionType() == SessionTypeEnum.P2P) {  //单聊
            listener.sendGift(giftDto, mNum, null, "");
        } else {
            if (TextUtils.equals(chatMsg.from, String.valueOf(service.getUserId()))) {
                listener.sendGift(giftDto, mNum, mGift.to, "");
            } else if (mGift.to.contains(String.valueOf(service.getUserId()))) {
                List<String> userId = new ArrayList<>();
                userId.add(chatMsg.from);
                listener.sendGift(giftDto, mNum, userId, "");
            } else {
                listener.sendGift(giftDto, mNum, mGift.to, "");
            }
        }
        dismiss();
    }


    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }

    public interface OnSelectListener {
        void sendGift(GiftDto data, int num, List<String> userId, String name);
    }

}
