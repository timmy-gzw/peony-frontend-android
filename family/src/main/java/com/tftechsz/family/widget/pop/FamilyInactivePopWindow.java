package com.tftechsz.family.widget.pop;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.family.R;
import com.tftechsz.common.entity.FamilyInactiveDto;
import com.tftechsz.common.widget.pop.BaseBottomPop;
import com.tftechsz.common.widget.pop.CustomPopWindow;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

/**
 * 清楚不活跃弹窗
 */
public class FamilyInactivePopWindow extends BaseBottomPop implements View.OnClickListener {
    private final Activity mContext;
    private RecyclerView mRvFamilyInactive;
    private LinearLayout mLlCancel;
    protected CompositeDisposable mCompositeDisposable;
    private int mRoleId;

    public FamilyInactivePopWindow(Activity context) {
        super(context);
        mContext = context;
        setPopupGravity(Gravity.BOTTOM);
        initUI();
        setPopupFadeEnable(true);
    }

    private void initUI() {
        mLlCancel = findViewById(R.id.ll_cancel);
        findViewById(R.id.tv_cancel).setOnClickListener(this);
        mRvFamilyInactive = findViewById(R.id.rv_inactive);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mRvFamilyInactive.setLayoutManager(layoutManager);
        mCompositeDisposable = new CompositeDisposable();
    }

    /**
     * 加载数据
     */
    public void initData(List<FamilyInactiveDto> data) {
        FamilyInactiveAdapter adapter = new FamilyInactiveAdapter(data);
        mRvFamilyInactive.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull @NotNull BaseQuickAdapter<?, ?> adapter1, @NonNull @NotNull View view, int position) {
                CustomPopWindow customPopWindow = new CustomPopWindow(mContext);
                customPopWindow.setContent("确认清理"+adapter.getData().get(position).desc+"的家族成员吗？");
                customPopWindow.addOnClickListener(new CustomPopWindow.OnSelectListener() {
                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onSure() {
                        if(listener!=null)
                            listener.clearInactive(adapter.getData().get(position).type);
                    }
                });
                customPopWindow.showPopupWindow();
                dismiss();
            }
        });

    }


    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_clear_inactive);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
         if (id == R.id.tv_cancel) {
            dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mCompositeDisposable != null && !mCompositeDisposable.isDisposed()){
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
        }
    }


    static class FamilyInactiveAdapter extends BaseQuickAdapter<FamilyInactiveDto, BaseViewHolder> {

        public FamilyInactiveAdapter(@Nullable List<FamilyInactiveDto> data) {
            super(R.layout.item_family_setting, data);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder helper, FamilyInactiveDto item) {
            helper.setText(R.id.tv_content, item.desc);
            helper.setVisible(R.id.view, helper.getLayoutPosition() != getData().size() - 1);

        }
    }


    public interface OnSelectListener {

        //移除用户
        void clearInactive(int type);
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }
}
