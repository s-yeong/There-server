package com.there.src.user;


import com.there.config.BaseException;
import com.there.config.BaseResponse;
import com.there.src.user.model.*;
import com.there.utils.AES256;
import com.there.utils.JwtService;
import com.there.utils.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import static com.there.config.BaseResponseStatus.*;



@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;



    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;

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

}


