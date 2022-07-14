package com.there.src.follow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FollowProvider {
    private final FollowDao followDao;

    @Autowired
    public FollowProvider(FollowDao followDao) {
        this.followDao = followDao;
    }

}
