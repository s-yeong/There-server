package com.there.src.point;


import com.there.src.point.config.BaseException;
import com.there.utils.JwtService;
import lombok.Setter;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.there.src.point.model.*;

import org.springframework.beans.factory.annotation.Autowired;


@Log
@Controller
public class PointController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Setter(onMethod_ = @Autowired)
    private final KakaoPay kakaopay;

    private final JwtService jwtService;

    public PointController(KakaoPay kakaopay, JwtService jwtService) {
        this.kakaopay = kakaopay;
        this.jwtService = jwtService;
    }


    @ResponseBody
    @PostMapping("/kakaoPay/{userIdx}")
    public String kakaoPay(@PathVariable("userIdx") int userIdx, @RequestBody PostPointReq postpointReq)
            throws com.there.config.BaseException{

    log.info("kakaoPay post............................................");

    int userIdxByJwt = jwtService.getUserIdx();
    if (userIdxByJwt != userIdx) return "유저 확인 실패";

    return "redirect:" + kakaopay.kakaoPayReady(userIdx, postpointReq);

    }

    @GetMapping("/kakaoPaySuccess")
    public String kakaoPaySuccess(@RequestParam("pg_token") String pg_token, Model model) {
        log.info("kakaoPaySuccess get............................................");
        log.info("kakaoPaySuccess pg_token : " + pg_token);


        /*KakaoPayApprovalVO kakao = kakaopay.kakaoPayInfo(pg_token);
        pointService.chargePoint(kakao.getAmount());*/

        model.addAttribute("info", kakaopay.kakaoPayInfo(pg_token));

        return null;
    }

}
