package com.there.src.user;


import com.there.src.user.config.BaseException;
import com.there.src.user.model.*;
import com.there.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.there.src.user.config.BaseResponseStatus.*;


@Service
public class UserProvider {

    private final UserDao userDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserProvider(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
    }

    // 유저 피드 조회
    public GetUserFeedRes retrieveUserFeed(int userIdx, int userIdxByJwt) throws BaseException {

        if (checkUserExist(userIdx) == 0) {
            throw new BaseException(USERS_EMPTY_USER_ID);
        }

        try {

            if (userIdxByJwt != userIdx) {

            }
            GetUserRes getUserRes = userDao.getUsersByIdx(userIdx);
            List<GetUserPostsRes> getUserPosts = userDao.selectUserPosts(userIdx);
            GetUserFeedRes getUserFeed = new GetUserFeedRes(getUserRes, getUserPosts);

            return getUserFeed;
        } catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public GetUserRes getUsersByIdx(int userIdx) throws BaseException{
        try{
            GetUserRes getUsersRes = userDao.getUsersByIdx(userIdx);
            return getUsersRes;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkEmail(String email) throws BaseException{
        try{
            return userDao.checkEmail(email);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }

    // 유저 확인
    public int checkUserExist(int userIdx) throws BaseException{
        try{
            return userDao.checkUserExist(userIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public PostLoginRes getUserInfo(String email) throws BaseException{
        try {
            int userIdx = userDao.getUserInfo(email);
            String jwt = jwtService.createToken(userIdx);
            return new PostLoginRes(userIdx, jwt);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public KakaoToken getKakaoToken(int kakaoIdx) throws BaseException {
        try {
            return userDao.getKakaoToken(kakaoIdx);
        } catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
