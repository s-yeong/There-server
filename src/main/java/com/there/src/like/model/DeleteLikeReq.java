package com.there.src.like.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteLikeReq {

    private int userIdx;
    private int postIdx;

}
