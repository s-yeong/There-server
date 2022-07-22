package com.there.src.chat;

import com.there.config.BaseException;
import com.there.config.BaseResponse;
import com.there.src.chat.model.MessagechatContentReq;
import com.there.src.chat.model.MessagechatContentRes;
import com.there.src.chat.model.PostChatRoomRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatContentService chatContentService;
    private final ChatRoomService chatRoomService;

    @Autowired
    public ChatController(SimpMessagingTemplate messagingTemplate, ChatContentService chatContentService, ChatRoomService chatRoomService) {
        this.messagingTemplate = messagingTemplate;
        this.chatContentService = chatContentService;
        this.chatRoomService = chatRoomService;
    }

    /**
     * ChatRoom 생성 API
     * /chat/room/{senderIdx}/receiverIdx}
     */
    @PostMapping("/room/{senderIdx}/{receiverIdx}")
    public BaseResponse<PostChatRoomRes> createRoom
            (@PathVariable("senderIdx") int senderIdx, @PathVariable("receiverIdx")int receiverIdx) {
        try {
            PostChatRoomRes postChatRoomRes = chatRoomService.createRoom(senderIdx, receiverIdx);
            return new BaseResponse<>(postChatRoomRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * Message 전송 API
     * app/chat/content/{sendIdx}/{receiverIdx}
     */
    @MessageMapping("content/{sendIdx}/{receiverIdx}")
    public void createContent
    (@PathVariable("senderIdx") int senderIdx, @PathVariable("receiverIdx")int receiverIdx,
     @Payload MessagechatContentReq messagechatContentReq) throws BaseException {

            // 생성 된 Content 가져오기
            int contentIdx = chatContentService.createContent(senderIdx, receiverIdx, messagechatContentReq);
            MessagechatContentRes messagechatContentRes = chatContentService.getChatContent(senderIdx, receiverIdx, contentIdx);

            // Content 전달
            messagingTemplate.convertAndSendToUser
                    (messagechatContentRes.getSenderId(), "/queue/message", messagechatContentRes);
    }

}
