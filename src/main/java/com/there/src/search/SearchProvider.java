package com.there.src.search;

import com.there.src.search.config.BaseException;
import com.there.src.search.model.*;
import com.there.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.there.src.search.config.BaseResponseStatus.DATABASE_ERROR;


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

    // 최근 검색어 조회 API
    public List<GetRecentSearchListRes> retrieveRecentSearches(int userIdx) throws BaseException {
        try{

            List<GetRecentSearchListRes> getRecentSearchList = searchDao.selectRecentSearches(userIdx);

            return getRecentSearchList;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    // 통합 검색 API
    public GetSearchByAllRes retrieveByAll(int userIdx, String keyword) throws BaseException {
        try{

            List<GetSearchByAccountRes> getSearchByAccount = searchDao.selectAccountList(keyword);
            List<GetSearchByHashtagRes> getSearchByHashtag = searchDao.selectHashtagList(keyword);
            // 검색 기록
            if(checkSearchExist(userIdx, keyword) == 0){
                searchDao.insertSearch(userIdx, keyword);
            }
            else{
                searchDao.updateSearch(keyword);
            }
            GetSearchByAllRes getSearchByAll = new GetSearchByAllRes(getSearchByAccount, getSearchByHashtag);

            return getSearchByAll;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    // 계정 검색 API
    public List<GetSearchByAccountRes> retrieveByAccount(int userIdx, String account) throws BaseException {
        try{
            List<GetSearchByAccountRes> getSearchByAccount = searchDao.selectAccountList(account);

            // 검색 기록
            if(checkSearchExist(userIdx, account) == 0){
                searchDao.insertSearch(userIdx, account);
            }
            else{
                searchDao.updateSearch(account);
            }

            return getSearchByAccount;
        }
        catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }

    }

    // 해시태그 검색 API
    public List<GetSearchByHashtagRes> retrieveByHashtag(int userIdx, String hashtag) throws BaseException {
        try{
            List<GetSearchByHashtagRes> getSearchByHashtag = searchDao.selectHashtagList(hashtag);
            // 검색 기록
            if(checkSearchExist(userIdx, hashtag) == 0){
                searchDao.insertSearch(userIdx, hashtag);
            }
            else{
                searchDao.updateSearch(hashtag);
            }
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

    // 검색 기록 체크 - 존재O : 1, 존재X : 0
    public int checkSearchExist(int userIdx, String keyword) throws BaseException {
        try{

            int result = searchDao.checkSearchExist(userIdx, keyword);
            return result;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 해당 유저 검색인지 체크
    public int checkUserSearchExist(int userIdx, int searchIdx) throws BaseException {
        try{
            int result = searchDao.checkUserSearchExist(userIdx, searchIdx);
            return result;

        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkUserExist(int userIdx) throws BaseException {
        try{
            return searchDao.checkUserExist(userIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
