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
    private final JwtService jwtService;

    @Autowired
    public CommentService(CommentDao commentDao, JwtService jwtService){
        this.commentDao = commentDao;
        this.jwtService = jwtService;
    }

    public PostCommentRes createComment(int postIdx, int userIdx, PostCommentReq postCommentReq) throws BaseException{
        try {
            int commentIdx = commentDao.createComment(postIdx, userIdx, postCommentReq);
            return new PostCommentRes(commentIdx);
        } catch (Exception exception) {
            throw new BaseException(CREATE_FAIL_COMMENT);
        }
    }
}
