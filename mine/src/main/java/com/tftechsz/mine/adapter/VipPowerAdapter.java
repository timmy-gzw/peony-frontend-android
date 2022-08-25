package com.tftechsz.mine.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.utils.GlideUtils;
import com.tftechsz.mine.R;
import com.tftechsz.mine.entity.VipPrivilegeBean;

import java.util.List;

public class VipPowerAdapter extends RecyclerView.Adapter<VipPowerAdapter.ViewHolder> {
    private final LayoutInflater mInflater;
    private final List<VipPrivilegeBean> vipPrivilege;
    private final Context context;

    public VipPowerAdapter(Context context, List<VipPrivilegeBean> vipPrivilege) {
        this.context = context;
        this.vipPrivilege = vipPrivilege;
        this.mInflater = LayoutInflater.from(BaseApplication.getInstance());

    }

    @NonNull
    @Override
    public VipPowerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_vip_power, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VipPowerAdapter.ViewHolder holder, int position) {
        GlideUtils.loadImage(context, holder.ivVipPer, vipPrivilege.get(position).img);
        holder.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onClick();
            }
        });

    }


    @Override
    public int getItemCount() {
        return vipPrivilege.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivVipPer;
        ImageView ivClose;

        ViewHolder(View itemView) {
            super(itemView);
            ivVipPer = itemView.findViewById(R.id.iv_vip_per);
            ivClose = itemView.findViewById(R.id.iv_close);

        }
    }


    public interface OnSelectListener {
        void onClick();
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }

}
