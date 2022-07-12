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

}
