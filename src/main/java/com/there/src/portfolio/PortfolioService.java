package com.there.src.portfolio;

import com.there.src.portfolio.config.*;
import com.there.src.portfolio.model.PostPortfolioReq;
import com.there.src.portfolio.model.PostPortfolioRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.there.src.portfolio.config.BaseResponseStatus.CREATE_FAIL_PORTFOLIO;

@Service
public class PortfolioService {

    private final PortfolioDao portfolioDao;

    @Autowired
    public PortfolioService(PortfolioDao portfolioDao) {
        this.portfolioDao = portfolioDao;
    }

    /**
     *  Portfolio 생성
     */
    public PostPortfolioRes createPortfolios(int userIdx, PostPortfolioReq postPortfolioReq) throws BaseException {
        try {
            int portfolioIdx = portfolioDao.createPortfolios(userIdx, postPortfolioReq);
            return new PostPortfolioRes(portfolioIdx);
        } catch (Exception exception) {
            throw new BaseException(CREATE_FAIL_PORTFOLIO);
        }
    }
}
