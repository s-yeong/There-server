package com.there.src.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
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


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
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

            return new PostLoginRes(userIdx, accessToken);
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

    // 카카오 로그인
    public String getKakaoAccessToken(String code) {
        String accessToken = "";
        String refreshToken = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";


        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=2e8b184b25eb6aee0a1496b4af1d7ffd");
            sb.append("&redirect_uri=https://recordinthere.shop/users/login/kakao");
            sb.append("&code=" + code);
            sb.append("&client_secret=fJMsCcaBpzyMWMj6ughnTuo9zl3jMLq6");
            bw.write(sb.toString());
            bw.flush();

            // 결과코드가 200이라면 성공
            int resposneCode = conn.getResponseCode();
            System.out.println("responseCode: " + resposneCode);

            // 요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {

                result += line;
            }

            System.out.println("response body: " + result);

            // Gson 라이브러리에 포함된 클래스로 JSON 파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            accessToken = element.getAsJsonObject().get("access_token").getAsString();
            refreshToken = element.getAsJsonObject().get("refresh_token").getAsString();

            System.out.println("access_token: " + accessToken);
            System.out.println("refresh_token: " + refreshToken);

            // 토큰 저장
            userDao.createKakaoUserToken(accessToken, refreshToken);

            br.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return accessToken;
    }

    public HashMap<String, Object> getUserInfo(String token)  {

        // 요청하는 클라이언트마다 가진 정보가 다를 수 있기에 HashMap타입으로 선언
        HashMap<String, Object> userInfo = new HashMap<>();
        String reqURL = "https://kapi.kakao.com/v2/user/me";
        String refreshToken = "";

        // access Token을 이용하여 사용자 정보 조회
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            //conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + token); //전송할 header 작성, access_token전송

            // 결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode: " + responseCode);

            // 요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body: " + result);

            //Gson 라이브러리로 JSON 파싱
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
            JsonObject kakao_account = element.getAsJsonObject().get("kakao_account").getAsJsonObject();

            String nickname = properties.getAsJsonObject().get("nickname").getAsString();
            String email = kakao_account.getAsJsonObject().get("email").getAsString();


            if (userDao.checkEmail(email) == 0){
                //없다면
                // user테이블 저장
                int kakaoIdx = userDao.getkakaoIdx(token);
                userDao.createKakaoUser(email, nickname, kakaoIdx);

            }

            userInfo.put("nickname" , nickname);
            userInfo.put("email", email);
            //br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 정보가 db에 저장되어 있는지
        return userInfo;
    }

    // 카카오 토큰 갱신
    public void updateKakaoToken(int kakaoIdx) throws BaseException {
        // error
        String RefreshToken = userDao.getkakaoRefreshToken(kakaoIdx);
        System.out.println(" kakao db에서 가져온 리프레시 토큰 " + RefreshToken);
        userDao.updateKakaoUser(kakaoIdx);

        KakaoToken kakaoToken = userProvider.getKakaoToken(kakaoIdx);
        String postURL = "https://kauth.kakao.com/oauth/token";
        KakaoToken newToken = null;

        try {
            URL url = new URL(postURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // POST 요청에 필요한 파라미터를 OutputStream을 통해 전송
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            String sb = "grant_type=refresh_token" +
                        "&client_id=2e8b184b25eb6aee0a1496b4af1d7ffd" + // REST_API_KEY
                        "&refresh_token=" + userDao.getkakaoRefreshToken(kakaoIdx) + // REFRESH_TOKEN
                        "&client_secret=fJMsCcaBpzyMWMj6ughnTuo9zl3jMLq6";
            bufferedWriter.write(sb);
            bufferedWriter.flush();

            // 요청을 통해 얻은 데이터를 InputStreamReader을 통해 읽어 오기
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            StringBuilder result = new StringBuilder();

            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            System.out.println("response body : " + result);

            JsonElement element = JsonParser.parseString(result.toString());

            Set<String> keySet = element.getAsJsonObject().keySet();

            // 새로 발급 받은 accessToken 불러오기
            String accessToken = element.getAsJsonObject().get("access_token").getAsString();

            // refreshToken은 유효 기간이 1개월 미만인 경우에만 갱신되어 반환되므로,
            // 반환되지 않는 경우의 상황을 if문으로 처리해주었다.
            String refreshToken = "";
            if(keySet.contains("refresh_token")) {
                refreshToken = element.getAsJsonObject().get("refresh_token").getAsString();
            }

            if(refreshToken.equals("")) {
                newToken = new KakaoToken(accessToken, kakaoToken.getRefreshtoken());
            } else {
                newToken = new KakaoToken(accessToken, refreshToken);
            }

            bufferedReader.close();
            bufferedWriter.close();

        } catch (IOException exception) {
            exception.printStackTrace();
        }
        try{
            int result = 0;
            if (newToken != null) {
                result = userDao.updateKakaoToken(kakaoIdx, newToken);
            }
            if(result == 0){
                throw new BaseException(REFRESH_TOKEN_ERROR);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 카카오 로그아웃
    public void logout(String token) throws Exception {
        String reqUrl = "https://kapi.kakao.com//v1/user/logout";

        URL url = new URL(reqUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        //요청 메서드 설정
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        //요청 헤더정보 설정
        conn.setRequestProperty("Authorization", "Bearer " + token);//Bearer다음 한 칸 띄고, accessToken


        //읽을 때는 그냥 bufferedRead
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {

            //응답 상태코드 200이면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("응답 코드(로그아웃) : " + responseCode);

            //응답데이터를 입력스트림으로부터 읽어내기
            String responseData = br.readLine();
            System.out.println("logout-response-data: " + responseData);

        } catch (Exception e) {
            e.printStackTrace();
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
                // 유저 기본 프로필 사진 업로드
                s3Service.uploadUserdeafultProfileImg(userIdx);

                return new PostJoinRes(userIdx);
            } catch (Exception exception) {
                System.out.println(exception);
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

                     // s3 업로드
                     String s3path = "User/userIdx : " + Integer.toString(userIdx);
                     String imgPath = s3Service.uploadFiles(MultipartFiles.get(0), s3path);

                     // db 업로드
                     s3Service.uploadUserProfileImg(imgPath, userIdx);
             }
             int result = 0;
             if (patchUserReq.getNickName() != null){
                 result = userDao.updateNickName(userIdx, patchUserReq);

             }
             if(patchUserReq.getName()!= null) {
                 result = userDao.updateName(userIdx, patchUserReq);
             }

             if(patchUserReq.getInfo() != null) {
                 result = userDao.updateInfo(userIdx, patchUserReq);
             }

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

        /*
        System.out.println(refreshToken);
        // 만료된 refresh token 에러
        if (!jwtService.validationExpiration(refreshToken)){
            System.out.println();
            throw new BaseException(REFRESH_TOKEN_ERROR);
        }*/

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


