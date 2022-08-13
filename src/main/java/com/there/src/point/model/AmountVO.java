package com.there.src.point.model;

import lombok.Data;

@Data
public class AmountVO {
    private Integer total, tax_free, vat, point, discount;


    public AmountVO() {

    }
    public AmountVO(Integer total, Integer tax_free ,Integer vat, Integer point, Integer discount){

        this.total = total;
        this.tax_free = tax_free;
        this.vat = vat;
        this.point = point;
        this.discount = discount;
    }
}
