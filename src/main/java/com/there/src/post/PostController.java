package com.there.src.post;

import com.there.src.post.config.BaseException;
import com.there.src.post.config.BaseResponse;
import com.there.src.post.model.*;
import com.there.utils.JwtService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.there.src.post.config.BaseResponseStatus.*;

@Api
@RestController
@RequestMapping("/posts")
public class PostController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final PostProvider postProvider;
    private final PostService postService;
    private final JwtService jwtService;

    @Autowired
    public PostController(PostProvider postProvider, PostService postService, JwtService jwtService) {
        this.postProvider = postProvider;
        this.postService = postService;
        this.jwtService = jwtService;
    }

    /**
    * 게시글 생성 API
    * posts/users/:userIdx
    */
    @ResponseBody
    @PostMapping("/users/{userIdx}")
    public BaseResponse<PostPostsRes> createPosts
    (@PathVariable("userIdx")int userIdx ,@RequestBody PostPostsReq postPostsReq) throws com.there.config.BaseException {
        try {

            int userIdxByJwt = jwtService.getUserIdx();

            if (userIdxByJwt != userIdx) return new BaseResponse<>(INVALID_USER_JWT);
            if (postPostsReq.getImgUrl() == null) return new BaseResponse<>(EMPTY_IMGURL);
            if (postPostsReq.getContent() == null) return new BaseResponse<>(EMPTY_CONTENT);

            PostPostsRes postPostsRes = postService.createPosts(userIdx, postPostsReq);
            return new BaseResponse<>(postPostsRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 게시글 수정 API
     * posts/{postIdx}/users/:userIdx
     */
    @ResponseBody
    @PatchMapping("change/{postIdx}/users/{userIdx}")
    public BaseResponse<String> updatePosts
    (@PathVariable("postIdx")int postIdx, @PathVariable("userIdx")int userIdx, @RequestBody PatchPostsReq patchPostsReq) throws com.there.config.BaseException {

        int userIdxByJwt = jwtService.getUserIdx();

        if (userIdxByJwt != userIdx) return new BaseResponse<>(INVALID_USER_JWT);

        try {
            postService.updatePosts(postIdx, patchPostsReq);
            String result = "게시글 수정을 성공했습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 게시글 삭제 API
     * posts/{postIdx}/users/:userIdx
     */
    @ResponseBody
    @PatchMapping("deletion/{postIdx}/users/{userIdx}")
    public BaseResponse<String> deletePosts
    (@PathVariable("postIdx") int postIdx, @PathVariable("userIdx") int userIdx) throws com.there.config.BaseException {

        int userIdxByJwt = jwtService.getUserIdx();

        if (userIdxByJwt != userIdx) return new BaseResponse<>(INVALID_USER_JWT);

        try {
            postService.deletePosts(postIdx);
            String result = "게시글 삭제를 성공했습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 무작위(랜덤) 게시글 리스트 조회 API
     * /posts/random
     */
    @ResponseBody
    @GetMapping("random")
    public BaseResponse<List<GetPostListRes>> getRandomPostList(){
        try {
            List<GetPostListRes> getPostListRes = postProvider.retrievePosts();
            return new BaseResponse<>(getPostListRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 인기글, 내가 팔로우한 구독자의 게시글 리스트 조회 API
     * /posts/rankingAndfollowerPostList
     */
    @ResponseBody
    @GetMapping("rankingAndfollowerPostList")
    public BaseResponse<Map<String, List<GetPostListRes>>>getRankingAndFollowerPostList() throws com.there.config.BaseException{
        try {
            int userIdxByJwt = jwtService.getUserIdx();

            Map<String, List<GetPostListRes>> getPostListRes = new HashMap<>();
            getPostListRes.put("인기글 리스트", postProvider.retrieveRankingPosts());
            getPostListRes.put("팔로우 게시글 리스트",postProvider.retrieveFollowerPosts(userIdxByJwt));


            return new BaseResponse<>(getPostListRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}