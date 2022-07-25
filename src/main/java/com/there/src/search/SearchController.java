package com.there.src.search;

import com.there.config.BaseException;
import com.there.config.BaseResponse;
import com.there.src.search.model.GetSearchByAccountRes;
import com.there.src.search.model.GetSearchByHashtagRes;
import com.there.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final SearchProvider searchProvider;
    @Autowired
    private final SearchService searchService;
    @Autowired
    private final JwtService jwtService;


    public SearchController(SearchProvider searchProvider, SearchService searchService, JwtService jwtService) {
        this.searchProvider = searchProvider;
        this.searchService = searchService;
        this.jwtService = jwtService;
    }


    /**
     * 계정 검색 API
     * [GET] /search/account?account=
     */
    @ResponseBody
    @GetMapping("/account")
    public BaseResponse<List<GetSearchByAccountRes>> getSearchByAccount(@RequestParam String account) throws com.there.config.BaseException{
        try{

             List<GetSearchByAccountRes> getSearchByAccountRes = searchProvider.retrieveByAccount(account);
            return new BaseResponse<>(getSearchByAccountRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 해시태그 검색 API - 게시물 태그로 사용되지 않은 것들은 검색되지 않음
     * [GET] /search/hashtag?hashTag=
     */
    @ResponseBody
    @GetMapping("/hashtag")
    public BaseResponse<List<GetSearchByHashtagRes>> getSearchByHashtag(@RequestParam String hashtag) throws com.there.config.BaseException{
        try{

            List<GetSearchByHashtagRes> getSearchByHashtagRes = searchProvider.retrieveByHashtag(hashtag);
            return new BaseResponse<>(getSearchByHashtagRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }




}

