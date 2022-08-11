package com.there.src.portfolio;

import com.there.config.*;
import com.there.src.portfolio.model.*;
import com.there.utils.JwtService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.there.config.BaseResponseStatus.EMPTY_TITLE;
import static com.there.config.BaseResponseStatus.INVALID_USER_JWT;

@Api
@RestController
@RequestMapping("/portfolio")
public class PortfolioController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final JwtService jwtService;
    private final PortfolioService portfolioService;
    private final PortfolioProvider portfolioProvider;

    @Autowired
    public PortfolioController(JwtService jwtService, PortfolioService portfolioService, PortfolioProvider portfolioProvider) {
        this.jwtService = jwtService;
        this.portfolioService = portfolioService;
        this.portfolioProvider = portfolioProvider;
    }

    @ApiOperation(value="Portfolio 생성 API", notes="포트폴리오 제목 반드시 입력 필요")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @PostMapping("/{userIdx}")
    public BaseResponse<PostPortfolioRes> createPortfolios
            (@PathVariable("userIdx") int userIdx, @RequestBody PostPortfolioReq postPortfolioReq) throws com.there.config.BaseException {

        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdxByJwt != userIdx) return new BaseResponse<>(INVALID_USER_JWT);
            if (postPortfolioReq.getTitle() == null) return new BaseResponse<>(EMPTY_TITLE);

            PostPortfolioRes Portfolio = portfolioService.createPortfolios(userIdx, postPortfolioReq);
            return new BaseResponse<>(Portfolio);

        } catch (BaseException exception) {

            return new BaseResponse<>(exception.getStatus());

        }
    }

    @ApiOperation(value="Portfolio 내 Post 추가 API", notes="")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @PostMapping("/{portfolioIdx}/post/{postIdx}")
    public BaseResponse<PostPostInPortfolioRes> createPostInPortfolio
            (@PathVariable("portfolioIdx")int portfolioIdx, @PathVariable("postIdx")int postIdx) {

        PostPostInPortfolioRes PostInPortfolioRes = portfolioService.createPostInPortfolio(portfolioIdx, postIdx);
        return new BaseResponse<>(PostInPortfolioRes);
    }


    @ApiOperation(value="Portfolio 리스트 조회 API", notes="포트폴리오 클릭 시 해당 유저의 포트폴리오 리스트 조회")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @GetMapping("/{userIdx}")
    public BaseResponse<List<GetPortfolioListRes>> getPortfolioList(@PathVariable("userIdx") int userIdx) throws com.there.config.BaseException {

        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdxByJwt != userIdx) return new BaseResponse<>(INVALID_USER_JWT);

            List<GetPortfolioListRes> PortfolioListRes = portfolioProvider.getPortfolioList(userIdx);

            return new BaseResponse<>(PortfolioListRes);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }

    @ApiOperation(value="Portfolio 내 Post 조회 API", notes="포트폴리오 제목 클릭 시 조회")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @GetMapping("/{portfolioIdx}")
    public BaseResponse<List<GetPortfolioRes>> getPortfolios (@PathVariable("portfolioIdx") int portfolioIdx) {
        List<GetPortfolioRes> PortfolioListRes = portfolioProvider.getPortfolios(portfolioIdx);
        return new BaseResponse<>(PortfolioListRes);

    }

    @ResponseBody
    @PatchMapping("/modify/{portfolioIdx}")
    public BaseResponse<String> ModifyPortfolioTitle
            (@PathVariable("portfolioIdx")int portfolioIdx, @RequestBody PatchPortfolioReq patchPortfolioReq) throws BaseException {

        portfolioService.ModifyPortfolioTitle(portfolioIdx, patchPortfolioReq);
        String result = "포트폴리오 이름 변경 하였습니다.";
        return new BaseResponse<>(result);

    }

    @ApiOperation(value="Portfolio 삭제 API", notes="")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @PatchMapping("/{portfolioIdx}")
    public BaseResponse<String> deletePortfolio (@PathVariable("portfolioIdx") int portfolioIdx) throws BaseException {

        portfolioService.deletePortfolio(portfolioIdx);
        String result = "포트폴리오 삭제를 성공했습니다.";
        return new BaseResponse<>(result);

    }

    @ApiOperation(value="Portfolio 내 Post 삭제 API", notes="")
    @ApiResponses({
            @ApiResponse(code = 1000, message = "요청 성공"),
            @ApiResponse(code = 4000, message = "서버 에러")
    })
    @ResponseBody
    @PatchMapping("/{contentIdx}")
    public BaseResponse<String> deletePostInPortfolio (@PathVariable("contentIdx") int contentIdx) throws BaseException {

        portfolioService.deletePostInPortfolio(contentIdx);
        String result = "포트폴리오 내 게시물 삭제를 성공했습니다.";
        return new BaseResponse<>(result);

    }

}
