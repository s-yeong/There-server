package com.there.src.user;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
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

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
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




    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService) {
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }



    /**
     * 유저 조회 API
     * [GET] /users
     * @return BaseResponse<GetUserRes>
     */

    @ApiOperation(value = "유저 조회 API", notes = " 유저 인덱스값 입력시 해당하는 유저 정보 리턴")
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동"),
            @ApiResponse(code = 500, message = "서버 에러")
    })
    @ResponseBody
    @GetMapping("/{userIdx}") // (GET) 127.0.0.1:9000/users/:userIdx
    public BaseResponse<GetUserRes> getUserByIdx(@PathVariable("userIdx") int userIdx) {
        try {

            GetUserRes getUsersRes = userProvider.getUsersByIdx(userIdx);
            return new BaseResponse<>(getUsersRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 유저 피드 조회
     * [GET] /users/feed
     * @return BaseResponse<GetUserFeedRes>
     */
    @ApiOperation(value = " 유저 피드 조회 API", notes = " 유저 인덱스 값 입력시 해당하는 유저 피드 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "API 정상 작동"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @GetMapping("/feed/{userIdx}")
    public BaseResponse<GetUserFeedRes> getUserFeed(@PathVariable("userIdx") int userIdx) throws com.there.config.BaseException {
        try {
            int userIdxByJwt = jwtService.getUserIdx1(jwtService.getJwt());
            GetUserFeedRes getUserFeed = userProvider.retrieveUserFeed(userIdx, userIdxByJwt);
            return new BaseResponse<>(getUserFeed);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 로그인 API
     * [POST] /users/login
     */
    @ApiOperation(value = "일반 로그인 API", notes = "Body 타입 : String ")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @PostMapping("/login")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq) {
        try {
            if (postLoginReq.getEmail() == null)
            {
                return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
            }

            if (postLoginReq.getPassword() == null)
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
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        } catch (com.there.config.BaseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 카카오 로그인 API
     * [GET] /users/login/kakao
     * @return BaseResponse<PostLoginRes>
     */
    @ApiOperation(value = " 카카오 로그인 API")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @GetMapping("/login/kakao")
    public BaseResponse<PostLoginRes> kakaoLogin(@RequestParam(required = false) String code){

        try {
            // URL에 포함된 code를 이용하여 액세스 토큰 발급
            String accessToken = userService.getKakaoAccessToken(code);
            System.out.println(accessToken);

            // 액세스 토큰을 이용하여 카카오 서버에서 유저 정보(닉네임, 이메일) 받아오기
            HashMap<String, Object> userInfo = userService.getUserInfo(accessToken);
            System.out.println("login Controller: " + userInfo);

            PostLoginRes postLoginRes = null;

            // 만일, db에 해당 email을 가지는 유저가 없으면 회원가입 시키고 유저 식별자와 jwt반환
            // 현재 카카오 유저의 전화번호를 받아올 권한이 없어서 테스트를 하지 못함.
            if(userProvider.checkEmail(String.valueOf(userInfo.get("email")))== 0) {
                //PostLoginRes postLoginRes = 해당 서비스;
                return new BaseResponse<>(postLoginRes);
            } else {
                // 아니면 기존 유저의 로그인으로 판단하고 유저 식별자와 jwt 반환
                postLoginRes = userProvider.getUserInfo(String.valueOf(userInfo.get("email")));
                return new BaseResponse<>(postLoginRes);
            }
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 카카오 토큰 갱신 API
     * [GET] /users/kakao/:userIdx
     * @return BaseResponse<String>
     */
    @ApiOperation(value = " 카카오 토큰 갱신 API", notes ="RequestParam : accessToken, refreshToken")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @GetMapping("/kakao/{kakaoIdx}")
    public BaseResponse<String> updateKakaoToken(@PathVariable int kakaoIdx) throws  BaseException {
        String result = "";
            userService.updateKakaoToken(kakaoIdx);
            return new BaseResponse<>(result);
    }

    /**
     * 일반 로그인 토큰 갱신 API
     * [POST] /users/:userIdx/reissue
     * @return BaseResponse<TokenDto>
     */
    @ApiOperation(
            value = "액세스, 리프레시 토큰 재발급",
            notes = "액세스 토큰 만료시 회원 검증 후 리프레스 토큰을 검증해서 액세스 토큰과 리프레시 토큰을 재발급합니다. ")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @PostMapping("/{userIdx}/reissue")
    public BaseResponse<TokenDto> reissue(
            @PathVariable("userIdx")int userIdx, @RequestParam ("accessToken") String accessToken, @RequestParam("refreshToken") String refreshToken) throws BaseException, com.there.config.BaseException {
        return new BaseResponse(userService.reissue(userIdx, accessToken,refreshToken ));
    }

    /**
     * 일반 로그아웃 API
     * [PACTH] /users/:userIdx/logout
     * @return BaseResponse<String>
     */
    @ApiOperation(value = "일반 로그인 로그아웃", notes = "유저 인덱스 값 입력 시 해당 유저 로그아웃")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @PatchMapping("/{userIdx}/logout")
    public BaseResponse<String> logout(@PathVariable("userIdx")int userIdx)  {
        try {
            userService.logout(userIdx);
            String result = "로그아웃 완료";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 일반 회원가입 API
     * [POST] /users/join
     * @return BaseResponse<PostJoinRes>
     */
    @ApiOperation(value = "일반 회원가입", notes = "Body 타입: String")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @PostMapping("/join")
    public BaseResponse<PostJoinRes> createUser(@RequestBody PostJoinReq postJoinReq) {
        try {
            if (postJoinReq.getEmail() == null)
            {
                return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
            }

            if (postJoinReq.getPassword() == null)
            {
                return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
            }

            // 이메일 정규 표현
            if (!isRegexEmail(postJoinReq.getEmail()))
            {
                return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
            }

            PostJoinRes postJoinRes = userService.createUser(postJoinReq);
            return new BaseResponse<>(postJoinRes);
        } catch (BaseException exception) {
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
     * @return BaseResponse<String>
     */
    @ApiOperation(value = "회원 삭제")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @PatchMapping("/{userIdx}/status")
    public BaseResponse<String> deleteUser(@PathVariable("userIdx") int userIdx) throws com.there.config.BaseException {
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            userService.deleteUser(userIdx);

            String result = "삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}

