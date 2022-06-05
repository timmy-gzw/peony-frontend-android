package com.tftechsz.im.adapter;

import android.annotation.SuppressLint;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.tftechsz.im.R;
import com.tftechsz.im.model.dto.AirdropDto;

import org.jetbrains.annotations.NotNull;

public class AirdropAdapter extends BaseQuickAdapter<AirdropDto, BaseViewHolder> {
    private int checkPosition;

    public AirdropAdapter() {
        super(R.layout.item_airdrop);
        checkPosition = 0;
    }

    public void setCheckPositions(int p) {
        checkPosition = p;
        if(listener!=null)
            listener.getAirdropId(getData().get(p).id,p);
        notifyDataSetChanged();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, AirdropDto item) {
        View llPrice = baseViewHolder.getView(R.id.ll_price);
        RecyclerView recyclerView = baseViewHolder.getView(R.id.rv_short);
        recyclerView.setFocusable(false);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        AirdropItemAdapter adapter = new AirdropItemAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setList(item.gift);
        baseViewHolder.setText(R.id.tv_coin, item.coin + "")
                .setText(R.id.tv_num, item.num + "个礼物");
        if (checkPosition == baseViewHolder.getLayoutPosition()) {
            llPrice.setAlpha(1);
            recyclerView.setBackgroundResource(R.drawable.bg_choose_airdrop_radius10);
        } else {
            llPrice.setAlpha((float) 0.5);
            recyclerView.setBackgroundResource(R.drawable.bg_normal_airdrop_radius10);
        }
        GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {

            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                setCheckPositions(baseViewHolder.getLayoutPosition());
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
        recyclerView.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }


    public interface OnSelectListener {
        void getAirdropId(int id,int position);

    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }

}
