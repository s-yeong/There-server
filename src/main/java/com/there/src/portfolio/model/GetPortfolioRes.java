package com.there.src.portfolio.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class GetPortfolioRes {

    private int contentIdx;
    private int postIdx;
    private String imgUrl;

}
