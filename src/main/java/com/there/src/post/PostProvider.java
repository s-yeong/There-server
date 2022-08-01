package com.there.src.post;

import com.there.src.post.config.BaseException;
import com.there.src.post.model.GetPostListRes;
import com.there.src.post.model.GetPostRes;
import com.there.src.post.model.GetPostTagRes;
import com.there.src.post.model.GetTotalPostRes;
import com.there.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.there.src.post.config.BaseResponseStatus.*;

@Service
public class PostProvider {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PostDao postDao;
    private final JwtService jwtService;

    @Autowired
    public PostProvider(PostDao postDao, JwtService jwtService) {
        this.postDao = postDao;
        this.jwtService = jwtService;
    }


    // 랜덤 게시글 리스트 조회
    public List<GetPostListRes> retrievePosts() throws BaseException {
        try {
            List<GetPostListRes> getPostListRes = postDao.selectRandomPostList();
            return getPostListRes;
        } catch (Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 인기글 게시글 리스트 조회
    public List<GetPostListRes> retrieveRankingPosts() throws BaseException {
        try{
            List<GetPostListRes> getPostListRes = postDao.selectRankingPostList();
            return getPostListRes;
        } catch(Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 내가 팔로우한 구독자의 게시글 리스트 조회
    public List<GetPostListRes> retrieveFollowerPosts(int userIdx) throws BaseException{
        try {
            List<GetPostListRes> getPostListRes = postDao.selectFollowerPostList(userIdx);

            return getPostListRes;
        } catch(Exception exception) {

            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }


}
