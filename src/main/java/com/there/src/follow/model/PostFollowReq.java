package com.there.src.follow.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PostFollowReq {

    private int followerIdx;


    public PostFollowReq(){

    }

    public PostFollowReq(int followerIdx) {
        this.followerIdx = followerIdx;

    }
}
