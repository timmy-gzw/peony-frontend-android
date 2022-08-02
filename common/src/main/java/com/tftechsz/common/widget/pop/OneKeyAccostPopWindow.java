package com.tftechsz.common.widget.pop;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.NetworkUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.netease.nim.uikit.common.ChatMsg;
import com.tftechsz.common.R;
import com.tftechsz.common.adapter.OneKeyAccostAdapter;
import com.tftechsz.common.entity.AccostPopupDto;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.PublicService;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.iservice.UserProviderService;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.CommonUtil;
import com.tftechsz.common.utils.RxUtil;
import com.tftechsz.common.utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

/**
 * 包 名 : com.tftechsz.common.widget.pop
 * 描 述 : 一键搭讪
 */
public class OneKeyAccostPopWindow extends BaseCenterPop implements OnItemClickListener, View.OnClickListener {

    private OneKeyAccostAdapter mAdapter;
    private CompositeDisposable mCompositeDisposable;
    private PublicService mUserService;
    UserProviderService userService;
    private TextView mAcbtn;
    private int mode;   // 1-一键搭讪 2-一键喜欢

    public OneKeyAccostPopWindow(Context context) {
        super(context);
        setOutSideDismiss(false);
        initUI();
    }

    @Override
    public boolean onDispatchKeyEvent(KeyEvent event) {
        return true;
    }

    private void initUI() {
        mUserService = RetrofitManager.getInstance().createUserApi(PublicService.class);
        mCompositeDisposable = new CompositeDisposable();
        userService = ARouter.getInstance().navigation(UserProviderService.class);

        RecyclerView recy = findViewById(R.id.recy);
        recy.setLayoutManager(new GridLayoutManager(mContext, 3));
        mAdapter = new OneKeyAccostAdapter();
        recy.setAdapter(mAdapter);
        mAdapter.onAttachedToRecyclerView(recy);
        mAdapter.setOnItemClickListener(this);

        mAcbtn = findViewById(R.id.accost_btn);
        findViewById(R.id.ll_btn).setOnClickListener(this);
        findViewById(R.id.del).setOnClickListener(this);
        mAcbtn.setText(CommonUtil.isBtnTextPop(userService) ? "一键喜欢" : "一键搭讪");
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_one_key_accost);
    }

    @Override
    public void onItemClick(@NonNull @NotNull BaseQuickAdapter<?, ?> adapter, @NonNull @NotNull View view, int position) {
        ImageView ivCheck = (ImageView) mAdapter.getViewByPosition(position, R.id.iv_check);
        if (ivCheck != null) {
            boolean isSelected = mAdapter.getItem(position).is_selected;
            if (isSelected) {
                ivCheck.setImageResource(R.mipmap.ic_check_normal);
            } else {
                ivCheck.setImageResource(R.mipmap.ic_check_selector);
            }
            mAdapter.getItem(position).is_selected = !isSelected;
        }
    }

    public void setUserList(List<ChatMsg.AccostPopup> list) {
//        mCompositeDisposable.add(mUserService.getAccostModel().compose(RxUtil.applySchedulers())
//                .subscribeWith(new ResponseObserver<BaseResponse<AccostModeDto>>() {
//                    @Override
//                    public void onSuccess(BaseResponse<AccostModeDto> resp) {
//                        if (resp.getData() != null) {
//                            mode = resp.getData().mode;
//                            mAcbtn.setText(mode == 2 ? "一键喜欢" : "一键搭讪");
//                        }
//                    }
//                }));

        mAdapter.setNewInstance(list);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_btn) { //一键搭讪
            if (!ClickUtil.canOperate()) {
                return;
            }
            List<Integer> ids = new ArrayList<>();
            for (int i = 0; i < mAdapter.getItemCount(); i++) {
                ChatMsg.AccostPopup item = mAdapter.getItem(i);
                if (item.is_selected) {
                    ids.add(item.user_id);
                }
            }
            if (ids.size() == 0) {
                dismiss();
                return;
            }

            if (!NetworkUtils.isConnected()) {
                Utils.toast(R.string.net_error);
                return;
            }
            mCompositeDisposable.add(mUserService.accostPopup(JSON.toJSONString(ids)).compose(RxUtil.applySchedulers())
                    .subscribeWith(new ResponseObserver<BaseResponse<List<AccostPopupDto>>>() {
                        @Override
                        public void onSuccess(BaseResponse<List<AccostPopupDto>> list) {
                            dismiss();
                            Utils.toast(CommonUtil.isBtnTextPop(userService) ? "一键喜欢成功" : "一键搭讪成功");
                            if (list.getData() != null) {
                                for (int i = 0; i < list.getData().size(); i++) {
                                    AccostPopupDto accostPopupDto = list.getData().get(i);
                                    CommonUtil.sendAccostGirlBoy(userService, accostPopupDto.from_user_id, accostPopupDto.msg_data, 0);
                                }
                            }
                        }
                    }));
        } else if (id == R.id.del) {
            dismiss();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
        }
    }
}
