package com.there.src.artistStatement;

import com.there.config.BaseException;
import com.there.src.artistStatement.model.PostArtistStatementReq;
import com.there.src.artistStatement.model.PostArtistStatementRes;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.there.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class ArtistStatementService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ArtistStatementDao artistStatementDao;
    private final ArtistStatementProvider artistStatementProvider;


    public PostArtistStatementRes createStatement(int userIdx, PostArtistStatementReq postArtistStatementReq)
            throws BaseException {

        if(artistStatementProvider.checkStatementExist(userIdx) == 1){
            throw new BaseException(STATEMENTS_EXIST);
        }

        try{

            int statementIdx = artistStatementDao.insertStatement(userIdx, postArtistStatementReq);
            return new PostArtistStatementRes(statementIdx);
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);

        }
    }



}