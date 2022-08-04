package com.there.src.portfolio.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class GetPortfolioListRes {

    private int portfolioIdx;
    private String title;
    private int Post_count;

}
