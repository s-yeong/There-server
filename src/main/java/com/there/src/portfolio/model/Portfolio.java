package com.there.src.portfolio.model;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class Portfolio {

    private int portfolioIdx;
    private int postIdx;
    private String title;

}
