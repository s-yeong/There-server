package com.there.src.user;

import com.there.config.BaseException;
import com.there.config.BaseResponse;
import com.there.src.post.model.Post;
import com.there.src.user.model.*;
import com.there.utils.JwtService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import static com.there.config.BaseResponseStatus.*;
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
    public BaseResponse<GetUserFeedRes> getUserFeed(@PathVariable("userIdx") int userIdx){
        try{


            /*
            int userIdxByJwt = jwtService.getUserIdx();
            GetUserFeedRes getUserFeed=userProvider.retrieveUserFeed(userIdx,userIdxByJwt);
            */

            GetUserFeedRes getUserFeed = userProvider.retrieveUserFeed(userIdx);
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
    public BaseResponse<PostJoinRes> createUser(@RequestBody PostJoinReq postJoinReq){
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
}
