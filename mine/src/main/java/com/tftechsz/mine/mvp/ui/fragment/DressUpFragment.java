package com.tftechsz.mine.mvp.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.NetworkUtils;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseMvpFragment;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.pagestate.PageStateConfig;
import com.tftechsz.common.pagestate.PageStateManager;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.widget.pop.SVGAPlayerPop;
import com.tftechsz.mine.R;
import com.tftechsz.mine.adapter.DressUpAdapter;
import com.tftechsz.mine.databinding.DressUpBubbleBinding;
import com.tftechsz.mine.entity.DressUpBean;
import com.tftechsz.mine.entity.VipPriceBean;
import com.tftechsz.mine.entity.VipPrivilegeBean;
import com.tftechsz.mine.entity.dto.VipConfigBean;
import com.tftechsz.mine.mvp.IView.IVipView;
import com.tftechsz.mine.mvp.presenter.IVipPresenter;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;

/**
 * 包 名 : com.tftechsz.mine.mvp.ui.fragment
 * 描 述 :
 */
public class DressUpFragment extends BaseMvpFragment<IVipView, IVipPresenter> implements IVipView {

    private DressUpBubbleBinding mBind;
    private DressUpAdapter mAdapter;
    private int mType, isSel = -1;
    private PageStateManager mPageManager;
    private SVGAPlayerPop mSvgaPlayerPop;

    @Override
    protected IVipPresenter initPresenter() {
        return new IVipPresenter();
    }

    @Override
    protected int getLayout() {
        return 0;
    }

    @Override
    public int getBindLayout() {
        return R.layout.dress_up_bubble;
    }

    @Override
    public void initUI(Bundle savedInstanceState) {
        mType = getArguments().getInt(Interfaces.EXTRA_TYPE);
        mBind = (DressUpBubbleBinding) getBind();

        mBind.recy.setLayoutManager(new GridLayoutManager(mContext, 2));
        mAdapter = new DressUpAdapter();
        mAdapter.onAttachedToRecyclerView(mBind.recy);
        mBind.recy.setAdapter(mAdapter);
        mAdapter.setEmptyView(View.inflate(mContext, R.layout.base_empty_view, null));
        mAdapter.setList(null);

        mPageManager = PageStateManager.initWhenUse(mBind.recy, new PageStateConfig() {
            @Override
            public void onRetry(View retryView) {
                mPageManager.showLoading();
                p.getMaterialAll(mType);
            }

            @Override
            public int customErrorLayoutId() {
                return R.layout.pager_error_exit;
            }

            @Override
            public void onExit() {
                mActivity.finish();
            }
        });
        if (!NetworkUtils.isConnected()) {
            mPageManager.showError(null);
        } else {
            mPageManager.showContent();
            p.getMaterialAll(mType);
        }

    }

    @Override
    protected void initData() {
        mAdapter.addChildClickViewIds(R.id.du_bg, R.id.svg_image);
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (!ClickUtil.canOperate()) return;
            int id = view.getId();
            if (id == R.id.du_bg) {
                if (isSel != position) {
                    p.postVipConfig(mType, mAdapter.getItem(position).getId());
                    isSel = position;
                }
            } else if (id == R.id.svg_image) {
                DressUpBean item = mAdapter.getItem(position);
                if (mSvgaPlayerPop == null) {
                    mSvgaPlayerPop = new SVGAPlayerPop(mContext);
                }
                mSvgaPlayerPop.addSvga(item.getSvg_name(), item.getSvg_link(), true);
            }
        });
    }

    @Override
    public void getVipPriceSuccess(List<VipPriceBean> bean) {

    }

    @Override
    public void getVipPrivilegeSuccess(List<VipPrivilegeBean> bean) {

    }

    @Override
    public void getVipConfigSuccess(VipConfigBean data) {

    }

    @Override
    public void getDressUpSuccess(List<DressUpBean> data) {
        mPageManager.showContent();
        //mAdapter.setList(getDressUpData(data));
        mAdapter.setList(data);
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            DressUpBean item = mAdapter.getItem(i);
            if (item.isActive()) {
                isSel = i;
                break;
            }
        }
    }

    private List<DressUpBean> getDressUpData(List<DressUpBean> data) { //1-头像框 2-聊天气泡
        List<DressUpBean> list = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            DressUpBean bean = data.get(i);
            if (mType == 1 && Constants.VIP_PIC_FRAME.contains(bean.getId())
                    || (mType == 2 && Constants.VIP_CHAT_BUBBLE.contains(bean.getId()))
                    || (mType == 4 && Constants.NOBLE_BANNER.contains(bean.getId()))) {
                list.add(bean);
            }
        }
        return list;
    }

    @Override
    public void postVipConfigSuccess() {
        toastTip("设置成功");
        RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_MINE_USER_INFO));
        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            DressUpBean item = mAdapter.getItem(i);
            item.setIs_active(i == isSel ? 1 : 0);
            TextView tv = (TextView) mAdapter.getViewByPosition(i, R.id.tv_is_active);
            if (tv != null) {
                tv.setVisibility(i == isSel ? View.VISIBLE : View.GONE);
            } else {
                mAdapter.setData(i, item);
            }
        }
    }
}
