package com.there.src.post;

import com.there.config.BaseException;
import com.there.config.BaseResponse;
import com.there.src.post.model.PatchPostsRes;
import com.there.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
public class PostController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private PostProvider postProvider;
    private PostService postService;
    private final JwtService jwtService;

    @Autowired
    public PostController(PostProvider postProvider, PostService postService, JwtService jwtService) {
        this.postProvider = postProvider;
        this.postService = postService;
        this.jwtService = jwtService;
    }

    // 게시글 생성

    // 게시글 삭제
    @ResponseBody
    @PatchMapping("/{postIdx}")
    public BaseResponse<String> deletePosts(@PathVariable("postIdx") int postIdx) {

        try {
            postService.deletePost(postIdx);
            String result = "게시글 삭제를 성공했습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }



    // 개시굴 수정


}
