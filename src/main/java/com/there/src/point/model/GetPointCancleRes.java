package com.there.src.point.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetPointCancleRes {
    private String aid, tid, cid, status, partner_order_id, partner_user_id,payment_method_type;
    private AmountVO amount;
    private String item_name, item_code, payload;
    private Integer quantity;
    private Date created_at, approved_at, cancled_at;

    public GetPointCancleRes() {
    }
    public GetPointCancleRes(AmountVO amount) {
        this.amount = amount;

    }

}
