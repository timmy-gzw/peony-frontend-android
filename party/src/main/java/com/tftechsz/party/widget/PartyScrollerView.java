package com.tftechsz.party.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import com.tftechsz.common.Constants;
import com.tftechsz.common.bus.RxBus;
import com.tftechsz.common.event.CommonEvent;
import com.tftechsz.common.utils.Utils;
import com.tftechsz.common.widget.PartyLoopLayoutManager;
import com.tftechsz.party.R;
import com.tftechsz.party.adapter.PartyScrollerAdapter;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import static android.os.Looper.getMainLooper;

/**
 * 包 名 : com.tftechsz.party.widget
 * 描 述 : TODO
 */
public class PartyScrollerView extends LinearLayout {
    private final RecyclerView recyclerView;
    private final static int LOOP_RECYCLER_VIEW_MSG = 1000;
    private final static int LOOP_RECYCLER_VIEW_FINISH = 1001;
    private final static int LOOP_INTERVAL = 1000;
    private final static int CLOSE_TIME = 15000;
    private final PartyScrollerAdapter mAdapter;
    private int position = 0;
    private int size;
    DecelerateInterpolator decelerateInterpolator;
    Handler myHandler = new Handler(getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == LOOP_RECYCLER_VIEW_MSG) {
                size = mAdapter.getData().size();
                if (position + 1 < size) {//判断是否有下一次
                    position++;
                    recyclerView.smoothScrollToPosition(position);
                    myHandler.sendEmptyMessageDelayed(LOOP_RECYCLER_VIEW_MSG, LOOP_INTERVAL);
                } else {
                    myHandler.sendEmptyMessageDelayed(LOOP_RECYCLER_VIEW_FINISH, CLOSE_TIME);
                }
            } else if (msg.what == LOOP_RECYCLER_VIEW_FINISH) {
                RxBus.getDefault().post(new CommonEvent(Constants.NOTIFY_PARTY_SCROLLER_GONE));
            }
        }
    };

    public PartyScrollerView(Context context) {
        this(context, null);
    }

    public PartyScrollerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.scroller_party_view, this);
        recyclerView = findViewById(R.id.recy);
        PartyLoopLayoutManager verticalLoopLayoutManager = new PartyLoopLayoutManager(getContext());
        recyclerView.setLayoutManager(verticalLoopLayoutManager);
        mAdapter = new PartyScrollerAdapter();
        recyclerView.setAdapter(mAdapter);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        decelerateInterpolator = new DecelerateInterpolator();
        recyclerView.requestDisallowInterceptTouchEvent(true);
    }

    public void addWarnTips(String msg) {
        stopLoop();
        Utils.runOnUiThread(() -> {
            List<String> data = mAdapter.getData();
            data.add(msg);
            mAdapter.setList(data);
            myHandler.sendEmptyMessageDelayed(LOOP_RECYCLER_VIEW_MSG, LOOP_INTERVAL);
        });
    }


    public void startLoop() {
        stopLoop();
        if (size > 1 && myHandler != null) {
            myHandler.sendEmptyMessageDelayed(LOOP_RECYCLER_VIEW_MSG, LOOP_INTERVAL);
        }
    }


    public void stopLoop() {
        if (myHandler != null) {
            myHandler.removeMessages(0);
            myHandler.removeMessages(LOOP_RECYCLER_VIEW_MSG);
            myHandler.removeCallbacksAndMessages(null);
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

}
