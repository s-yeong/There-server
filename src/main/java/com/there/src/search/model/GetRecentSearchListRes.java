package com.there.src.search.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetRecentSearchListRes {

    private int searchIdx;
    private String content;
//    private GetSearchByAccountRes getSearchByAccountRes = null;
//    private GetSearchByHashtagRes getSearchByHashtagRes = null;

}
