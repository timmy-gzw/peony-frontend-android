package com.tftechsz.common.widget.pop;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tftechsz.common.R;
import com.tftechsz.common.adapter.TopicAdapter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class TopicPop extends BaseBottomPop{
    List<String> data;
    TopicItemOnClickListener itemOnClickListener;

    public TopicPop(Context context) {
        super(context);
    }

    public TopicPop(Context context, List<String> data) {
        super(context);
        this.data = data;
        init();
    }

    private void init() {
        setHeight(1000);
        RecyclerView rv = findViewById(R.id.rv_topic);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(linearLayoutManager);
        TopicAdapter topicAdapter = new TopicAdapter(1);
        rv.setAdapter(topicAdapter);
        //打乱顺序
        if(data != null) {
            Collections.shuffle(data);
        }
        topicAdapter.setList(data);
        topicAdapter.setOnItemClickListener((adapter, view, position) -> {
            dismiss();
            if(null != itemOnClickListener){
                itemOnClickListener.onTopicItemClick(topicAdapter.getItem(position));
            }
        });
    }

    @Override
    protected View createPopupById() {
        return createPopupById(R.layout.pop_topic);
    }


    public interface TopicItemOnClickListener{
        void onTopicItemClick(String text);
    }

    public void setTopicItemClickListener(TopicItemOnClickListener listener){
        this.itemOnClickListener = listener;
    }
}
