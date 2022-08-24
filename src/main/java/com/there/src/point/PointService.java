package com.there.src.point;


import com.there.config.BaseException;
import com.there.src.point.model.PostPointRes;
import com.there.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.there.config.BaseResponseStatus.*;


@Service
@RequiredArgsConstructor
public class PointService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final PointDao pointDao;

    // 포인트 충전
    public PostPointRes chargePoint(int userIdx, int amount, int tax_free_amount, String tid) throws BaseException {
        try {
            int pointIdx = pointDao.chargePoint(userIdx, amount, tax_free_amount, tid);
            return new PostPointRes(pointIdx);
        } catch (Exception exception) {
            throw new BaseException(CREATE_FAIL_CHARGE_POINT);
        }
    }
}
