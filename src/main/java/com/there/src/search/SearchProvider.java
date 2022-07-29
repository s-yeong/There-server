package com.there.src.search;

import com.there.config.BaseException;
import com.there.src.search.model.*;
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

    // 최근 검색 기록 API
    public List<GetRecentSearchListRes> retrieveRecentSearches(int userIdx) throws BaseException {
        try{
            List<GetRecentSearchRes> getRecentSearch = searchDao.selectRecentSearches(userIdx);
            List<GetRecentSearchListRes> getRecentSearchList = searchDao.selectRecentSearchIdx(userIdx);

            for(int i = 0; i < getRecentSearch.size(); i++){

                if(getRecentSearch.get(i).getTagOrAccount().equals("Tag")){
                    GetSearchByHashtagRes getSearchByHashtagRes = searchDao.selectHashtag(getRecentSearch.get(i).getTagOrUserIdx());
                    getRecentSearchList.get(i).setGetSearchByHashtagRes(getSearchByHashtagRes);

                }

                else if(getRecentSearch.get(i).getTagOrAccount().equals("Account")){
                    GetSearchByAccountRes getSearchByAccountRes = searchDao.selectAccount(getRecentSearch.get(i).getTagOrUserIdx());
                    getRecentSearchList.get(i).setGetSearchByAccountRes(getSearchByAccountRes);
                }
            }
            return getRecentSearchList;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    // 통합 검색 API
    public GetSearchByAllRes retrieveByAll(String keyword) throws BaseException {
        try{

            List<GetSearchByAccountRes> getSearchByAccount = searchDao.selectAccountList(keyword);
            List<GetSearchByHashtagRes> getSearchByHashtag = searchDao.selectHashtagList(keyword);

            GetSearchByAllRes getSearchByAll = new GetSearchByAllRes(getSearchByAccount, getSearchByHashtag);

            return getSearchByAll;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

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
            throw new BaseException(DATABASE_ERROR);
        }

    }

    // 해시태그 인기 게시물 검색
    public List<GetSearchPostsByHashtagRes> retrievePopularPosts(int tagIdx) throws BaseException {
        try{
            List<GetSearchPostsByHashtagRes> getSearchPostsByHashtag = searchDao.selectPopularPosts(tagIdx);
            return getSearchPostsByHashtag;
        }
        catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 해시태그 최근 게시물 검색

    public List<GetSearchPostsByHashtagRes> retrieveRecentPosts(int tagIdx) throws BaseException {
        try{
            List<GetSearchPostsByHashtagRes> getSearchPostByHashtag = searchDao.selectRecentPosts(tagIdx);
            return getSearchPostByHashtag;
        }
        catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
