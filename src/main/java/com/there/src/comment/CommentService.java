package com.there.src.comment;


import com.there.src.comment.config.BaseException;
import com.there.src.comment.model.PostCommentReq;
import com.there.src.comment.model.PostCommentRes;
import com.there.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.there.src.comment.config.BaseResponseStatus.*;

@Service
public class CommentService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CommentDao commentDao;

    private final CommentProvider commentProvider;
    private final JwtService jwtService;

    @Autowired
    public CommentService(CommentDao commentDao, CommentProvider commentProvider, JwtService jwtService){
        this.commentDao = commentDao;
        this.commentProvider = commentProvider;
        this.jwtService = jwtService;
    }

    // 댓글 작성
    public PostCommentRes createComment(int postIdx, int userIdx, PostCommentReq postCommentReq) throws BaseException{
        try {
            int commentIdx = commentDao.createComment(postIdx, userIdx, postCommentReq);
            return new PostCommentRes(commentIdx);
        } catch (Exception exception) {
            throw new BaseException(CREATE_FAIL_COMMENT);
        }
    }

    // 댓글 삭제
    public void deleteComment(int userIdx, int commentIdx) throws com.there.config.BaseException, BaseException {

        if(commentProvider.checkUserCommentExist(userIdx, commentIdx) == 0){
            throw new BaseException(USERS_COMMENT_INVALID_ID);
        }
        try {
            int result = commentDao.deleteComment(commentIdx);
            if(result == 0) {
                throw new BaseException(DELETE_FAIL_COMMENT);
            }
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
