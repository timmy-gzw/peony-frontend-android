package com.tftechsz.mine.mvp.ui.fragment;

import android.os.Bundle;

import com.tftechsz.common.base.BaseMvpFragment;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.mine.R;

public class GuardFragment extends BaseMvpFragment {

    public static final String TYPE = "type";
    public static final String TYPE_WATCH = "watch";
    public static final String TYPE_FANS = "fans";

    public static GuardFragment newInstance(String type) {
        Bundle args = new Bundle();
        args.putString(TYPE, type);
        GuardFragment fragment = new GuardFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    public void initUI(Bundle savedInstanceState) {

    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_guard;
    }

    @Override
    protected void initData() {

    }
}
