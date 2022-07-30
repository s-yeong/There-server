package com.there.src.point;


import com.there.src.point.config.BaseException;
import com.there.src.point.model.PostPointRes;
import com.there.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import static com.there.src.point.config.BaseResponseStatus.*;
import static com.there.src.point.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class PointService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PointDao pointDao;
    private final JwtService jwtService;

    @Autowired
    public PointService(PointDao pointDao, JwtService jwtService) {
        this.pointDao = pointDao;
        this.jwtService = jwtService;
    }

    // 포인트 충전
    public PostPointRes chargePoint(int userIdx, int amount) throws BaseException {
        try {
            int pointIdx = pointDao.chargePoint(userIdx, amount);
            return new PostPointRes(pointIdx);
        } catch (Exception exception) {
            throw new BaseException(CREATE_FAIL_CHARGE_POINT);
        }
    }
}
