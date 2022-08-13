package com.there.src.point;

import com.there.src.point.config.BaseException;
import com.there.src.point.config.BaseResponse;
import com.there.utils.JwtService;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.there.src.point.model.*;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.there.src.point.config.BaseResponseStatus.*;



@RestController
@Controller
public class PointController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Setter(onMethod_ = @Autowired)
    private final KakaoPay kakaopay;

    private final JwtService jwtService;
    private final PointProvider pointProvider;
    private KakaoPayReadyVO kakaoPayReadyVO;

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
    public BaseResponse<String> kakaoPay(@PathVariable("userIdx") int userIdx,
            @RequestBody PostPointReq postpointReq)
            throws com.there.config.BaseException {

        int userIdxByJwt = jwtService.getUserIdx1(jwtService.getJwt());
        if (userIdxByJwt != userIdx) {
            return new BaseResponse<>(INVALID_USER_JWT);
        }
        kakaopay.kakaoPayReady(userIdx, postpointReq);

        String url = kakaopay.kakaoPayReady(userIdx, postpointReq);
        System.out.println(url);

        return new BaseResponse<>(url);
    }


    /**
     * 포인트 충전 직후 충전 내역 조회 API
     * kakaoPaySuccess
     */
    @GetMapping("/kakaoPaySuccess/{userIdx}")
    public BaseResponse<String> kakaoPaySuccess(@RequestParam("pg_token") String pg_token, @PathVariable("userIdx") int userIdx) {

        kakaopay.kakaoPayInfo(pg_token, userIdx);

        String result = "포인트 충전을 완료하였습니다.";
        return new BaseResponse<>(result);
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
            int userIdxByJwt = jwtService.getUserIdx1(jwtService.getJwt());
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
            int userIdxByJwt = jwtService.getUserIdx1(jwtService.getJwt());
            if (userIdxByJwt != userIdx) return new BaseResponse<>(INVALID_USER_JWT);

            GetTotalPointRes getTotalPointRes = pointProvider.findTotalPoint(userIdx, userIdxByJwt);
                return new BaseResponse<>(getTotalPointRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PostMapping("Point/cancle/{pointIdx}")
    public BaseResponse<String> canclePoint(@PathVariable("pointIdx") int pointIdx) {

        kakaopay.kakaoPayCancle(pointIdx);

        String result = "포인트 환불 되었습니다.";
        return new BaseResponse<>(result);
    }
}