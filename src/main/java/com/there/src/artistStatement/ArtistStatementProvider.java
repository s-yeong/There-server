package com.there.src.artistStatement;

import com.there.config.BaseException;
import com.there.src.artistStatement.model.GetArtistStatementRes;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static com.there.config.BaseResponseStatus.DATABASE_ERROR;

@Service
@RequiredArgsConstructor
public class ArtistStatementProvider {

    private final ArtistStatementDao artistStatementDao;

    final Logger logger = LoggerFactory.getLogger(this.getClass());


    // 작가노트 조회
    public GetArtistStatementRes retrieveStatement(int userIdx) throws BaseException {
        try {
            GetArtistStatementRes getArtistStatementRes = artistStatementDao.selectStatement(userIdx);
            return getArtistStatementRes;

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }



}
