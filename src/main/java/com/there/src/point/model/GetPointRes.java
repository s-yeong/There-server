package com.there.src.point.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetPointRes {
    private String tid;
    private int cancle_amount;
    private int tax_free_amount;
}
