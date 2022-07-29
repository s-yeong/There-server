package com.there.src.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.there.config.BaseException;
import com.there.config.BaseResponse;
import com.there.src.search.model.*;
import com.there.utils.JwtService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.there.src.post.config.BaseResponseStatus.INVALID_USER_JWT;

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
     * 최근 검색어 조회 API
     *
     */
    @ApiOperation(value="최근 검색어 조회 API", notes="유저의 최근 검색 기록 조회")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @GetMapping("/recent")
    public BaseResponse<List<GetRecentSearchListRes>> getRecentSearch() throws com.there.config.BaseException{
      try{
          int userIdxByJwt = jwtService.getUserIdx();
          List<GetRecentSearchListRes> getRecentSearchListRes = searchProvider.retrieveRecentSearches(userIdxByJwt);
          return new BaseResponse<>((getRecentSearchListRes));
      } catch (BaseException exception) {
          return new BaseResponse<>((exception.getStatus()));
      }

    }

    /**
     * 최근 검색어 삭제 API
     */
    @ApiOperation(value="최근 검색어 삭제 API", notes="최근 검색 하나만 삭제")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @DeleteMapping("/recent/{searchIdx}")
    public BaseResponse<String> deleteRecentSearch(@PathVariable("searchIdx") int searchIdx) throws com.there.config.BaseException{
        try{
            int userIdxByJwt = jwtService.getUserIdx();
            searchService.deleteRecentSearch(userIdxByJwt, searchIdx);

            String result = "삭제를 성공했습니다.";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 계정 검색 API
     * [GET] /search/account?account=
     */
    @ApiOperation(value="계정 검색 API", notes="ACTIVE한 계정만 검색")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
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
     * 해시태그 검색 API
     * [GET] /search/hashtag?hashTag=
     */
    @ApiOperation(value="해시태그 검색 API", notes="게시물 태그로 사용되지 않은 해시태그는 검색되지 않음")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
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


    /**
     * 해시태그 인기 게시물 리스트 API
     * [GET] /search/hashtag/{tagIdx}/popular
     */
    @ApiOperation(value="해시태그 인기 게시물 리스트 API", notes="#해시태그(tagIdx)에 해당하는 게시물 리스트")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @GetMapping("/hashtag/{tagIdx}/popular")
    public BaseResponse<List<GetSearchPostsByHashtagRes>> getPopularSearch(@PathVariable("tagIdx") int tagIdx) throws com.there.config.BaseException{
        try{

            List<GetSearchPostsByHashtagRes> getSearchPostsByHashtagRes = searchProvider.retrievePopularPosts(tagIdx);
            return new BaseResponse<>(getSearchPostsByHashtagRes);

        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 해시태그 최근 게시물 리스트 API
     * [GET} /search/hashtag/{tagIdx}/recent
     */
    @ApiOperation(value="해시태그 최근 게시물 리스트 API", notes="#해시태그(tagIdx)에 해당하는 최근 게시물 리스트")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @GetMapping("/hashtag/{tagIdx}/recent")
    public BaseResponse<List<GetSearchPostsByHashtagRes>> getRecentSearch(@PathVariable("tagIdx") int tagIdx) throws com.there.config.BaseException{
        try{

            List<GetSearchPostsByHashtagRes> getSearchPostsByHashtagRes = searchProvider.retrieveRecentPosts(tagIdx);
            return new BaseResponse<>(getSearchPostsByHashtagRes);

        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }






}

