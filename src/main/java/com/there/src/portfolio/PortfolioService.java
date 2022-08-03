package com.there.src.portfolio;

import com.there.src.portfolio.config.*;
import com.there.src.portfolio.model.PostPortfolioReq;
import com.there.src.portfolio.model.PostPortfolioRes;
import com.there.src.portfolio.model.PostPostInPortfolioRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.there.src.portfolio.config.BaseResponseStatus.CREATE_FAIL_PORTFOLIO;
import static com.there.src.portfolio.config.BaseResponseStatus.CREATE_FAIL_POSTINPORTFOLIO;

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

    /**
     * Portfolio 내 Post 추가
     * @param portfolioIdx
     * @param postIdx
     * @return
     * @throws BaseException
     */
    public PostPostInPortfolioRes createPostInPortfolio(int portfolioIdx, int postIdx) throws BaseException {
        try {
            int contentIdx = portfolioDao.createPostInPortfolio(portfolioIdx, postIdx);
            return new PostPostInPortfolioRes(contentIdx);
        } catch (Exception exception) {
            throw new BaseException(CREATE_FAIL_POSTINPORTFOLIO);
        }
    }

}
