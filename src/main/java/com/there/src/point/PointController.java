package com.there.src.point;

import com.there.src.point.config.BaseException;
import com.there.src.point.config.BaseResponse;
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


import java.util.List;

import static com.there.src.point.config.BaseResponseStatus.*;
import static java.lang.Integer.parseInt;


@Log
@Controller
public class PointController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Setter(onMethod_ = @Autowired)
    private final KakaoPay kakaopay;

    private final JwtService jwtService;
    private final PointProvider pointProvider;

    public PointController(KakaoPay kakaopay, JwtService jwtService, PointProvider pointProvider) {
        this.kakaopay = kakaopay;
        this.jwtService = jwtService;
        this.pointProvider = pointProvider;
    }

    /**
     * 내 포인트 충전 API
     * kakaoPay/:userIdx
     */
    @ResponseBody
    @PostMapping("/kakaoPay/{userIdx}")
    public String kakaoPay(@PathVariable("userIdx") int userIdx, @RequestBody PostPointReq postpointReq)
            throws com.there.config.BaseException {

        log.info("kakaoPay post............................................");

        int userIdxByJwt = jwtService.getUserIdx();
        if (userIdxByJwt != userIdx) return "유저 확인 실패";

        return "redirect:" + kakaopay.kakaoPayReady(userIdx, postpointReq);

    }
/*
    @ResponseBody
    @PostMapping("/kakaoPay/{userIdx}")*/

    /**
     * 포인트 충전 직후 충전 내역 조회 API
     * kakaoPaySuccess
     */
    @GetMapping("/kakaoPaySuccess/{userIdx}")
    public String kakaoPaySuccess(@RequestParam("pg_token") String pg_token, Model model, @PathVariable("userIdx") int userIdx) {
        log.info("kakaoPaySuccess get............................................");
        log.info("kakaoPaySuccess pg_token : " + pg_token);

        System.out.println(userIdx);

        model.addAttribute("info", kakaopay.kakaoPayInfo(pg_token, userIdx));

        return null;
    }

    /**
     * 포인트 충전 내역 리스트 조회 API
     * Point/chargePointList/:userIdx
     */
    @ResponseBody
    @GetMapping("Point/chargePointList/{userIdx}")
    public BaseResponse<List<GetchargePointListRes>> chargePointList(@PathVariable("userIdx") int userIdx)
            throws com.there.config.BaseException {

        try{
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdxByJwt != userIdx) return new BaseResponse<>(INVALID_USER_JWT);

            List<GetchargePointListRes> getchargePointListRes = pointProvider.retrieveChargePoint(userIdx, userIdxByJwt);
            return new BaseResponse<>(getchargePointListRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 누적 포인트 내역 조회 API
     * Point/totalPoint/:userIdx
     */
    @ResponseBody
    @GetMapping("Point/totalPoint/{userIdx}")
    public BaseResponse<GetTotalPointRes> getTotalPoint(@PathVariable("userIdx") int userIdx)
            throws com.there.config.BaseException {

        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdxByJwt != userIdx) return new BaseResponse<>(INVALID_USER_JWT);

            GetTotalPointRes getTotalPointRes = pointProvider.findTotalPoint(userIdx, userIdxByJwt);
                return new BaseResponse<>(getTotalPointRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}