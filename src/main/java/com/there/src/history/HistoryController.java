package com.there.src.history;

import com.there.config.BaseException;
import com.there.config.BaseResponse;
import com.there.src.history.model.*;
import com.there.utils.JwtService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/historys")
public class HistoryController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final HistoryProvider historyProvider;
    @Autowired
    private final HistoryService historyService;
    @Autowired
    private final JwtService jwtService;




    public HistoryController(HistoryProvider historyProvider, HistoryService historyService, JwtService jwtService){
        this.historyProvider = historyProvider;
        this.historyService = historyService;
        this.jwtService = jwtService;
    }

    /**
     * 히스토리 조회 API   -- ex) "히스토리 제목" 누르면 그 히스토리 조회
     * [GET] /historys/:historyIdx
     * @return BaseResponse<getHistoryRes>
     */
    @ResponseBody
    @GetMapping("/{historyIdx}")
    public BaseResponse<GetHistoryRes> getHistory(@PathVariable("historyIdx")int historyIdx) {
        try {

            GetHistoryRes getHistoryRes = historyProvider.findHistory(historyIdx);
            return new BaseResponse<>(getHistoryRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 히스토리 리스트 조회 API  -- ex) "히스토리의 제목" + 날짜들이 리스트로 조회
     * [GET] /historys?postIdx=
     * @return BaseResponse<getHistoryListRes>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetHistoryListRes>> getHistoryList(@RequestParam int postIdx) {
        try{

            List<GetHistoryListRes> getHistoryListRes = historyProvider.retrieveHistorys(postIdx);
            return new BaseResponse<>(getHistoryListRes);

        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 히스토리 작성 API
     * [POST] /historys
     * @return BaseResponse<postHistoryRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostHistoryRes> createHistory(@RequestBody PostHistoryReq postHistoryReq) {


        try {

            // 유저 로그인 API 작성 완료시 주석해제
            // int userIdxByJwt = jwtService.getUserIdx();

            //PostHistoryRes postHistoryRes = historyService.createHistory(userIdxByJwt, postHistoryReq);
            PostHistoryRes postHistoryRes = historyService.createHistory(postHistoryReq.getUserIdx(), postHistoryReq);
            return new BaseResponse<>(postHistoryRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }



}

