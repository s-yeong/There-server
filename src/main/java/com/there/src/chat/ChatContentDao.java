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
     * 메세시 생성
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

    // 채팅방 조회
    public List<GetChatContentRes> selectChatContentList(int roomIdx) {
        String selectChatContentQuery = "select  content, created_At\n" +
                "from    chatContent\n" +
                "where   roomIdx = ? and status = 'ACTIVE';";
        int selectChatContentParams = roomIdx;

        return this.jdbcTemplate.query(selectChatContentQuery, (rs, rowNum) ->  new GetChatContentRes(
                rs.getString("content"),
                rs.getString("created_At")),selectChatContentParams);
    }

    public int deleteChatContent(int contentIdx) {
        String deleteChatContentQuery = "UPDATE chatContent SET status = 'INACTIVE' WHERE contentIdx = ?";
        int deleteChatContentParams = contentIdx;

        return this.jdbcTemplate.update(deleteChatContentQuery, deleteChatContentParams);
    }

    // 메시지 확인
    public int checkChatContent(int roomIdx) {
        String updateChatContentQuery = "UPDATE  chatContent\n" +
                "SET     `check` = 1\n" +
                "WHERE   status = 'ACTIVE' and roomIdx = ?;";
        int updateChatContentParams = roomIdx;

        return this.jdbcTemplate.update(updateChatContentQuery, updateChatContentParams);
    }

    // 안 읽은 메시지 수 카운트
    public int selectUnCheckCount(int userIdx) {
        String selectUncheckCountQuery = "select count(*) count\n" +
                "from chatContent\n" +
                "where roomIdx in (select roomIdx from chatRoom where senderIdx = ?) and `check` = 0 and status = 'ACTIVE';";
        int selectUnCheckCountParams = userIdx;

        return this.jdbcTemplate.queryForObject(selectUncheckCountQuery, int.class, selectUnCheckCountParams);
    }
}
