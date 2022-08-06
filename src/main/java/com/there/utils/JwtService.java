package com.there.utils;

import com.there.config.*;
import com.there.config.secret.Secret;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static com.there.config.BaseResponseStatus.*;
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtService {

    private final Long accessTokenVaildMillisecond = 60 * 60 * 1000L; // 1 hour
    private final Long refreshTokenVaildMillisecond = 14 * 24* 60 * 60 * 1000L; // 14 day
    private final UserDetailsService userDetailsService;
    /*
    JWT 생성
    @param userIdx
    @return String
     */
    public String createToken(int userIdx){

        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam("type","jwt")
                .claim("userIdx",userIdx)
                .setIssuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis()+accessTokenVaildMillisecond))
                .signWith(SignatureAlgorithm.HS256, Secret.JWT_SECRET_KEY)
                .compact();
    }

    /*
    jwt refresh token 생성
     */
    public String createRefreshToken() {
        Date now = new Date();
        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenVaildMillisecond))
                .signWith(SignatureAlgorithm.HS256, Secret.JWT_SECRET_KEY)
                .compact();
    }

    /*
    Header에서 X-ACCESS-TOKEN 으로 JWT 추출
    @return String
     */
    public String getJwt(){
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("X-ACCESS-TOKEN");
    }


    public UsernamePasswordAuthenticationToken getAuthentication(String token) {

        // Jwt에서 claims 추출
        Claims claims = parseClaims(token);

        UserDetails userDetails = userDetailsService.loadUserByUsername(claims.getSubject());

        return new UsernamePasswordAuthenticationToken(userDetails, "" , userDetails.getAuthorities());
    }

    //Jwt 토큰 복호화해서 가져오기
    private Claims parseClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(Secret.JWT_SECRET_KEY).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }

    /*
    JWT에서 userIdx 추출
    @return int
    @throws BaseException
     */
    public int getUserIdx() throws BaseException{
        //1. JWT 추출
        String accessToken = getJwt();
        if(accessToken == null || accessToken.length() == 0){
            throw new BaseException(EMPTY_JWT);
        }

        // 2. JWT parsing
        Jws<Claims> claims;
        try{
            claims = Jwts.parser()
                    .setSigningKey(Secret.JWT_SECRET_KEY)
                    .parseClaimsJws(accessToken);
        } catch (Exception ignored) {
            throw new BaseException(INVALID_JWT);
        }

        // 3. userIdx 추출
        return claims.getBody().get("userIdx",Integer.class);
    }

    public int getUserIdx1(String token) throws BaseException{
        //1. JWT 추출
        //String accessToken = getJwt();
        if(token == null || token.length() == 0){
            throw new BaseException(EMPTY_JWT);
        }

        // 2. JWT parsing
        Jws<Claims> claims;
        try{
            claims = Jwts.parser()
                    .setSigningKey(Secret.JWT_SECRET_KEY)
                    .parseClaimsJws(token);
        } catch (Exception ignored) {
            throw new BaseException(INVALID_JWT);
        }

        // 3. userIdx 추출
        return claims.getBody().get("userIdx",Integer.class);
    }


    // jwt 유효성 및 만료일자 확인
    public boolean validationToken(String token){
        try {
            Jwts.parser().setSigningKey(Secret.JWT_SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            System.out.println("잘못된 jwt 서명입니다. ");
        } catch (ExpiredJwtException e) {
            System.out.println("만료된 토큰입니다. ");
        } catch (UnsupportedJwtException e) {
            System.out.println("지원하지 않는 토큰입니다. ");
        } catch (IllegalArgumentException e) {
            System.out.println("잘못된 토큰입니다. ");
        }
        return false;
    }
}
