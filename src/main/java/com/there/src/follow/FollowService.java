package com.there.src.follow;

import com.there.config.BaseException;

import com.there.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.there.config.BaseResponseStatus.*;
@Service
@RequiredArgsConstructor
public class FollowService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final FollowDao followDao;


    // 팔로우
     public void follow(int userIdx, int followeeIdx) throws BaseException{
         try {
             int follow = followDao.follow(userIdx, followeeIdx);
         } catch (Exception exception) {
             throw new BaseException(DATABASE_ERROR);
         }
     }


    // 언팔로우
    public void unfollow(int followIdx) throws BaseException{
        try {
            int follow = followDao.unfollow(followIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
