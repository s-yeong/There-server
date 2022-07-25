package com.there.src.post;

import com.there.src.post.config.BaseException;
import com.there.src.post.model.GetPostListRes;
import com.there.src.post.model.PatchPostsReq;
import com.there.src.post.model.PostPostsReq;
import com.there.src.post.model.PostPostsRes;
import com.there.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.there.src.post.config.BaseResponseStatus.*;

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

    /**
     * 게시글 생성
     */
    public PostPostsRes createPosts(int userIdx, PostPostsReq postPostsReq) throws BaseException {
        try {
            int postIdx = postDao.createPosts(userIdx, postPostsReq);
            return new PostPostsRes(postIdx);
        } catch (Exception exception) {
            throw new BaseException(CREATE_FAIL_POST);
        }
    }

    /**
     * 게시글 수정
     *  ImgUrl, Content 수정
     *  ImgUrl 수정
     *  Content 수정
     */
    public void updatePosts(int postIdx, PatchPostsReq patchPostsReq) throws BaseException {
        int result = 0;

        try {
            if (patchPostsReq.getImgUrl() != null && patchPostsReq.getContent() != null) {
                result = postDao.updatePosts(postIdx, patchPostsReq);
            }
            else if (patchPostsReq.getImgUrl() != null) {
                result = postDao.updatepostsImgUrl(postIdx, patchPostsReq);
            }
            else if (patchPostsReq.getContent() != null) {
                result = postDao.updatepostsContent(postIdx, patchPostsReq);
            }

            if (result == 0) throw new BaseException(UPDATE_FAIL_POST); // 삭제 확인 (0 : 실패 / 1 : 성공)
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 게시글 삭제
     *  ACTIVE -> INACTIVE로 변경 (즉, 상태만 변경. 게시글 기록은 남김)
     */
    public void deletePosts(int postIdx) throws BaseException {

        try {
            int result = postDao.deletePosts(postIdx);
            if (result == 0) throw new BaseException(DELETE_FAIL_POST); // 삭제 확인 (0 : 실패 / 1 : 성공)
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }
}
