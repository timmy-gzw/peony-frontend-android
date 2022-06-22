package com.tftechsz.mine.mvp.ui.fragment;

import android.os.Bundle;

import com.netease.nim.uikit.common.UserInfo;
import com.tftechsz.common.base.BaseMvpFragment;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.dto.GiftDto;
import com.tftechsz.mine.mvp.IView.IMineAboutMeView;
import com.tftechsz.mine.mvp.presenter.MineAboutMePresenter;

import java.util.List;

public class MineAboutMeFragment extends BaseMvpFragment<IMineAboutMeView, MineAboutMePresenter> implements IMineAboutMeView {

    public static MineAboutMeFragment newInstance(String type) {
        Bundle args = new Bundle();
        MineAboutMeFragment fragment = new MineAboutMeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_mine_about_me;
    }

    @Override
    public void initUI(Bundle savedInstanceState) {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected MineAboutMePresenter initPresenter() {
        return null;
    }

    @Override
    public void getUserInfoSuccess(UserInfo userInfo) {

    }

    @Override
    public void getGiftSuccess(List<GiftDto> data) {

    }
}
