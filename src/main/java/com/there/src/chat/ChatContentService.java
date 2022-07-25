package com.there.src.chat;

import com.there.config.*;
import com.there.src.chat.model.MessagechatContentReq;
import com.there.src.chat.model.MessagechatContentRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.there.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class ChatContentService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ChatRoomDao chatRoomDao;
    private final ChatContentDao chatContentDao;

    @Autowired
    public ChatContentService(ChatRoomDao chatRoomDao, ChatContentDao chatContentDao) {
        this.chatRoomDao = chatRoomDao;
        this.chatContentDao = chatContentDao;
    }

    /**
     * 메시지 생성
     */
    public int createContent
            (int senderIdx, int receiverIdx, MessagechatContentReq messagechatContentReq) throws BaseException {

        try {
            int roomIdx = chatRoomDao.selectRoomIdx(senderIdx, receiverIdx);
            int contentIdx = chatContentDao.createContent(roomIdx, messagechatContentReq);
            return contentIdx;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    /**
     * 메시지 가져오기
     */
    public MessagechatContentRes getChatContent(int senderIdx, int receiverIdx, int contentIdx) throws BaseException {

        MessagechatContentRes result = chatContentDao.getChatContent(senderIdx, receiverIdx, contentIdx);
        return result;

    }
}
