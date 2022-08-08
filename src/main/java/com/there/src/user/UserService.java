package com.there.src.user;

import com.there.src.s3.S3Service;
import com.there.src.user.config.BaseException;

import static com.there.src.user.config.BaseResponseStatus.*;


import com.there.src.user.model.*;

import com.there.utils.AES256;
import com.there.utils.JwtService;
import com.there.utils.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;
    private final S3Service s3Service;

    private final RedisTemplate redisTemplate;



    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService, S3Service s3Service, RedisTemplate redisTemplate) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;
        this.s3Service = s3Service;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    // 로그인
    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException, com.there.config.BaseException {

        User user = userDao.getPassword(postLoginReq);

        String pwd;
        try{
            //암호화
            pwd = new SHA256().encrypt(postLoginReq.getPassword());  postLoginReq.setPassword(pwd);
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }

        // password 비교하여 일치한다면 jwt 발급
        if (postLoginReq.getPassword().equals(pwd)) {
            int userIdx = userDao.getPassword(postLoginReq).getUserIdx();
            String accessToken = jwtService.createToken(userIdx);
            String refreshToken = jwtService.createRefreshToken();

            userDao.refreshTokensave(refreshToken, userIdx);

            redisTemplate.opsForValue()
                    .set("RT: " + userIdx, refreshToken, jwtService.getExpiration1(refreshToken), TimeUnit.MILLISECONDS);

            return new PostLoginRes(userIdx, accessToken, refreshToken);
        } else
            throw new BaseException(FAILED_TO_LOGIN);
    }

    @Transactional
    public void logout(int userIdx) throws BaseException {
        try {

            int result = userDao.logout(userIdx);
            //System.out.println(req);

            if (result == 0) {
                throw new BaseException(FAIL_TO_LOGOUT);
            }

            System.out.println("무슨 토큰이야" + jwtService.getJwt());

            // 1. Access Token 검증
            if (!jwtService.validationToken(jwtService.getJwt())){
                throw new BaseException(ACCESS_TOKEN_ERROR);
            }

            // 2. Access Token에서 userIdx 가져오기
            String accessToken = jwtService.getJwt();
            jwtService.getUserIdx1(accessToken);


            // 3. Redis에서 해당 userIdx로 저장된 Refresh Token이 있는지 여부를 확인 후 있을 경우 삭제합니다.
            if (redisTemplate.opsForValue().get("RT: " + userIdx) != null) {

                // Refresh Token 삭제
                redisTemplate.delete("RT: " + userIdx);
            }

             // 4. 해당 Access Token 유효시간 가지고 와서 BlackList로 저장하기
            Long expiration = jwtService.getExpiration(accessToken);
            redisTemplate.opsForValue()
                    .set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);

        } catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 회원가입
    @Transactional
    public PostJoinRes createUser(PostJoinReq postJoinReq) throws BaseException {
        // 중복 확인
        if (userProvider.checkEmail(postJoinReq.getEmail()) == 1) {
            throw new BaseException(DUPLICATED_EMAIL);
        }
        // 입력 비밀번호 일치 여부 확인
        if (postJoinReq.getPassword().equals(postJoinReq.getCheckpwd()) ==false){
            throw new BaseException(DUPLICATED_PWD);

        }
            // 암호화
            String password;
            try {
                password = new AES256().encrypt(postJoinReq.getPassword());
                postJoinReq.setPassword(password);

            } catch (Exception ignored) {
                throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
            }

            try {
                int userIdx = userDao.createUser(postJoinReq);

                return new PostJoinRes(userIdx);
            } catch (Exception exception) {
                throw new BaseException(DATABASE_ERROR);
            }
        }

    // 유저 프로필 수정
    @Transactional(rollbackFor = BaseException.class)
    public void modifyProfile(int userIdx, PatchUserReq patchUserReq, List<MultipartFile> MultipartFiles) throws BaseException{
        if(userProvider.checkUserExist(userIdx) == 0) {
            throw new BaseException(USERS_EMPTY_USER_ID);
        }
        if(MultipartFiles.size() > 1){
            throw new BaseException(USERS_EXCEEDED_PROFILEIMG);
        }
         try {
             if (MultipartFiles != null) {

                    s3Service.removeFolder("User/userIdx : " + Integer.toString(userIdx));
                    s3Service.delUserProfileImg(userIdx);

                     // s3 업로드
                     String s3path = "User/userIdx : " + Integer.toString(userIdx);
                     String imgPath = s3Service.uploadFiles(MultipartFiles.get(0), s3path);

                     // db 업로드
                     s3Service.uploadUserProfileImg(imgPath, userIdx);
             }
             int result = userDao.updateProfile(userIdx, patchUserReq);
             if (result == 0) {
                 throw new BaseException(MODIFY_FAIL_USERNAME);
             }
         }catch (Exception exception) {
             System.out.println(exception);
             throw new BaseException(DATABASE_ERROR);
         }
    }

    // 회원 삭제
    public void deleteUser(int userIdx) throws BaseException{
        try{
            int result = userDao.updateUserStatus(userIdx);
            if(result == 0) {
                throw new BaseException(DELETE_FAIL_USER);
            }
        } catch(Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // Access Token, Refresh Token 재발급 요청
    public String reissue(int userIdx, String accessToken, String refreshToken) throws BaseException, com.there.config.BaseException {

        // 만료된 refresh token 에러
        if (!jwtService.validationExpiration(refreshToken)){
            throw new BaseException(REFRESH_TOKEN_ERROR);
        }

        // refresh token 불일치 에러
        if (!userDao.getRefreshToken(userIdx).equals(refreshToken)) {
            throw new BaseException(REFRESH_TOKEN_ERROR);
        }
        // AccessToken, RefreshToken 토큰 재발급, 리프레시 토큰 저장
        String newCreatedToken = jwtService.createToken(userIdx);
        String newRefreshToken = jwtService.createRefreshToken();
        userDao.refreshTokensave(newRefreshToken, userIdx);

        // RefreshToken Redis 업데이트
        redisTemplate.opsForValue()
                .set("RT: " + userIdx, newRefreshToken, jwtService.getExpiration1(newRefreshToken), TimeUnit.MILLISECONDS);

        return newCreatedToken;
    }
}


