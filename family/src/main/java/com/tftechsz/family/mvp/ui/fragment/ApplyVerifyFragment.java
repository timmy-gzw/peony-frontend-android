package com.tftechsz.family.mvp.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.family.R;
import com.tftechsz.family.api.FamilyApiService;
import com.tftechsz.family.entity.dto.FamilyApplyDto;
import com.tftechsz.common.Constants;
import com.tftechsz.common.base.BaseListFragment;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.http.BaseResponse;
import com.tftechsz.common.http.ResponseObserver;
import com.tftechsz.common.http.RetrofitManager;
import com.tftechsz.common.other.GlobalDialogManager;
import com.tftechsz.common.utils.ARouterUtils;
import com.tftechsz.common.utils.ClickUtil;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.common.utils.RxUtil;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Flowable;

/**
 * 审核申请
 */
public class ApplyVerifyFragment extends BaseListFragment<FamilyApplyDto> {

    public static final String TYPE = "type";
    public static final String FAMILY_ID = "family_id";
    public static final String TYPE_JOIN = "join";   //加入列表
    public static final String TYPE_LEAVE = "leave";   //离开
    public String mType;
    private int mFamilyId;

    private FamilyApiService service;

    public static ApplyVerifyFragment newInstance(String type, int familyId) {
        Bundle args = new Bundle();
        args.putString(TYPE, type);
        args.putInt(FAMILY_ID, familyId);
        ApplyVerifyFragment fragment = new ApplyVerifyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Flowable setNetObservable() {
        mType = getArguments().getString(TYPE);
        mFamilyId = getArguments().getInt(FAMILY_ID);
        if (null == service)
            service = new RetrofitManager().createFamilyApi(FamilyApiService.class);
        return service.applyList(mPage, mFamilyId, mType);
    }


    /**
     * 同意加入
     */
    private void acceptJoin(int applyId, int position) {
        if (null == service)
            service = new RetrofitManager().createFamilyApi(FamilyApiService.class);
        mCompositeDisposable.add(service.acceptJoin(applyId)
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        GlobalDialogManager.getInstance().dismiss();
                        if (response.getData()) {
                            //onRefresh();
                            for (int i = 0; i < adapter.getItemCount(); i++) {
                                FamilyApplyDto item = adapter.getItem(i);
                                if (applyId == item.apply_id) {
                                    item.status = 1;
                                    adapter.setData(i, item);
                                    break;
                                }
                            }
                            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_FAMILY_INFO));
                            toastTip("操作成功");
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        GlobalDialogManager.getInstance().dismiss();
                        if (code == 1002) {
                            adapter.getData().get(position).status = 2;
                            adapter.notifyItemChanged(position);
                        }
                    }
                }));
    }


    /**
     * 同意离开
     */
    private void acceptLeave(int applyId) {
        if (null == service)
            service = new RetrofitManager().createFamilyApi(FamilyApiService.class);
        mCompositeDisposable.add(service.acceptLeave(applyId)
                .compose(RxUtil.applySchedulers()).subscribeWith(new ResponseObserver<BaseResponse<Boolean>>() {
                    @Override
                    public void onSuccess(BaseResponse<Boolean> response) {
                        GlobalDialogManager.getInstance().dismiss();
                        if (response.getData()) {
                            mPage = 1;
                            onRefresh();
                            toastTip("操作成功");
                            RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_UPDATE_FAMILY_INFO));
                        }
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        super.onFail(code, msg);
                        GlobalDialogManager.getInstance().dismiss();
                    }
                }));
    }


    /**
     * 通知更新
     */
    private void initBus() {
        mCompositeDisposable.add(RxBus.getDefault().toObservable(CommonEvent.class)
                .compose(this.bindToLifecycle())
                .subscribe(
                        event -> {
                            if (event.type == Constants.NOTIFY_UPDATE_APPLY_LIST) {
                                onRefresh();
                            }
                        }
                ));
    }


    @Override
    public RecyclerView.LayoutManager setLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    @Override
    public void setData(List<FamilyApplyDto> datas, int page) {
        super.setData(datas, page);
        adapter.addChildClickViewIds(R.id.tv_accept);
        adapter.setOnItemChildClickListener((adapter1, view, position) -> {
            if (view.getId() == R.id.tv_accept) {
                if (!ClickUtil.canOperate()) {
                    return;
                }
                GlobalDialogManager.getInstance().show(mActivity.getFragmentManager(), "正在加载中,请稍后...");
                if (TextUtils.equals(mType, TYPE_JOIN)) {
                    acceptJoin(adapter.getData().get(position).apply_id, position);
                } else {
                    acceptLeave(adapter.getData().get(position).apply_id);
                }
            }
        });
        adapter.setOnItemClickListener((adapter1, view, position) -> ARouterUtils.toMineDetailActivity(String.valueOf(adapter.getData().get(position).user_id)));
    }


    @Override
    public int setItemLayoutRes() {
        return R.layout.item_apply_verify;
    }

    @Override
    public void bingViewHolder(BaseViewHolder helper, FamilyApplyDto item, int position) {
        TextView tvAccept = helper.getView(R.id.tv_accept);
        tvAccept.setEnabled(false);
        helper.setText(R.id.tv_name, item.nickname);
        GlideUtils.loadRoundImage(getActivity(), helper.getView(R.id.iv_avatar), item.icon);
        // status; // 申请状态（0:等待通过，1:已经通过，2:已经忽略）
        tvAccept.setBackgroundResource(R.color.transparent);
        tvAccept.setTextColor(Color.parseColor("#999999"));
        if (TextUtils.equals(mType, TYPE_JOIN)) {
            setContent(tvAccept, item.status, "接受");
        } else {
            setContent(tvAccept, item.status, "同意");
        }
        if (item.status == 0)
            tvAccept.setEnabled(true);
    }

    private void setContent(TextView textView, int status, String content) {
        if (status == 0) {
            textView.setText(content);
            textView.setBackgroundResource(R.drawable.bg_family_add);
            textView.setTextColor(Color.parseColor("#CFB78A"));
        } else if (status == 1) {
            textView.setText("已" + content);
        } else {
            textView.setText("已忽略");
        }
    }

    @Override
    public String setEmptyContent() {
        return "暂无数据";
    }

    @Override
    public void initUI(Bundle savedInstanceState) {
        initBus();
    }

    @Override
    protected int getLayout() {
        return R.layout.base_list;
    }

    @Override
    protected void initData() {

    }
}
