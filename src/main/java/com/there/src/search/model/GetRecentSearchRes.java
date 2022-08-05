package com.there.src.search.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetRecentSearchRes {

    private int searchIdx;
    private int tagOrUserIdx;
    private String tagOrAccount;
    private String createdAt;

}
