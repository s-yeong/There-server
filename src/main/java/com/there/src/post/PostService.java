package com.there.src.post;

import com.there.config.BaseException;
import com.there.config.BaseResponseStatus;
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
    private final PostProvider PostProvider;
    private final JwtService jwtService;

    @Autowired
    public PostService(PostDao postDao, PostProvider postProvider, JwtService jwtService) {
        this.postDao = postDao;
        this.PostProvider = postProvider;
        this.jwtService = jwtService;

    }

    public void deletePost(int postIdx) throws BaseException {

        try {
            int result = postDao.deletePost(postIdx);
            // 삭제 확인 (0 : 실패 / 1 : 성공)
            if (result == 0) throw new BaseException(DELETE_FAIL_POST);
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }


}
