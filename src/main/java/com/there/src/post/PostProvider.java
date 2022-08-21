package com.there.src.post;

import com.there.config.BaseException;
import com.there.src.post.model.*;
import com.there.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.there.config.BaseResponseStatus.*;

@Service
public class PostProvider {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PostDao postDao;

    @Autowired
    public PostProvider(PostDao postDao) {
        this.postDao = postDao;
    }


    // 게시글 조회
    public GetPostsRes retrievePosts(int postIdx) throws BaseException {
        try {
            GetPostsRes getPostsRes = postDao.selectPosts(postIdx);
            return getPostsRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 랜덤 게시글 리스트 조회
    public List<GetPostListRes> retrieveRandomPosts() throws BaseException {
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
