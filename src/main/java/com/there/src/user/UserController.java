package com.there.src.user;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.there.src.user.config.BaseException;
import com.there.src.user.config.BaseResponse;
import com.there.src.user.model.*;
import com.there.utils.JwtService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.there.src.user.config.BaseResponseStatus.*;
import static com.there.utils.ValidationRegex.isRegexEmail;



@RestController
@RequestMapping("/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;




    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService){
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }



    /**
     * 유저 조회 API
     * [GET] /users
     * @return BaseResponse<GetUserRes>
     */

    @ApiOperation(value="유저 조회 API", notes=" 유저 인덱스값 입력시 해당하는 유저 정보 리턴")
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @ResponseBody
    @GetMapping("/{userIdx}") // (GET) 127.0.0.1:9000/users/:userIdx
    public BaseResponse<GetUserRes> getUserByIdx(@PathVariable("userIdx")int userIdx) {
        try{

            GetUserRes getUsersRes = userProvider.getUsersByIdx(userIdx);
            return new BaseResponse<>(getUsersRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /** 유저 피드 조회
     * [GET] /users/feed
     * @return BaseResponse<GetUserFeedRes>
     */

    @ResponseBody
    @GetMapping("/feed/{userIdx}")
    public BaseResponse<GetUserFeedRes> getUserFeed(@PathVariable("userIdx") int userIdx)throws com.there.config.BaseException{
        try{
            int userIdxByJwt = jwtService.getUserIdx();
            GetUserFeedRes getUserFeed=userProvider.retrieveUserFeed(userIdx,userIdxByJwt);
            return new BaseResponse<>(getUserFeed);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 로그인 API
     * [POST] /users/login
     */
    @ResponseBody
    @PostMapping("/login")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq){
        try {
            if(postLoginReq.getEmail() == null)
            {
                return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
            }

            if(postLoginReq.getPassword() == null)
            {
                return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
            }

                // 이메일 형식
            if(!isRegexEmail(postLoginReq.getEmail()))
            {
                return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
            }

            PostLoginRes postLoginRes = userService.logIn(postLoginReq);

            return new BaseResponse<>(postLoginRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 회원가입 API
     * [POST] /users/join
     */
    @ResponseBody
    @PostMapping("/join")
    public BaseResponse<PostJoinRes> createUser(@RequestBody PostJoinReq postJoinReq) {
        try{
            if(postJoinReq.getEmail() == null)
            {
                return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
            }

            if(postJoinReq.getPassword() == null)
            {
                return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
            }

            // 이메일 정규 표현
           if(!isRegexEmail(postJoinReq.getEmail()))
           {
                return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
           }

            PostJoinRes postJoinRes = userService.createUser(postJoinReq);
            return new BaseResponse<>(postJoinRes);
        } catch(BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 프로필 수정
     * [PATCH] /users/{userIdx}
     */
    @ResponseBody
    @PatchMapping(value = "/{userIdx}", consumes = {"multipart/form-data"})
    public BaseResponse<String> modifyProfile(@PathVariable("userIdx")int userIdx, @RequestParam ("jsonList") String jsonList,
                                              @RequestPart(value = "images", required = false) List<MultipartFile> MultipartFiles)
            throws IOException, com.there.config.BaseException{

        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        PatchUserReq patchUserReq = objectMapper.readValue(jsonList, new TypeReference<>() {});

        if(patchUserReq.getNickName() ==null) {
            return new BaseResponse<>(POST_USER_EMPTY_NICKNAME);
        }
        if(MultipartFiles == null){
            return new BaseResponse<>(POST_USER_EMPTY_PROFILEIMG);
        }
        if(patchUserReq.getName() == null){
            return new BaseResponse<>(POST_USER_EMPTY_NAME);
        }
        if(patchUserReq.getInfo() == null){
            return new BaseResponse<>(POST_USER_EMPTY_INFO);
        }
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            userService.modifyProfile(userIdx, patchUserReq, MultipartFiles);
            String result ="회원정보 수정을 완료하였습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 회원 삭제
     * [PATCH] /{userIdx}/status
     */
    @ResponseBody
    @PatchMapping("/{userIdx}/status")
    public BaseResponse<String> deleteUser(@PathVariable("userIdx") int userIdx) throws com.there.config.BaseException{
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            userService.deleteUser(userIdx);

            String result = "삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch(BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
