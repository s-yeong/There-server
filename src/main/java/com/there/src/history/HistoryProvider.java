package com.there.src.history;

import com.there.config.BaseException;
import com.there.src.history.model.*;
import com.there.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.there.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class HistoryProvider {

    private final HistoryDao historyDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public HistoryProvider(HistoryDao historyDao, JwtService jwtService) {
        this.historyDao = historyDao;
        this.jwtService = jwtService;
    }

    // 히스토리 조회
    public GetHistoryRes findHistory(int historyIdx) throws BaseException {
        try {
            GetHistoryRes getHistoryRes = historyDao.selectHistory(historyIdx);
            return getHistoryRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
