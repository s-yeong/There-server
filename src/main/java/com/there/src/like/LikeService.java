package com.there.src.like;

import com.there.src.like.model.DeleteLikeReq;
import com.there.src.like.model.PostLikeReq;
import com.there.src.like.model.PostLikeRes;
import com.there.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.there.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class LikeService {

    private final LikeDao likeDao;
    private final JwtService jwtService;

    @Autowired
    public LikeService(LikeDao likeDao,JwtService jwtService) {
        this.likeDao = likeDao;
        this.jwtService = jwtService;
    }
    
    // 좋아요 및 감정표현 생성
    public PostLikeRes createLikes(PostLikeReq postLikeReq) throws BaseException {
        try {
            int likesIdx = likeDao.createLikes(postLikeReq);
            return new PostLikeRes(likesIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    
    // 좋아요 및 감정표현 수정
    public void updateLikes(int userIdx, int postIdx, int emotion) throws BaseException {
        try {
            int result = likeDao.updateLikes(userIdx, postIdx, emotion);
            if (result == 0) throw new BaseException(DATABASE_ERROR);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 좋아요 및 감정표현 삭제
    public void deleteLikes(DeleteLikeReq deleteLikeReq) throws BaseException {
        try {
            int result = likeDao.deleteLikes(deleteLikeReq);
            if (result == 0) throw new BaseException(DATABASE_ERROR);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
