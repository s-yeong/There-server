package com.there.src.search.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetSearchByAllRes {

    List<GetSearchByAccountRes> getSearchByAccount;
    List<GetSearchByHashtagRes> getSearchByHashtag;

}
