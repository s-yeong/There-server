package com.there.src.post;

import com.there.config.BaseException;
import com.there.config.BaseResponseStatus;
import com.there.src.like.model.PatchLikeReq;
import com.there.src.post.model.PatchPostsReq;
import com.there.src.post.model.PostPostsReq;
import com.there.src.post.model.PostPostsRes;
import com.there.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.there.config.BaseResponseStatus.*;

@Service
public class PostService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PostDao postDao;
    private final JwtService jwtService;

    @Autowired
    public PostService(PostDao postDao, JwtService jwtService) {
        this.postDao = postDao;
        this.jwtService = jwtService;

    }

    // 게시글 생성
    public PostPostsRes createPosts(int userIdx, PostPostsReq postPostsReq) throws BaseException {
        try {
            int postIdx = postDao.createPosts(userIdx, postPostsReq);
            return new PostPostsRes(postIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    // 게시글 삭제
    public void deletePosts(int postIdx) throws BaseException {

        try {
            int result = postDao.deletePosts(postIdx);
            // 삭제 확인 (0 : 실패 / 1 : 성공)
            if (result == 0) throw new BaseException(DATABASE_ERROR);
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    // 게시글 수정
    public void updatePosts(PatchPostsReq patchPostsReq) throws BaseException {
        try {
            int result = postDao.updatePosts(patchPostsReq);
            // 삭제 확인 (0 : 실패 / 1 : 성공)
            if (result == 0) throw new BaseException(DATABASE_ERROR);
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
