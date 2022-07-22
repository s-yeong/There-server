package com.there.src.comment;


import com.there.src.comment.config.BaseException;
import com.there.src.comment.config.BaseResponse;
import com.there.src.comment.model.GetCommentListRes;
import com.there.src.comment.model.PatchCommentReq;
import com.there.src.comment.model.PostCommentReq;
import com.there.src.comment.model.PostCommentRes;
import com.there.utils.JwtService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.there.src.comment.config.BaseResponseStatus.*;

@Api
@RestController
@RequestMapping("/comments")
public class CommentController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CommentProvider commentProvider;
    private final CommentService commentService;
    private final JwtService jwtService;

    @Autowired
    public CommentController(CommentProvider commentProvider, CommentService commentService, JwtService jwtService) {
        this.commentProvider = commentProvider;
        this.commentService = commentService;
        this.jwtService = jwtService;
    }

    /**
     * 댓글 생성 API
     * comments/:postIdx
     */
    @ResponseBody
    @PostMapping("/{postIdx}/{userIdx}")
    public BaseResponse<PostCommentRes> createComment
    (@PathVariable("postIdx") int postIdx, @PathVariable("userIdx") int userIdx,
     @RequestBody PostCommentReq postCommentReq) throws com.there.config.BaseException {
        try {

            int userIdxByJwt = jwtService.getUserIdx();

            if (userIdxByJwt != userIdx) return new BaseResponse<>(INVALID_USER_JWT);
            if (postCommentReq.getContent() == null) return new BaseResponse<>(COMMENTS_EMPTY_CONTENT);

            PostCommentRes postCommentRes = commentService.createComment(postIdx, userIdx, postCommentReq);
            return new BaseResponse<>(postCommentRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 댓글 리스트 조회 API
     * comments/:commentIdx
     */
    @ResponseBody
    @GetMapping("{postIdx}")
    public BaseResponse<List<GetCommentListRes>> getCommentList(@PathVariable("postIdx") int postIdx)
            throws com.there.config.BaseException {
        List<GetCommentListRes> getCommentListResList = commentProvider.retrieveComment(postIdx);
        return new BaseResponse<>(getCommentListResList);

    }

    /**
     * 댓글 삭제 API
     * comments/:commentIdx/status
     */
    @ResponseBody
    @PatchMapping("/{commentIdx}/status")
    public BaseResponse<String> deleteComment(@PathVariable("commentIdx") int commentIdx)
            throws com.there.config.BaseException, BaseException {
        int userIdxByJwt = jwtService.getUserIdx();
        commentService.deleteComment(userIdxByJwt, commentIdx);

        String result = "댓글이 삭제되었습니다. ";
        return new BaseResponse<>(result);
    }

    /**
     * 댓글 수정 API
     * comments/change/:commentIdx
     */
    @ResponseBody
    @PatchMapping("change/{commentIdx}")
    public BaseResponse<String> updateComment(@PathVariable("commentIdx") int commentIdx,
           @RequestBody PatchCommentReq patchCommentReq) throws com.there.config.BaseException {

        int userIdxByJwt = jwtService.getUserIdx();
        try {
            commentService.updateComment(commentIdx, patchCommentReq);
            String result = "댓글 수정을 완료하였습니다. ";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
