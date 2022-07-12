package com.there.src.history;

import com.there.config.BaseException;
import com.there.src.history.model.*;
import com.there.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.there.config.BaseResponseStatus.*;

@Service
public class HistoryService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final HistoryDao historyDao;
    private final HistoryProvider historyProvider;
    private final JwtService jwtService;


    @Autowired
    public HistoryService(HistoryDao historyDao, HistoryProvider historyProvider, JwtService jwtService) {
        this.historyDao = historyDao;
        this.historyProvider = historyProvider;
        this.jwtService = jwtService;

    }

    // 히스토리 작성
    public PostHistoryRes createHistory(int userIdx, PostHistoryReq postHistoryReq) throws BaseException {

        try{

            // 히스토리 DB에서 생성시 히스토리 식별자 Dao에서 가져옴
            int historyIdx = historyDao.insertHistory(userIdx, postHistoryReq);

            for(int i = 0; i < postHistoryReq.getPostHistoryPicturesReq().size(); i++){
                historyDao.insertHistoryPicture(historyIdx, postHistoryReq.getPostHistoryPicturesReq().get(i));
            }

            return new PostHistoryRes(historyIdx);
        }
        catch (Exception exception) {

            throw new BaseException(DATABASE_ERROR);

        }
    }

    // 히스토리 삭제
    public void deleteHistory(int userIdx, int historyIdx) throws BaseException{


        if(historyProvider.checkUserExist(userIdx) == 0){
            throw new BaseException(USERS_EMPTY_USER_ID);
        }

        if(historyProvider.checkHistoryExist(historyIdx) == 0){
            throw new BaseException(HISTORYS_EMPTY_HISTORY_ID);
        }

        // 이 히스토리의 userIdx가 맞는지
        if(historyProvider.checkUserHistoryExist(userIdx, historyIdx) == 0){
            throw new BaseException(USERS_HISTORYS_INVALID_ID);
        }

        try{

            int result = historyDao.deleteHistory(historyIdx);
            if(result == 0){
                throw new BaseException(DELETE_FAIL_HISTORY);
            }

        }
        catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
