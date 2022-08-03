package com.there.src.portfolio;

import com.there.src.portfolio.config.*;
import com.there.src.portfolio.model.*;
import com.there.utils.JwtService;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.there.src.portfolio.config.BaseResponseStatus.INVALID_USER_JWT;

@Api
@RestController
@RequestMapping("/portfolio")
public class PortfolioController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final JwtService jwtService;
    private final PortfolioService portfolioService;
    private final PortfolioProvider portfolioProvider;

    public PortfolioController(JwtService jwtService, PortfolioService portfolioService, PortfolioProvider portfolioProvider) {
        this.jwtService = jwtService;
        this.portfolioService = portfolioService;
        this.portfolioProvider = portfolioProvider;
    }

    /**
     * Portfolio 생성 API
     * @param userIdx
     * @param postPortfolioReq
     * @return
     * @throws com.there.config.BaseException
     */
    @ResponseBody
    @PostMapping("/{userIdx}")
    public BaseResponse<PostPortfolioRes> createPortfolios
            (@PathVariable("userIdx") int userIdx, @RequestBody PostPortfolioReq postPortfolioReq) throws com.there.config.BaseException {

        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdxByJwt != userIdx) return new BaseResponse<>(INVALID_USER_JWT);

            PostPortfolioRes Portfolio = portfolioService.createPortfolios(userIdx, postPortfolioReq);

            return new BaseResponse<>(Portfolio);

        } catch (BaseException exception) {

            return new BaseResponse<>(exception.getStatus());

        }
    }

    /**
     * Portfolio 내 Post 추가 APi
     * @param portfolioIdx
     * @param postIdx
     * @return
     */
    @ResponseBody
    @PostMapping("/{portfolioIdx}/post/{postIdx}")
    public BaseResponse<PostPostInPortfolioRes> createPostInPortfolio
            (@PathVariable("portfolioIdx")int portfolioIdx, @PathVariable("postIdx")int postIdx) {

        try {
            PostPostInPortfolioRes PostInPortfolioRes = portfolioService.createPostInPortfolio(portfolioIdx, postIdx);
            return new BaseResponse<>(PostInPortfolioRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * Portfolio List 조회 API
     * @param userIdx
     * @return
     * @throws com.there.config.BaseException
     */
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

    /**
     * Portfolio 조회 API
     * @param portfolioIdx
     * @return
     * @throws com.there.config.BaseException
     */
    @ResponseBody
    @GetMapping("/{portfolioIdx}")
    public BaseResponse<List<GetPortfolioRes>> getPortfolios(@PathVariable("portfolioIdx") int portfolioIdx) throws com.there.config.BaseException {

        List<GetPortfolioRes> PortfolioListRes = portfolioProvider.getPortfolios(portfolioIdx);

        return new BaseResponse<>(PortfolioListRes);

    }


}
