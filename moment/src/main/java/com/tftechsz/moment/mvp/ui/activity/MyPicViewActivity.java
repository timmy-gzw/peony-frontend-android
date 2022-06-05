package com.tftechsz.moment.mvp.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.tftechsz.common.adapter.FragmentVpAdapter;
import com.tftechsz.common.base.BaseMvpActivity;
import com.tftechsz.common.base.BasePresenter;
import com.tftechsz.common.constant.Interfaces;
import com.tftechsz.common.entity.UserViewInfo;
import com.tftechsz.common.fragment.PicLoadFragment;
import com.tftechsz.moment.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 包 名 : com.tftechsz.moment.mvp.ui.activity
 * 描 述 : TODO
 */
public class MyPicViewActivity extends BaseMvpActivity {

    private TextView mTvNum;

    @Override
    public BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected int getLayout() {
        return R.layout.act_my_pic;
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void initView(Bundle savedInstanceState) {
        ViewPager viewPager = findViewById(R.id.viewpager);
        mTvNum = findViewById(R.id.tv_num);

        int picIndex = getIntent().getIntExtra(Interfaces.EXTRA_TREND_PIC_INDEX, 0);
        ArrayList<UserViewInfo> userViewInfos = getIntent().getParcelableArrayListExtra(Interfaces.EXTRA_TREND_PIC_LIST);
        List<Fragment> list = new ArrayList<>();
        if (userViewInfos != null && userViewInfos.size() > 0) {
            for (int i = 0; i < userViewInfos.size(); i++) {
                UserViewInfo userViewInfo = userViewInfos.get(i);
                list.add(PicLoadFragment.newInstance(i, userViewInfo.getUrl()));
            }
            viewPager.setAdapter(new FragmentVpAdapter(getSupportFragmentManager(), list));
            mTvNum.setText(String.format("1/%d", list.size()));
        }
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTvNum.setText(String.format("%1$d/%2$d", position + 1, list.size()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (picIndex < list.size()) {
            viewPager.setCurrentItem(picIndex);
        }
    }

}
