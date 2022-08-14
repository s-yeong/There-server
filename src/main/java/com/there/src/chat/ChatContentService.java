package com.there.src.chat;

import com.there.config.*;
import com.there.src.chat.model.MessagechatContentReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.there.config.BaseResponseStatus.*;

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

    public void deleteChatContent(int contentIdx) throws BaseException {
        try {
            int result = chatContentDao.deleteChatContent(contentIdx);
            if (result == 0) throw new BaseException(DELETE_FAIL_CHATCONTENT); // 삭제 확인 (0 : 실패 / 1 : 성공)
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void checkChatContent(int roomIdx) throws BaseException {
        try {
            int result = chatContentDao.checkChatContent(roomIdx);
            if (result == 0) throw new BaseException(CHECK_FAIL_CHATCONTENT); // 삭제 확인 (0 : 실패 / 1 : 성공)
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }
}
