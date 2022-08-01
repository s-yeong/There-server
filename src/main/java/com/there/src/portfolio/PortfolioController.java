package com.there.src.portfolio;

import com.there.src.portfolio.config.*;
import com.there.src.portfolio.model.*;
import com.there.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.there.src.portfolio.config.BaseResponseStatus.INVALID_USER_JWT;

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
            //int userIdxByJwt = jwtService.getUserIdx();
            //if (userIdxByJwt != userIdx) return new BaseResponse<>(INVALID_USER_JWT);

            PostPortfolioRes Portfolio = portfolioService.createPortfolios(userIdx, postPortfolioReq);

            return new BaseResponse<>(Portfolio);

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
    public BaseResponse<List<GetPortfolioListRes>> getPortfoliosList(@PathVariable("userIdx") int userIdx) throws com.there.config.BaseException {

        try {
            //int userIdxByJwt = jwtService.getUserIdx();
            //if (userIdxByJwt != userIdx) return new BaseResponse<>(INVALID_USER_JWT);

            List<GetPortfolioListRes> PortfolioListRes = portfolioProvider.getPortfolioList(userIdx);

            return new BaseResponse<>(PortfolioListRes);

        } catch (BaseException exception) {
           return new BaseResponse<>(exception.getStatus());
        }

    }

}
