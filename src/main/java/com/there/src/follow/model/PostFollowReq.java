package com.there.src.follow.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PostFollowReq {

    private int followerIdx;
    private int followeeIdx;

    public PostFollowReq(){

    }

    public PostFollowReq(int followerIdx, int followeeIdx) {
        this.followerIdx = followerIdx;
        this.followeeIdx = followeeIdx;
    }
}
