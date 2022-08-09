package com.there.src.point.model;

import lombok.Data;

@Data
public class AmountVO {
    private Integer total;

    public AmountVO() {

    }
    public AmountVO(Integer total ){
        this.total = total;
    }
}
