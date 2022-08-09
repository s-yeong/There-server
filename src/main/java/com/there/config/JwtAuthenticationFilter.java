package com.there.config;

import com.there.utils.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService){
        this.jwtService = jwtService;
    }

    // request로 들어오느 Jwt의 유효성을 검증 - JwtService.validatinoToken()을 필터로서 FilterChain에 추가
    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain filterChain) throws IOException, ServletException {
        // request에서 token을 취한다.
        String token = jwtService.resolveToken((HttpServletRequest) request);

        //검증
        System.out.println("[Verifying token]");
        System.out.println(((HttpServletRequest) request).getRequestURL().toString());

        if (token != null && jwtService.validationToken(token)) {
            // 토큰이 유효하면 토큰으로부터 유저정보를 받아옵니다.
            Authentication authentication = jwtService.getAuthentication(token);
            // SecurityContextHolder에 Authetication 객체를 저장합니다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
}
