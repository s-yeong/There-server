package com.there.src.point;

import com.there.config.BaseResponse;
import com.there.src.point.model.*;
import lombok.Setter;
import lombok.extern.java.Log;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import com.there.src.point.model.*;

import org.springframework.beans.factory.annotation.Autowired;

@Log
@Controller
public class PointController {
    @Setter(onMethod_ = @Autowired)
    private KakaoPayService kakaoPayService;



    @GetMapping("/kakaoPay")
    public void kakaoPayGet() {

    }

    @PostMapping("/kakaoPay")
    public BaseResponse<PostPointRes> kakaoPay(@RequestBody PostPointReq postPointReq) throws com.there.config.BaseException{
        try {

            log.info("kakaoPay post............................................");

            return "redirect:" + kakaoPayService.kakaoPayReady(postPointReq);
        }


    }

    @GetMapping("/kakaoPaySuccess")
    public void kakaoPaySuccess(@RequestParam("pg_token") String pg_token, Model model) {
        log.info("kakaoPaySuccess get............................................");
        log.info("kakaoPaySuccess pg_token : " + pg_token);

        model.addAttribute("info", kakaoPayService.kakaoPayInfo(pg_token));

    }
}
