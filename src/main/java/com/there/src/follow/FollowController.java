package com.there.src.follow;


import com.there.config.BaseException;
import com.there.config.BaseResponse;
import com.there.src.follow.model.PostFollowReq;
import com.there.src.follow.model.PostFollowRes;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api
@RestController
@RequestMapping("/follow")
public class FollowController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final FollowService followService;
    private final FollowProvider followProvider;

    @Autowired
    public FollowController(FollowProvider followProvider, FollowService followService) {
        this.followProvider = followProvider;
        this.followService = followService;
    }

    // 팔로우
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostFollowRes> follow(@RequestBody PostFollowReq postFollowReq) {
        try {
            PostFollowRes postFollowRes = followService.follow(postFollowReq);
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