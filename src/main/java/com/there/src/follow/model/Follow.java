package com.there.src.follow.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Follow {
    private int followIdx;
    private int followerIdx;
    private int followeeIdx;
}
