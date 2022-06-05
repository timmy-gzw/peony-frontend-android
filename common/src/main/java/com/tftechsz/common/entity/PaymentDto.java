package com.tftechsz.common.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 包 名 : com.tftechsz.common.entity
 * 描 述 : TODO
 */
public class PaymentDto implements Serializable {
    public List<RechargeDto> payment;
    public String first_pay_desc;
    public Branch branch;

    public static class Branch {
        public String url;
        public int id;
    }
}
