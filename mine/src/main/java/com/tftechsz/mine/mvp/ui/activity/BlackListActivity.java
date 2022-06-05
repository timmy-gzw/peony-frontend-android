package com.tftechsz.mine.mvp.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.netease.nim.uikit.common.ui.imageview.AvatarVipFrameView;
import com.tftechsz.common.base.BaseListActivity;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.mine.R;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.entity.dto.FriendDto;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Flowable;


public class BlackListActivity extends BaseListActivity<FriendDto> implements View.OnClickListener {


    public MineApiService service;

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        new ToolBarBuilder().showBack(true)
                .setTitle("黑名单")
                .build();
        service = RetrofitManager.getInstance().createUserApi(MineApiService.class);

    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public void setData(List<FriendDto> datas, int page) {
        super.setData(datas, page);
//        adapter.setOnItemLongClickListener((adapter, view, position) -> {
//            CustomPopWindow popWindow = new CustomPopWindow(BlackListActivity.this);
//            popWindow.setContent("确定取消拉黑");
//            popWindow.addOnClickListener(new CustomPopWindow.OnSelectListener() {
//                @Override
//                public void onCancel() {
//                }
//
//                @Override
//                public void onSure() {
//                    FriendDto dto = (FriendDto) adapter.getItem(position);
//                    mCompositeDisposable.add(service.cancelBlack(dto.user_id).compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
//                        @Override
//                        public void onSuccess(BaseResponse<Boolean> response) {
//                            adapter.remove(position);
//                        }
//                    }));
//                }
//            });
//            popWindow.showPopupWindow();
//            return false;
//        });
        adapter.addChildClickViewIds(R.id.tv_cancel_black);
        adapter.setOnItemChildClickListener((adapter1, view, position) -> {
            if (view.getId() == R.id.tv_cancel_black) {
                FriendDto dto = (FriendDto) adapter.getItem(position);
                mCompositeDisposable.add(service.cancelBlack(dto.user_id).compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        adapter.remove(dto);
                    }
                }));
            }
        });
    }

    @Override
    public Flowable setNetObservable() {
        return new RetrofitManager().createUserApi(MineApiService.class).getBlackList();
    }

    @Override
    public RecyclerView.LayoutManager setLayoutManager() {
        return new LinearLayoutManager(this);
    }

    @Override
    public int setItemLayoutRes() {
        return R.layout.item_black_list;
    }

    @Override
    public void bingViewHolder(BaseViewHolder helper, FriendDto item, int position) {
        helper.setText(R.id.tv_black_time, "拉黑时间：" + item.created_at);
        AvatarVipFrameView ivAvatar = helper.getView(R.id.iv_avatar);
        CommonUtil.setUserName(helper.getView(R.id.tv_name), item.nickname, item.is_vip == 1);
        GlideUtils.loadRoundImageRadius(this, ivAvatar.getImageView(), item.icon);
        ivAvatar.setBgFrame(item.picture_frame);
        helper.setVisible(R.id.view, helper.getLayoutPosition() != getDatas().size() - 1);
    }

    @Override
    public int setEmptyImg() {
        return R.mipmap.mine_ic_black_empty;
    }

    @Override
    public String setEmptyContent() {
        return "黑名单为空";
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_base_list;
    }
}
