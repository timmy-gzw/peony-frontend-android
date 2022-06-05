package com.tftechsz.mine.widget.pop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;

import com.blankj.utilcode.util.ImageUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.hoko.blur.HokoBlur;
import com.hoko.blur.task.AsyncBlurTask;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.pop.BaseCenterPop;
import com.tftechsz.mine.R;
import com.tftechsz.mine.adapter.NobleItemDetailAdapter;
import com.tftechsz.mine.adapter.NobleItemDetailTitleAdapter;
import com.tftechsz.mine.databinding.NobleItemDetailBinding;
import com.tftechsz.mine.entity.NobleBean;
import com.tftechsz.mine.entity.NobleItemClickEvent;
import com.tftechsz.mine.utils.LoopTransformer;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

/**
 * 包 名 : com.tftechsz.mine.widget.pop
 * 描 述 : TODO
 */
public class NobleItemDetailPop extends BaseCenterPop implements OnItemClickListener, View.OnClickListener {

    private NobleItemDetailBinding mBind;
    private NobleBean bean;
    private NobleItemDetailTitleAdapter mTitleAdapter;
    private int pageSel, itemPos;
    private NobleItemDetailAdapter mItemDetailAdapter;
    private final Activity activity;

    public NobleItemDetailPop(Activity activity) {
        super(activity);
        this.activity = activity;
        initUI();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initUI() {
        mTitleAdapter = new NobleItemDetailTitleAdapter();
        mBind.recy.setAdapter(mTitleAdapter);
        mBind.recy.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mTitleAdapter.setOnItemClickListener(this);

        mItemDetailAdapter = new NobleItemDetailAdapter();
        mBind.vpLoop.setAdapter(mItemDetailAdapter);
        mBind.vpLoop.setOffscreenPageLimit(5);
        mBind.vpLoop.setPageTransformer(new LoopTransformer());
        mBind.vpLoop.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                itemPos = position;
                NobleBean.PrivilegeDTO privilegeDTO = bean.privilege.get(position);
                mBind.name.setText(privilegeDTO.name);
                mBind.tips.setText(privilegeDTO.is_heat == 1 ? String.format(privilegeDTO.tips, bean.grade.get(pageSel).heat).replace("\n", "") : privilegeDTO.tips.replace("\n", ""));
                Utils.logE(mItemDetailAdapter.getItem(position));
            }
        });
        setViewClickListener(this, mBind.root, mBind.botBtn, mBind.ivClose/*, mBind.cTop, mBind.cMid, mBind.cBot*/);

//        ViewGroup.LayoutParams lp = mBind.vpLoop.getLayoutParams();
//        lp.height = ScreenUtils.getScreenHeight() / 3;
//        mBind.vpLoop.setLayoutParams(lp);

        mBind.rlVp.setOnTouchListener((v, event) -> mBind.vpLoop.dispatchTouchEvent(event));
    }

    @Override
    protected View createPopupById() {
        View view = createPopupById(R.layout.noble_item_detail);
        mBind = DataBindingUtil.bind(view);
        return view;

    }

    public void setData(NobleBean bean) {
        this.bean = bean;
    }

    /**
     * @param pageSel  选中的贵族等级索引
     * @param position 卡片页索引
     * @param changeBg 是否修改背景
     */
    public void updateIndex(int pageSel, int position, boolean changeBg) {
        if (changeBg) {
            HokoBlur.with(mContext).radius(20)
                    .asyncBlur(activity.getWindow().getDecorView().getRootView(), new AsyncBlurTask.Callback() {
                        @Override
                        public void onBlurSuccess(Bitmap bitmap) {
                            mBind.root.setBackground(ImageUtils.bitmap2Drawable(bitmap));
                        }

                        @Override
                        public void onBlurFailed(Throwable error) {
                        }
                    });
        }

        mBind.vpLoop.postDelayed(() -> mBind.vpLoop.setCurrentItem(position, false), 0);

        this.pageSel = pageSel;
        this.itemPos = position;
        for (int i = 0; i < bean.grade.size(); i++) {
            NobleBean.GradeDTO gradeDTO = bean.grade.get(i);
            gradeDTO.setSel(false);
        }
        NobleBean.GradeDTO gradeDTO = bean.grade.get(pageSel);
        gradeDTO.setSel(true);
        mTitleAdapter.setList(bean.grade);

        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < bean.privilege.size(); i++) {
            NobleBean.PrivilegeDTO privilegeDTO = bean.privilege.get(i);
            String replace = "";
            if (!TextUtils.isEmpty(bean.window_privilege_icon)) {
                String privilege_icon = bean.window_privilege_icon;
                replace = privilege_icon.replace("{level}", String.valueOf(gradeDTO.id))
                        .replace("{index}", String.valueOf(privilegeDTO.id))
                        .replace("{lock}", gradeDTO.privilege.contains(privilegeDTO.id) ? "unlock" : "lock");
            }
            strings.add(replace);
        }

        if (mItemDetailAdapter.getItemCount() > 0) {
            for (int i = 0; i < mItemDetailAdapter.getItemCount(); i++) {
                mItemDetailAdapter.setData(i, strings.get(i));
            }
        } else {
            mItemDetailAdapter.setList(strings);
        }
        View childAt = mBind.vpLoop.getChildAt(0);
        if (childAt instanceof RecyclerView) {
            childAt.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }

        NobleBean.PrivilegeDTO privilegeDTO = bean.privilege.get(position);

        mBind.name.setText(privilegeDTO.name);
        mBind.tips.setText(privilegeDTO.is_heat == 1 ? String.format(privilegeDTO.tips, bean.grade.get(this.pageSel).heat).replace("\n", "") : privilegeDTO.tips.replace("\n", ""));
        //String name = TextUtils.isEmpty(gradeDTO.full_name) ? gradeDTO.name : gradeDTO.full_name;
        //mBind.botBtn.setText(String.format((gradeDTO.used == 1 ? "续费%s" : "开通%s"), name.replace("·", "")));
    }

    @Override
    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
        if (position == pageSel) {
            return;
        }
        updateIndex(position, itemPos, false);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.bot_btn) {
            RxBus.getDefault().post(new NobleItemClickEvent(pageSel, true));
            dismiss();
        } else if (/*id == R.id.root ||*/ id == R.id.iv_close) {
            dismiss();
        }
    }
}
