package com.tftechsz.im.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.opensource.svgaplayer.SVGADrawable;
import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.tftechsz.im.R;
import com.tftechsz.im.model.dto.AirdropBagDto;
import com.tftechsz.common.base.BaseApplication;
import com.tftechsz.common.utils.CountBackUtils;
import com.tftechsz.common.utils.TimeUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReceiveAirdropAdapter extends RecyclerView.Adapter<ReceiveAirdropAdapter.ViewHolder> {
    private final LayoutInflater mInflater;
    private List<AirdropBagDto> airDrop;
    private final SVGAParser svgaParser;

    public ReceiveAirdropAdapter(List<AirdropBagDto> airDrop) {
        this.airDrop = airDrop;
        this.mInflater = LayoutInflater.from(BaseApplication.getInstance());
        svgaParser = new SVGAParser(BaseApplication.getInstance());

    }

    @NonNull
    @Override
    public ReceiveAirdropAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_receive_airdrop, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiveAirdropAdapter.ViewHolder holder, int position) {
        String sex = "";
        if (airDrop.get(position).rule_type == 1) {
            sex = "(限男士)";
        } else if (airDrop.get(position).rule_type == 2) {
            sex = "(限女士)";
        }
        if (airDrop.get(position).status == 1 && airDrop.get(position).seconds == 0) {
            holder.tvOpen.setEnabled(true);
            holder.tvOpen.setText("抢空投" + sex);
            playAirdrop("airdrops_receive.svga",holder.svgaImageView);
        } else {
            playAirdrop("airdrops_send_small.svga",holder.svgaImageView);
            holder.countBackUtils = new CountBackUtils();
            holder.tvOpen.setEnabled(false);
            String finalSex = sex;
            holder.tvOpen.setBackgroundResource(R.drawable.bg_gray_ee_radius25);
            holder.tvOpen.setText(TimeUtils.timeParse(airDrop.get(position).seconds) + "后可抢");
            holder.countBackUtils.countBack(airDrop.get(position).seconds, new CountBackUtils.Callback() {
                @Override
                public void countBacking(long time) {
                    holder.tvOpen.setEnabled(false);
                    holder.tvOpen.setBackgroundResource(R.drawable.bg_gray_ee_radius25);
                    holder.tvOpen.setText(TimeUtils.timeParse(time) + "后可抢");
                }

                @Override
                public void finish() {
                    holder.tvOpen.setBackgroundResource(R.drawable.bg_airdrop_radius25);
                    holder.tvOpen.setText("抢空投" + finalSex);
                    holder.tvOpen.setEnabled(true);
                    playAirdrop("airdrops_receive.svga",holder.svgaImageView);
                }
            });
        }
        holder.tvOpen.setOnClickListener(v -> {
            if (listener != null)
                listener.openAirdrop(airDrop.get(position).gift_bag_id);
        });
    }


    @Override
    public int getItemCount() {
        return airDrop.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        SVGAImageView svgaImageView;
        TextView tvOpen;
        CountBackUtils countBackUtils;

        ViewHolder(View itemView) {
            super(itemView);
            svgaImageView = itemView.findViewById(R.id.svga_image);
            tvOpen = itemView.findViewById(R.id.tv_open);
        }
    }

    private void playAirdrop(String name,SVGAImageView imageView){
        svgaParser.decodeFromAssets(name, new SVGAParser.ParseCompletion() {

            @Override
            public void onError() {

            }

            @Override
            public void onComplete(@NotNull SVGAVideoEntity videoItem) {
                if (imageView != null) {
                    SVGADrawable drawable = new SVGADrawable(videoItem);
                    imageView.setImageDrawable(drawable);
                    imageView.startAnimation();
                }
            }
        },null);
    }


    public interface OnSelectListener {
        void openAirdrop(int airdropId);
    }

    public OnSelectListener listener;

    public void addOnClickListener(OnSelectListener listener) {
        this.listener = listener;
    }
}
