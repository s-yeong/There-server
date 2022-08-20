package com.there.src.follow;

import com.there.src.follow.model.GetFollowerListRes;
import com.there.src.follow.model.GetFollowingListRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.there.config.BaseException;

import java.util.List;

import static com.there.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class FollowProvider {
    private final FollowDao followDao;

    @Autowired
    public FollowProvider(FollowDao followDao) {
        this.followDao = followDao;
    }

    // 나를 팔로우 하는 유저 리스트 조회 (== FollowerList)
    public List<GetFollowerListRes> FollowerList(int userIdx)  throws BaseException {
        try {
            List<GetFollowerListRes> getFollowerListRes =  followDao.selectFollowerList(userIdx);
            return getFollowerListRes;
        } catch(Exception exception){
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 내가 팔로우 하는 유저 리스트 조회 (==FollowingList)
    public List<GetFollowingListRes> FollowingList(int userIdx) throws BaseException {
        try {
            List<GetFollowingListRes> getFollowingListRes = followDao.selectFollowingList(userIdx);
            return getFollowingListRes;
        } catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }
}


