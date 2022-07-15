package com.there.src.like.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Like {

    private int userIdx;
    private int postIdx;
    private int emotion;

}
