package com.there.src.like;

import com.there.config.BaseException;
import com.there.config.BaseResponse;
import com.there.src.like.model.*;
import com.there.utils.JwtService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api
@RestController
@RequestMapping("/likes")
public class LikeController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final LikeProvider likeProvider;
    private final LikeService likeService;
    private final JwtService jwtService;

    @Autowired
    public LikeController(LikeProvider likeProvider, LikeService likeService, JwtService jwtService) {

        this.likeProvider = likeProvider;
        this.likeService = likeService;
        this.jwtService = jwtService;
    }

    // 좋아요 및 감정표현 생성
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostLikeRes> createLikes(@RequestBody PostLikeReq postLikeReq) {
        try {
            PostLikeRes postLikeRes = likeService.createLikes(postLikeReq);
            return new BaseResponse<>(postLikeRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 좋아요 및 감정표현 조회
    @ResponseBody
    @GetMapping("/{postIdx}")
    public BaseResponse<List<GetLikeRes>> selectLikes(@PathVariable("postIdx")int postIdx) {
        try {
            List<GetLikeRes> getLikes = likeProvider.retrieveLikes(postIdx);
            return new BaseResponse<>(getLikes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    // 좋아요 및 감정표현 수정
    @ResponseBody
    @PatchMapping("/update")
    public BaseResponse<String> updateLikes(@RequestBody PatchLikeReq patchLikeReq) {

        try {
            likeService.updateLikes(patchLikeReq.getUserIdx(), patchLikeReq.getPostIdx(), patchLikeReq.getEmotion());
            String result = "변경 완료";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }

    // 좋아요 및 감정표현 삭제
    @ResponseBody
    @DeleteMapping("/delete")
    public BaseResponse<String> deleteLikes(@RequestBody DeleteLikeReq deleteLikeReq) {

        try {
            likeService.deleteLikes(deleteLikeReq);
            String result = "좋아요 및 감정표현 삭제 완료";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }



}
