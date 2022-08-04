package com.there.src.post;

import com.there.config.*;
import com.there.src.history.model.GetHistoryListRes;
import com.there.src.post.config.BaseException;
import com.there.src.post.config.BaseResponse;
import com.there.src.post.model.*;
import com.there.utils.JwtService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @ApiOperation(value="게시글 생성 API", notes="사진과 게시물에 대한 내용 반드시 입력")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
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

    @ApiOperation(value="게시글 수정 API", notes="")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
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

    @ApiOperation(value="게시글 삭제 API", notes="실제 삭제가 아닌 status 변경")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
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

    @ApiOperation(value="게시글 랜덤 조회 API", notes="")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
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

    @ApiOperation(value="게시글 생성 API", notes="사진과 게시물에 대한 내용 반드시 입력")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @GetMapping("ranking")
    public BaseResponse<List<GetPostListRes>> getRankingPostList(){
        try {
            List<GetPostListRes> getPostListRes = postProvider.retrieveRankingPosts();
            return new BaseResponse<>(getPostListRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 감정별 게시글 리스트 조회 API
     * /posts/emotion/:emotion
     */
    @ApiOperation(value="게시글 감정별 조회 API", notes="추후에 구현 필요")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @GetMapping("emotion/{emotion}")
    public BaseResponse<List<GetPostListRes>> getEmotionPostList(@PathVariable("emotion") int emotion) {
        try {
            List<GetPostListRes> getPostListRes = postProvider.retrievePostList(emotion);
            return new BaseResponse<>(getPostListRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

}
