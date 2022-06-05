package com.tftechsz.party.entity.dto;

import java.util.List;

public class NoticeRankDto {
    public List<WatcherRankDto> rank;
    public int hour_rank;  //当前排名
    public String hour_rank_before_diff;  //和上一名距离
    public String volume;
    public String heat;
    public int apply_count;
}
