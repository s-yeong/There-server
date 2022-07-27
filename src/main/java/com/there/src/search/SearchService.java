package com.there.src.search;

import com.there.config.BaseException;
import com.there.config.BaseResponseStatus;
import com.there.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.there.config.BaseResponseStatus.DATABASE_ERROR;


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


    // 최근 검색 삭제

    public void deleteRecentSearch(int userIdx, int searchIdx) throws BaseException {


        // 해당하는 유저의 검색 기록인지

        try{

            int result = searchDao.deleteRecentSearch(searchIdx);
            if(result == 0){
//                trhow new BaseException(DELETE_FAIL_SEARCH);
            }

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }


}