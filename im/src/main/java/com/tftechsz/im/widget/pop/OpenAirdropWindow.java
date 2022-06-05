package com.tftechsz.im.widget.pop;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.netease.nim.uikit.common.UIUtils;
import com.tftechsz.im.R;
import com.tftechsz.im.adapter.ReceiveAirdropAdapter;
import com.tftechsz.im.api.ChatApiService;
import com.tftechsz.im.model.dto.AirdropBagDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.common.widget.CircleImageView;
import com.tftechsz.common.widget.pop.BaseCenterPop;

import java.util.ArrayList;
import java.util.List;

import androidx.viewpager2.widget.ViewPager2;
import io.reactivex.disposables.CompositeDisposable;

/**
 * 打开空投
 */
public class OpenAirdropWindow extends BaseCenterPop implements View.OnClickListener {

    private Context mContext;
    private ViewPager2 viewPager2;
    private ImageView mIvLeft, mIvRight;
    private int currentPosition = 0;
    private final ChatApiService chatApiService;
    private final CompositeDisposable mCompositeDisposable;
    private List<AirdropBagDto> airdropList;
    private TextView mTvName, mTvType, mTvSex;
    private CircleImageView mCiAvatar;

    public OpenAirdropWindow(Context context) {
        super(context);
        mContext = context;
        setPopupFadeEnable(true);
        chatApiService = RetrofitManager.getInstance().createFamilyApi(ChatApiService.class);
        mCompositeDisposable = new CompositeDisposable();
        airdropList = new ArrayList<>();
        viewPager2 = findViewById(R.id.view_pager);
        mCiAvatar = findViewById(R.id.ci_avatar);
        mTvName = findViewById(R.id.tv_name);
        mTvType = findViewById(R.id.tv_type);
        mIvLeft = findViewById(R.id.iv_left);
        mIvLeft.setOnClickListener(this);
        mIvRight = findViewById(R.id.iv_right);
        mIvRight.setOnClickListener(this);
        findViewById(R.id.iv_close).setOnClickListener(this);
    }

    public void initData(int id) {
        getAirdrop(id);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentPosition = position;
                if (currentPosition == 0) {
                    mIvLeft.setImageResource(R.mipmap.chat_ic_left);
                    mIvRight.setImageResource(R.mipmap.chat_ic_right_red);
                } else if (currentPosition == airdropList.size() - 1) {
                    mIvLeft.setImageResource(R.mipmap.chat_ic_left_red);
                    mIvRight.setImageResource(R.mipmap.chat_ic_right);
                } else {
                    mIvLeft.setImageResource(R.mipmap.chat_ic_left_red);
                    mIvRight.setImageResource(R.mipmap.chat_ic_right_red);
                }
                if (airdropList != null) {
                    AirdropBagDto bagDto = airdropList.get(position);
                    GlideUtils.loadRoundImage(mContext, mCiAvatar, bagDto.icon);
                    mTvName.setText(bagDto.nickname + bagDto.gift_bag_name);
                    UIUtils.setFamilyTitle(mTvType, bagDto.family_role);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_open_airdrop);
    }

    /**
     * 获取空投信息
     */
    public void getAirdrop(int id) {
        mCompositeDisposable.add(chatApiService.getAirdropBag(id).compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<List<AirdropBagDto>>>() {
            @Override
            public void onSuccess(BaseResponse<List<AirdropBagDto>> response) {
                if (response.getData() != null) {
                    airdropList.clear();
                    currentPosition = 0;
                    airdropList = response.getData();
                    ReceiveAirdropAdapter adapter = new ReceiveAirdropAdapter(airdropList);
                    viewPager2.setAdapter(adapter);
                    adapter.addOnClickListener(airdropId -> openAir(airdropId));
                    viewPager2.setCurrentItem(currentPosition);
                    viewPager2.setOffscreenPageLimit(airdropList.size());
                    if (airdropList.size() == 1) {
                        mIvLeft.setVisibility(View.GONE);
                        mIvRight.setVisibility(View.GONE);
                    } else {
                        mIvLeft.setImageResource(R.mipmap.chat_ic_left_red);
                        mIvRight.setImageResource(R.mipmap.chat_ic_right_red);
                        mIvLeft.setVisibility(View.VISIBLE);
                        mIvRight.setVisibility(View.VISIBLE);
                    }
                }
            }
        }));
    }

    /**
     * 打开取空投信息
     */
    public void openAir(int id) {
        mCompositeDisposable.add(chatApiService.openAirdrop(id).compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
            @Override
            public void onSuccess(BaseResponse<Boolean> response) {
                if (response.getData() != null) {
                    if (listener != null) {
                        listener.openAirdrop(id);
                    }
                    dismiss();
                }
            }

            @Override
            public void onFail(int code, String msg) {
                super.onFail(code, msg);
                dismiss();
            }
        }));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_close) {
            dismiss();
        } else if (id == R.id.iv_left) {
            if (currentPosition == 0 || airdropList == null) return;
            currentPosition--;
            viewPager2.setCurrentItem(currentPosition);
        } else if (id == R.id.iv_right) {
            if (airdropList == null) return;
            if (currentPosition >= airdropList.size() - 1) return;
            currentPosition++;
            viewPager2.setCurrentItem(currentPosition);
        }
    }


    public interface OnSelectListener {
        void openAirdrop(int airdropId);
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }
}
