package com.there.src.chat;

import com.there.config.BaseException;
import com.there.src.chat.model.GetChatRoomRes;
import com.there.src.chat.model.PostChatRoomRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.there.config.BaseResponseStatus.*;

@Service
public class ChatRoomService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ChatRoomDao chatRoomDao;

    @Autowired
    public ChatRoomService(ChatRoomDao chatRoomDao) {
        this.chatRoomDao = chatRoomDao;
    }

    public PostChatRoomRes createRoom(int senderIdx, int receiverIdx) throws BaseException {

        try {
            int roodIdx = chatRoomDao.selectRoomIdx(senderIdx, receiverIdx);
            return new PostChatRoomRes(roodIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

}
