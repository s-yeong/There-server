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


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public SearchProvider(SearchDao searchDao) {
        this.searchDao = searchDao;
    }

    // 인기 검색어 조회
    public List<GetPopularSearchListRes> retrievePopularSearches() throws BaseException {
        try{

            List<GetPopularSearchListRes> getPopularSearchList = searchDao.selectPopularSearches();

            return getPopularSearchList;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    // 최근 검색어 조회
    public List<GetRecentSearchListRes> retrieveRecentSearches(int userIdx) throws BaseException {
        try{

            List<GetRecentSearchListRes> getRecentSearchList = searchDao.selectRecentSearches(userIdx);

            return getRecentSearchList;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }


    // 계정 검색
    public List<GetSearchByAccountRes> retrieveByAccount(int userIdx, String account) throws BaseException {
        try{
            List<GetSearchByAccountRes> getSearchByAccount = searchDao.selectAccountList(account);

            // 검색 기록 체크 - 존재O : 1, 존재X : 0
            if(checkSearchExist(userIdx, account) == 0){

                // 중복 검색어 체크 - 존재O : 1, 존재X : 0
                if(checkDuplicateKeyword(account) == 0)
                    searchDao.insertSearch(userIdx, account);
                // 중복 검색어인 경우 해당 유저가 검색한지만 기록(UserSearch)
                else searchDao.insertUserSearch(userIdx, account);
            }

            // 검색 기록 존재하면 기록 업데이트(최근 검색어 업데이트 순으로 나열하기 위해)
            else{
                searchDao.updateSearch(account);
            }

            return getSearchByAccount;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    // 해시태그 검색
    public List<GetSearchByHashtagRes> retrieveByHashtag(int userIdx, String hashtag) throws BaseException {
        try{
            List<GetSearchByHashtagRes> getSearchByHashtag = searchDao.selectHashtagList(hashtag);

            // 검색 기록 체크 - 존재O : 1, 존재X : 0
            if(checkSearchExist(userIdx, hashtag) == 0){

                // 중복 검색어 체크 - 존재O : 1, 존재X : 0
                if(checkDuplicateKeyword(hashtag) == 0)
                    searchDao.insertSearch(userIdx, hashtag);
                // 중복 검색어인 경우 해당 유저가 검색한지만 기록(UserSearch)
                else searchDao.insertUserSearch(userIdx, hashtag);
            }

            // 검색 기록 존재하면 기록 업데이트(최근 검색어 업데이트 순으로 나열하기 위해)
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

    // 중복 검색어 체크 - 존재O : 1, 존재X : 0
    public int checkDuplicateKeyword(String keyword) throws BaseException {
        try{
            int result = searchDao.checkDuplicateKeyword(keyword);
            return result;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 해당 유저 검색인지 체크
    public int checkUserSearch(int userIdx, int searchIdx) throws BaseException {
        try{
            int result = searchDao.checkUserSearch(userIdx, searchIdx);
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

    // 해당 유저의 검색 기록이 존재하는지 체크
    public int checkUserSearchExist(int userIdx) throws BaseException {
        try{
            int result = searchDao.checkUserSearchExist(userIdx);
            return result;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
