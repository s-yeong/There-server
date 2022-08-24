package com.there.src.like.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostLikeReq {

    private int emotion;
    private int postIdx;

}
