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

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;
    private final S3Service s3Service;



    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService, S3Service s3Service) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;
        this.s3Service = s3Service;
    }

    // 로그인
    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException {

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
            String jwt = jwtService.createJwt(userIdx);
            return new PostLoginRes(userIdx, jwt);
        } else
            throw new BaseException(FAILED_TO_LOGIN);
    }


    // 회원가입
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
                // jwt 발급
                String jwt = jwtService.createJwt(userIdx);
                return new PostJoinRes(jwt, userIdx);
            } catch (Exception exception) {
                throw new BaseException(DATABASE_ERROR);
            }
        }

    // 유저 프로필 수정
    @Transactional(rollbackFor = BaseException.class)
    public void modifyProfile(int userIdx, PatchUserReq patchUserReq, List<MultipartFile> MultipartFile) throws BaseException{
        if(userProvider.checkUserExist(userIdx) == 0) {
            throw new BaseException(USERS_EMPTY_USER_ID);
        }

         try {
             if (MultipartFile != null) {
                 if(MultipartFile.size() > 1){
                     throw new BaseException(USERS_EXCEEDED_PROFILEIMG);
                 }

                     // s3 업로드
                     String s3path = "User/userIdx : " + Integer.toString(userIdx);
                     String imgPath = s3Service.uploadFiles(MultipartFile.get(0), s3path);

                     // db 업로드
                     s3Service.uploadUserProfileImg(imgPath, userIdx);
             }
             int result = userDao.updateProfile(userIdx, patchUserReq);
             if (result == 0) {
                 throw new BaseException(MODIFY_FAIL_USERNAME);
             }
         }catch (Exception exception) {
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
}


