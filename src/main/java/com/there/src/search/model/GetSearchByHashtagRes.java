package com.there.src.search.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class GetSearchByHashtagRes {

    private int tagIdx;
    private String hashtag;
    private String postCount;

}
