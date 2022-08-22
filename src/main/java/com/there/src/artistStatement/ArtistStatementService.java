package com.there.src.artistStatement;

import com.there.config.BaseException;
import com.there.src.history.model.PatchHistoryReq;
import com.there.src.history.model.PostHistoryReq;
import com.there.src.history.model.PostHistoryRes;
import com.there.src.s3.S3Service;
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



}