package com.tftechsz.mine.entity.dto;

import java.util.ArrayList;

public class ExchangeRecord {

    private String date;
    private String footer;
    private ArrayList<ExchangeRecordDto> data;

    public ExchangeRecord(String date, String footer, ArrayList<ExchangeRecordDto> data) {
        this.date = date;
        this.footer = footer;
        this.data = data;
    }

    public String getHeader() {
        return date;
    }

    public void setHeader(String header) {
        this.date = date;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public ArrayList<ExchangeRecordDto> getChildren() {
        return data;
    }

    public void setChildren(ArrayList<ExchangeRecordDto> children) {
        this.data = children;
    }
}
