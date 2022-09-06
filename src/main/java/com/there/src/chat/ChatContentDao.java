package com.there.src.chat;

import com.there.src.chat.model.GetChatContentRes;
import com.there.src.chat.model.MessagechatContentReq;
import com.there.src.chat.model.MessagechatContentRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ChatContentDao {

    private JdbcTemplate jdbcTemplate;
    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * 메세지 생성
     */
    public int createContent(int roomIdx, MessagechatContentReq messagechatContentReq) {
        String createRoomQuery = "insert into chatRoom (roomIdx, content) values (?, ?);";
        Object[] createRoomParams = new Object[]{roomIdx, messagechatContentReq.getContent()};

        this.jdbcTemplate.update(createRoomQuery, createRoomParams);

        String lastContentIdxQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastContentIdxQuery, int.class);

    }

    /**
     * 메시지 가져오기
     */
    public MessagechatContentRes getChatContent(int senderIdx, int receiverIdx, int contentIdx) {

        String getChatContentQuery = "select c.roomIdx, s.nickName, r.nickName, c.content, c.created_At\n" +
                "from chatContent c, " +
                "(select nickName from User where userIdx = ?) s, " +
                "(select nickName from User where userIdx = ?) r\n" +
                "where contentIdx = ?;";
        Object[] getChatContentParams = new Object[]{senderIdx, receiverIdx, contentIdx};

        return this.jdbcTemplate.queryForObject(getChatContentQuery, (rs, rowNum) -> new MessagechatContentRes(
                    rs.getInt("roomIdx"),
                    rs.getString("senderId"),
                    rs.getString("receiverId"),
                    rs.getString("content"),
                    rs.getString("created_At")), getChatContentParams);
    }

    // 채팅방 메시지 조회(자신)
    public List<GetChatContentRes> selectChatContentList(int roomIdx, int senderIdx) {
        String selectChatContentQuery = "select      cc.senderIdx userIdx, content, cc.created_At\n" +
                "from        chatContent cc left join chatRoom cr on cc.roomIdx = cr.roomIdx and cr.status = 'ACTIVE'\n" +
                "where       cc.status = 'ACTIVE' and cc.roomIdx = ? and cc.senderIdx = ?;";
        Object[] selectChatContentParams = new Object[]{roomIdx, senderIdx};

        return this.jdbcTemplate.query(selectChatContentQuery, (rs, rowNum) ->  new GetChatContentRes(
                rs.getInt("userIdx"),
                rs.getString("content"),
                rs.getString("created_At")),selectChatContentParams);
    }



    public int deleteChatContent(int contentIdx) {
        String deleteChatContentQuery = "UPDATE chatContent SET status = 'INACTIVE' WHERE contentIdx = ?";
        int deleteChatContentParams = contentIdx;

        return this.jdbcTemplate.update(deleteChatContentQuery, deleteChatContentParams);
    }

    // 메시지 확인
    public int checkChatContent(int roomIdx, int senderIdx) {
        String updateChatContentQuery = "UPDATE  chatContent\n" +
                "SET     `read` = 1\n" +
                "WHERE   status = 'ACTIVE' and roomIdx = ? and senderIdx = ?;";
        Object[] updateChatContentParams = new Object[]{roomIdx, senderIdx};

        return this.jdbcTemplate.update(updateChatContentQuery, updateChatContentParams);
    }

}
