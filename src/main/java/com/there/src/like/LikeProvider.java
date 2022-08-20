package com.there.src.like;

import com.there.config.BaseException;
import com.there.src.like.model.GetLikeRes;
import com.there.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.there.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class LikeProvider {

    private final LikeDao likeDao;
    private final JwtService jwtService;

    @Autowired
    public LikeProvider(LikeDao likeDao, JwtService jwtService) {
        this.likeDao = likeDao;
        this.jwtService = jwtService;
    }

    // 좋아요 및 감정표현 조회
    public List<GetLikeRes> retrieveLikes(int postIdx) throws BaseException {
        try {
            List<GetLikeRes> getLikes = likeDao.selectLikes(postIdx);
            return getLikes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
