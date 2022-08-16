package com.there.src.point;

import com.there.config.BaseException;
import com.there.src.point.model.GetTotalPointRes;
import com.there.src.point.model.GetchargePointListRes;
import com.there.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.there.config.BaseResponseStatus.*;

@Service
public class PointProvider {

    private final PointDao pointDao;

    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public PointProvider(PointDao pointDao, JwtService jwtService) {
        this.pointDao = pointDao;
        this.jwtService = jwtService;
    }

    // 포인트 충전 내역 리스트 조회
    public List<GetchargePointListRes> retrieveChargePoint(int userIdx, int userIdxByJwt) throws BaseException {

        try {
            if (userIdxByJwt != userIdx) {
            }
            List<GetchargePointListRes> getchargePointListRes = pointDao.selectChargePointList(userIdx);

            return getchargePointListRes;

        } catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 누적 포인트 내역 조회
    public GetTotalPointRes findTotalPoint(int userIdx, int userIdxByJwt) throws BaseException{
        try {
            if (userIdxByJwt != userIdx) {
            }
            GetTotalPointRes getTotalPointRes = pointDao.selectTotalPoint(userIdx);
            return getTotalPointRes;
        } catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }

    }
}
