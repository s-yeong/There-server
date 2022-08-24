package com.there.src.like.model;

import lombok.*;

@Data
@NoArgsConstructor
public class PostLikeReq {

    private int postIdx;
    private int emotion;

}
