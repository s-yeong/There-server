package com.there.src.post.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetTotalPostRes {
    private GetPostRes getPostRes;
    private List<GetPostTagRes> getPostTagist;
}
