package com.there.src.portfolio;

import com.there.src.portfolio.config.*;
import com.there.src.portfolio.model.GetPortfolioListRes;
import com.there.src.portfolio.model.GetPortfolioRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.there.src.portfolio.config.BaseResponseStatus.GET_FAIL_LIST;
import static com.there.src.portfolio.config.BaseResponseStatus.GET_FAIL_PORTFOLIO;

@Service
public class PortfolioProvider {

    private final PortfolioDao portfolioDao;

    @Autowired
    public PortfolioProvider(PortfolioDao portfolioDao) {
        this.portfolioDao = portfolioDao;
    }

    /**
     * Portfolio List 조회
     * @param userIdx
     * @return
     * @throws BaseException
     */
    public List<GetPortfolioListRes> getPortfolioList(int userIdx) throws BaseException {
        try {
            List<GetPortfolioListRes> getPortfolioListRes = portfolioDao.getPortfolioList(userIdx);
           return getPortfolioListRes;
        } catch (Exception exception) {
            throw new BaseException(GET_FAIL_LIST);
        }
    }

    /**
     * Portfolio 조회
     * @param portfolioIdx
     * @return
     * @throws BaseException
     */
    public List<GetPortfolioRes> getPortfolios(int portfolioIdx) throws BaseException {
        try {
            List<GetPortfolioRes> getPortfolioRes = portfolioDao.getPortfolios(portfolioIdx);
            return getPortfolioRes;
        } catch (Exception exception) {
            throw new BaseException(GET_FAIL_PORTFOLIO);
        }
    }
}
