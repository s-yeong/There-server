package com.there.src.search;

import com.there.config.BaseException;
import com.there.src.search.model.GetSearchByAccountRes;
import com.there.src.search.model.GetSearchByHashtagRes;
import com.there.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.there.config.BaseResponseStatus.DATABASE_ERROR;


@Service
public class SearchProvider {

    private final SearchDao searchDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public SearchProvider(SearchDao searchDao, JwtService jwtService) {
        this.searchDao = searchDao;
        this.jwtService = jwtService;
    }

    // 계정 검색 API
    public List<GetSearchByAccountRes> retrieveByAccount(String account) throws BaseException {
        try{
            List<GetSearchByAccountRes> getSearchByAccount = searchDao.selectAccountList(account);
            return getSearchByAccount;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    // 해시태그 검색 API
    public List<GetSearchByHashtagRes> retrieveByHashtag(String hashtag) throws BaseException {
        try{
            List<GetSearchByHashtagRes> getSearchByHashtag = searchDao.selectHashtagList(hashtag);
            return getSearchByHashtag;
        }
        catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }

    }


}
