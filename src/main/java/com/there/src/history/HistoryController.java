package com.there.src.history;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.there.config.*;
import com.there.src.history.model.*;
import com.there.utils.JwtService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/historys")
public class HistoryController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final HistoryProvider historyProvider;
    private final HistoryService historyService;
    private final JwtService jwtService;


    /**
     * 히스토리 조회 API   -- ex) "히스토리 제목" 누르면 그 히스토리 조회
     * [GET] /historys/:historyIdx
     */
    @ApiOperation(value="히스토리 조회 API", notes="히스토리 제목(historyIdx) 누르면 그 히스토리 조회")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다.")
    })
    @ResponseBody
    @GetMapping("/{historyIdx}")
    public BaseResponse<GetHistoryRes> getHistory(@PathVariable("historyIdx") int historyIdx) {
        try {

            GetHistoryRes getHistoryRes = historyProvider.retrieveHistory(historyIdx);
            return new BaseResponse<>(getHistoryRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 히스토리 리스트 조회 API
     * [GET] /historys:postIdx
     */
    @ApiOperation(value="히스토리 리스트 조회 API", notes="히스토리 제목 + 날짜들이 리스트로 조회")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다.")
    })
    @ResponseBody
    @GetMapping("/posts/{postIdx}")
    public BaseResponse<List<GetHistoryListRes>> getHistoryList(@PathVariable("postIdx") int postIdx) {
        try {

            List<GetHistoryListRes> getHistoryListRes = historyProvider.retrieveHistoryList(postIdx);
            return new BaseResponse<>(getHistoryListRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 히스토리 작성 API
     * [POST] /historys
     */
    @ApiOperation(value="히스토리 작성 API", notes="Body 타입 : form-data<jsonList, images>")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2004, message = "제목을 입력해주세요."),
            @ApiResponse(code = 2005, message = "사진을 올려주세요."),
            @ApiResponse(code = 2006, message = "내용을 입력해주세요."),
            @ApiResponse(code = 2007, message = "해당 유저가 아닙니다."),
            @ApiResponse(code = 2008, message = "없는 아이디입니다."),
            @ApiResponse(code = 2103, message = "히스토리 제목의 글자 수를 확인해주세요."),
            @ApiResponse(code = 2104, message = "히스토리 내용의 글자 수를 확인해주세요."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다.")
    })
    @ResponseBody
    @PostMapping(value="", consumes = {"multipart/form-data"})
    public BaseResponse<PostHistoryRes> createHistory(@RequestParam("jsonList") String jsonList,
                                                      @RequestPart(value = "images", required = false) List<MultipartFile> MultipartFiles)
            throws IOException {

        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        PostHistoryReq postHistoryReq = objectMapper.readValue(jsonList, new TypeReference<>() {
        });

        try {


            int userIdxByJwt = jwtService.getUserIdx1(jwtService.getJwt());

            if (postHistoryReq.getTitle() == null) {
                return new BaseResponse<>(BaseResponseStatus.EMPTY_TITLE);
            }

            if (postHistoryReq.getTitle().length() > 45) {
                return new BaseResponse<>(BaseResponseStatus.HISTORYS_INVALID_TITLES);
            }

            if (postHistoryReq.getContent() == null) {
                return new BaseResponse<>(BaseResponseStatus.EMPTY_CONTENT);
            }

            if (postHistoryReq.getContent().length() > 500) {
                return new BaseResponse<>(BaseResponseStatus.HISTORYS_INVALID_CONTENTS);
            }

            if (MultipartFiles == null ) {
                return new BaseResponse<>(BaseResponseStatus.EMPTY_IMGURL);
            }


            PostHistoryRes postHistoryRes = historyService.createHistory(userIdxByJwt, postHistoryReq, MultipartFiles);


            return new BaseResponse<>(postHistoryRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 히스토리 삭제 API
     * [PATCH] /historys/:historyIdx/status
     */
    @ApiOperation(value="히스토리 삭제 API", notes="실제 DB를 삭제하지 않고 status를 DELETED로 변경")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2007, message = "해당 유저가 아닙니다."),
            @ApiResponse(code = 2008, message = "없는 아이디입니다."),
            @ApiResponse(code = 2102, message = "히스토리 아이디 값을 확인해주세요."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다."),
            @ApiResponse(code = 4113, message = "히스토리 삭제를 실패하였습니다."),
    })
    @ResponseBody
    @PatchMapping("/{historyIdx}/status")
    public BaseResponse<String> deleteHistory(@PathVariable("historyIdx") int historyIdx) {

        try {

            int userIdxByJwt = jwtService.getUserIdx1(jwtService.getJwt());
            historyService.deleteHistory(userIdxByJwt, historyIdx);

            String result = "히스토리가 삭제되었습니다.";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 히스토리 수정 API
     * [PATCH] /historys/:historyIdx
     */
    @ApiOperation(value="히스토리 수정 API", notes="Body 타입 : form-data<jsonList, images>")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청에 성공하였습니다."),
            @ApiResponse(code = 2005, message = "변경 사항이 없습니다."),
            @ApiResponse(code = 2007, message = "해당 유저가 아닙니다."),
            @ApiResponse(code = 2008, message = "없는 아이디입니다."),
            @ApiResponse(code = 2102, message = "히스토리 아이디 값을 확인해주세요."),
            @ApiResponse(code = 2103, message = "히스토리 제목의 글자 수를 확인해주세요."),
            @ApiResponse(code = 2104, message = "히스토리 내용의 글자 수를 확인해주세요."),
            @ApiResponse(code = 4000, message = "데이터베이스 연결에 실패하였습니다."),
    })
    @ResponseBody
    @PatchMapping(value = "/modify/{historyIdx}", consumes = {"multipart/form-data"})
    public BaseResponse<String> modifyHistory(@PathVariable("historyIdx") int historyIdx,
                                              @RequestParam("jsonList") String jsonList,
                                              @RequestPart(value = "images", required = false) List<MultipartFile> MultipartFiles)
            throws IOException {

        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        PatchHistoryReq patchHistoryReq = objectMapper.readValue(jsonList, new TypeReference<>() {
        });


        try {

            if(patchHistoryReq.getTitle() == null && patchHistoryReq.getContent() == null && MultipartFiles == null){
                return new BaseResponse<>(BaseResponseStatus.HISTORYS_MODIFY_NOTTHING);
            }

            if(patchHistoryReq.getTitle() != null){
                if (patchHistoryReq.getTitle().length() > 45) {
                    return new BaseResponse<>(BaseResponseStatus.HISTORYS_INVALID_TITLES);
                }
            }

            if(patchHistoryReq.getContent() != null){
                if (patchHistoryReq.getContent().length() > 500) {
                    return new BaseResponse<>(BaseResponseStatus.HISTORYS_INVALID_CONTENTS);
                }
            }

            int userIdxByJwt = jwtService.getUserIdx1(jwtService.getJwt());

            historyService.modifyHistory(userIdxByJwt, historyIdx, patchHistoryReq, MultipartFiles);

            String result = "히스토리가 수정되었습니다.";
            return new BaseResponse<>(result);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


}

