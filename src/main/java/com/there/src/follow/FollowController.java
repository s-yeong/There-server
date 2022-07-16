package com.there.src.follow;


import com.there.src.user.config.BaseException;
import com.there.src.user.config.BaseResponse;
import com.there.src.follow.model.PostFollowReq;
import com.there.src.follow.model.PostFollowRes;
import com.there.utils.JwtService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.there.src.user.config.BaseResponseStatus.*;
import static com.there.src.user.config.BaseResponseStatus.INVALID_USER_JWT;

@RestController
@RequestMapping("/follow")
public class FollowController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final FollowService followService;
    private final FollowProvider followProvider;
    private final JwtService jwtService;

    @Autowired
    public FollowController(FollowProvider followProvider, FollowService followService, JwtService jwtService) {
        this.followProvider = followProvider;
        this.followService = followService;
        this.jwtService = jwtService;
    }

    // 팔로우
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostFollowRes> follow(@RequestBody PostFollowReq postFollowReq) {
        try {
            int userIdxByJwt = jwtService.getUserIdx();

            PostFollowRes postFollowRes = followService.follow(userIdxByJwt, postFollowReq);
            return new BaseResponse<>(postFollowRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 언팔로우
    @ResponseBody
    @PatchMapping("/{followIdx}/status")
    public BaseResponse<String> unfollow(@PathVariable("followIdx")int followIdx){
        try {
            followService.unfollow(followIdx);
            String result = "팔로우 취소되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}