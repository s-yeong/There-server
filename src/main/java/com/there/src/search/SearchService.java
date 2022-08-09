package com.there.src.search;

import com.there.src.search.config.BaseException;
import com.there.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.there.src.search.config.BaseResponseStatus.*;


@Service
public class SearchService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SearchDao searchDao;
    private final SearchProvider searchProvider;
    private final JwtService jwtService;


    @Autowired
    public SearchService(SearchDao searchDao, SearchProvider searchProvider, JwtService jwtService) {
        this.searchDao = searchDao;
        this.searchProvider = searchProvider;
        this.jwtService = jwtService;
    }


    // 최근 검색어 삭제
    public void deleteRecentSearch(int userIdx, int searchIdx) throws BaseException, com.there.config.BaseException {

        if(searchProvider.checkUserExist(userIdx) == 0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }

        // 해당하는 유저의 검색 기록인지
        if(searchProvider.checkUserSearchExist(userIdx, searchIdx) == 0){
            throw new BaseException(USERS_SEARCHES_INVALID_ID);
        }

        try{
            searchDao.deleteRecentSearch(userIdx, searchIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    // 최근 검색어 모두 삭제
    public void deleteAllRecentSearch(int userIdx) throws BaseException, com.there.config.BaseException {

        if(searchProvider.checkUserExist(userIdx) == 0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }

        try{

            int result = searchDao.deleteAllRecentSearch(userIdx);
            if(result == 0){
                throw new BaseException(DELETE_FAIL_SEARCH);
            }

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }


}