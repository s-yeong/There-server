package com.there.src.chat;

import com.there.config.*;
import com.there.src.chat.model.GetChatContentRes;
import com.there.src.chat.model.MessagechatContentRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.there.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class ChatContentProvider {

    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ChatContentDao chatContentDao;

    @Autowired
    public ChatContentProvider(ChatContentDao chatContentDao) {
        this.chatContentDao = chatContentDao;
    }

    /**
     * 채팅방 콘텐츠 조회
     * 받은 메시지 일 경우 senderIdx = 상대방 Idx
     * 보낸 메세지 일 경우 senderIdx = 자신 Idx
     */
    public List<GetChatContentRes> retrieveChatContent(int roomIdx, int senderIdx) throws BaseException {
        try {
            List<GetChatContentRes> getChatContentList = chatContentDao.selectChatContentList(roomIdx, senderIdx);
            chatContentDao.checkChatContent(roomIdx, senderIdx);
            return getChatContentList;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }


    /**
     * 메시지 가져오기
     */
    public MessagechatContentRes getChatContent(int senderIdx, int receiverIdx, int contentIdx) {

        MessagechatContentRes result = chatContentDao.getChatContent(senderIdx, receiverIdx, contentIdx);
        return result;

    }
}
