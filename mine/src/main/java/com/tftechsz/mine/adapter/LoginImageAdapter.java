package com.tftechsz.mine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.tftechsz.mine.R;

import org.jetbrains.annotations.NotNull;

public class LoginImageAdapter extends RecyclerView.Adapter<LoginImageAdapter.BaseViewHolder> {
    private final Context mContext;


    public LoginImageAdapter(Context context) {
        this.mContext = context;
    }

    @NotNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_login_image, parent, false);
        return new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull BaseViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    static class BaseViewHolder extends RecyclerView.ViewHolder {

        public BaseViewHolder(View itemView) {
            super(itemView);
        }
    }


}
