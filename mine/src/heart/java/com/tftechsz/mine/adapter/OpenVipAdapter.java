package com.tftechsz.mine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.VipPrivilegeBean;

import java.util.List;

public class OpenVipAdapter extends RecyclerView.Adapter<OpenVipAdapter.ViewHolder> {
    private final LayoutInflater mInflater;
    private final List<VipPrivilegeBean> vipPrivilege;
    private final Context context;

    public OpenVipAdapter(Context context, List<VipPrivilegeBean> vipPrivilege) {
        this.context = context;
        this.vipPrivilege = vipPrivilege;
        this.mInflater = LayoutInflater.from(context);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_vip_open, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GlideUtils.loadRouteImage(context, holder.ivVipPer, vipPrivilege.get(position).img);
    }


    @Override
    public int getItemCount() {
        return vipPrivilege.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivVipPer;

        ViewHolder(View itemView) {
            super(itemView);
            ivVipPer = itemView.findViewById(R.id.iv_vip_per);
        }
    }

}
