package com.tftechsz.mine.mvp.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.NetworkUtils;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.netease.nim.uikit.common.ui.imageview.AvatarVipFrameView;
import com.tftechsz.common.base.BaseListFragment;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.pagestate.PageStateConfig;
import com.tftechsz.common.pagestate.PageStateManager;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.api.MineApiService;
import com.tftechsz.mine.entity.dto.FriendDto;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Flowable;

public class FriendFragment extends BaseListFragment<FriendDto> {

    public static final String TYPE = "type";
    public static final String TYPE_WATCH = "watch";   //关注列表
    public static final String TYPE_FANS = "fans";   //粉丝列表
    public static final String TYPE_FRIEND = "friend";   //好友列表
    public String mType;
    private View view;
    private PageStateManager mPageManager;

    public static FriendFragment newInstance(String type) {
        Bundle args = new Bundle();
        args.putString(TYPE, type);
        FriendFragment fragment = new FriendFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Flowable setNetObservable() {
        mType = getArguments().getString(TYPE);
        return new RetrofitManager().createUserApi(MineApiService.class).getFriend(mPage, mType);
    }

    @Override
    public RecyclerView.LayoutManager setLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    @Override
    public void setData(List<FriendDto> datas, int page) {
        mPageManager.showContent();
        super.setData(datas, page);
        view.setVisibility(datas.size() > 0 ? View.VISIBLE : View.GONE);
        adapter.setOnItemClickListener((adapter1, view, position) ->
                ARouterUtils.toMineDetailActivity(String.valueOf(adapter.getItem(position).user_id)));
    }


    @Override
    public int setItemLayoutRes() {
        return R.layout.item_friend;
    }

    @Override
    public void bingViewHolder(BaseViewHolder helper, FriendDto item, int position) {
        AvatarVipFrameView ivAvatar = helper.getView(R.id.iv_avatar);
        helper.setText(R.id.tv_user_code, item.user_code);
        helper.setText(R.id.tv_user, String.format("%s号", mContext.getString(R.string.app_name)));
        CommonUtil.setUserName(helper.getView(R.id.tv_name), item.nickname, item.is_vip == 1);
        GlideUtils.loadRoundImageRadius(getActivity(), ivAvatar.getImageView(), item.icon);
        ivAvatar.setBgFrame(item.picture_frame);
        CommonUtil.setSexAndAge(getContext(), item.sex, item.age, helper.getView(R.id.iv_sex));
        helper.setGone(R.id.iv_real_people, item.is_real != 1);  //是否真人
        helper.setVisible(R.id.view, helper.getLayoutPosition() != getData().size() - 1);

    }

    @Override
    public String setEmptyContent() {
        if (TextUtils.equals(mType, TYPE_WATCH)) {
            return "你还没有关注美女/帅哥吗？";
        } else if (TextUtils.equals(mType, TYPE_FANS)) {
            return "你还没有粉丝呢";
        }
        return "相互关注 才是好友";
    }

    @Override
    public void initUI(Bundle savedInstanceState) {
        mType = getArguments().getString(TYPE);
        LinearLayout root = getView(R.id.root);
        view = getView(R.id.view);

        mPageManager = PageStateManager.initWhenUse(root, new PageStateConfig() {
            @Override
            public void onRetry(View retryView) {
                mPageManager.showLoading();
                onRefresh();
            }

            @Override
            public int customErrorLayoutId() {
                return R.layout.pager_error_exit;
            }

            @Override
            public void onExit() {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });
        if (!NetworkUtils.isConnected()) {
            root.postDelayed(() -> mPageManager.showError(null), 300);
        }
    }

    @Override
    protected void onRefresh() {
        if (!NetworkUtils.isConnected()) {
            mPageManager.showError(null);
            return;
        }
        super.onRefresh();

    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_friend;
    }

    @Override
    protected void initData() {

    }
}
