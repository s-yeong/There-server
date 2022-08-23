package com.there.src.point;

import com.there.config.BaseException;
import com.there.config.BaseResponse;
import com.there.utils.JwtService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.there.src.point.model.*;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.there.config.BaseResponseStatus.*;



@RestController
@RequiredArgsConstructor
@Controller
public class PointController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final KakaoPay kakaopay;

    private final JwtService jwtService;
    private final PointProvider pointProvider;
    private KakaoPayReadyVO kakaoPayReadyVO;



    /**
     * 내 포인트 충전 API
     * /kakaoPay/:userIdx
     */
    @ApiOperation(value = "카카오페이 포인트 충전", notes = "Body 타입: String")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @PostMapping("/kakaoPay/{userIdx}")
    public BaseResponse<String> kakaoPay(@PathVariable("userIdx") int userIdx, @RequestBody PostPointReq postpointReq)
            throws BaseException {

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
     * 채팅 시 상대방에게 포인트 결제 API
     * /kakaoPay/:userIdx
     */
    @ApiOperation(value = "카카오페이 포인트 결제", notes = "Body 타입: String")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @PostMapping("/kakaoPay/chat/{userIdx}")
    public BaseResponse<String> chatkakaoPay(@PathVariable("userIdx") int userIdx, @RequestBody PostPointReq postpointReq) {

        kakaopay.kakaoPayReady(userIdx, postpointReq);

        String url = kakaopay.kakaoPayReady(userIdx, postpointReq);
        System.out.println(url);

        return new BaseResponse<>(url);
    }


    /**
     * 포인트 충전 직후 내역 조회 API
     * /kakaoPaySuccess/:userIdx
     */
    @ApiOperation(value = "포인트(단건 결제) 충전 내역", notes = "PathVariable로 들어온 userIdx의 포인트 충전 단건결제 내역 조회")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @GetMapping("/kakaoPaySuccess/{userIdx}")
    public BaseResponse<String> kakaoPaySuccess(@RequestParam("pg_token") String pg_token, @PathVariable("userIdx") int userIdx) {

        kakaopay.kakaoPayInfo(pg_token, userIdx);

        String result = "포인트 충전을 완료하였습니다.";
        return new BaseResponse<>(result);
    }

    /**
     * 포인트 충전 내역 리스트 조회 API
     * /kakaoPay/chargePointList/:userIdx
     */
    @ApiOperation(value = "포인트 충전 내역 리스트", notes = "PathVariable로 들어온 userIdx의 포인트 충전 내역 리스트 조회")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @GetMapping("/kakaoPay/chargePointList/{userIdx}")
    public BaseResponse<List<GetchargePointListRes>> chargePointList(@PathVariable("userIdx") int userIdx)
            throws BaseException {

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
     * /kakaoPay/totalPoint/:userIdx
     */
    @ApiOperation(value = "누적 포인트 내역", notes = "PathVariable로 들어온 userIdx의 누적 포인트 내역 조회")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @GetMapping("/kakaoPay/totalPoint/{userIdx}")
    public BaseResponse<GetTotalPointRes> getTotalPoint(@PathVariable("userIdx") int userIdx)
            throws BaseException {

        try {
            int userIdxByJwt = jwtService.getUserIdx1(jwtService.getJwt());
            if (userIdxByJwt != userIdx) return new BaseResponse<>(INVALID_USER_JWT);

            GetTotalPointRes getTotalPointRes = pointProvider.findTotalPoint(userIdx, userIdxByJwt);
                return new BaseResponse<>(getTotalPointRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 카카오페이 환불 API
     * /kakaoPay/cancel/:pointIdx
     */
    @ApiOperation(value = "카카오페이 환불", notes = "PathVariable로 들어온 pointIdx 포인트 status 'DELETED'로 변경")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @PostMapping("/kakaoPay/cancel/{pointIdx}")
    public BaseResponse<String> canclePoint(@PathVariable("pointIdx") int pointIdx) {

        kakaopay.kakaoPayCancle(pointIdx);

        String result = "포인트 환불 되었습니다.";
        return new BaseResponse<>(result);
    }
}