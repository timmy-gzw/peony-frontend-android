package com.tftechsz.common.widget.pop;

import android.content.Context;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.common.R;
import com.tftechsz.common.entity.FamilyInactiveDto;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

/**
 * 禁用弹窗
 */
public class FamilyMutePopWindow extends BaseBottomPop implements View.OnClickListener {
    private final Context mContext;
    private RecyclerView mRvFamilyInactive;
    protected CompositeDisposable mCompositeDisposable;

    public FamilyMutePopWindow(Context context) {
        super(context);
        mContext = context;
        setPopupGravity(Gravity.BOTTOM);
        initUI();
        setPopupFadeEnable(true);
    }

    private void initUI() {
        findViewById(R.id.cf_title).setVisibility(View.GONE);
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
        adapter.setOnItemClickListener((adapter1, view, position) -> {
            if(listener!=null)
                listener.muteUser(adapter.getData().get(position).type);
            dismiss();
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
            mCompositeDisposable = null;
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

        void muteUser(int type);
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }
}
