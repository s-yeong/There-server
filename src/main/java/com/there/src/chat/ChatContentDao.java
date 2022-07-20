package com.there.src.chat;

import com.there.src.chat.model.MessagechatContentReq;
import com.there.src.chat.model.MessagechatContentRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class ChatContentDao {

    private JdbcTemplate jdbcTemplate;
    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int createContent(int roomIdx, MessagechatContentReq messagechatContentReq) {
        String createRoomQuery = "insert into chatRoom (roomIdx, content) values (?, ?);";
        Object[] createRoomParams = new Object[]{roomIdx, messagechatContentReq.getContent()};

        this.jdbcTemplate.update(createRoomQuery, createRoomParams);

        String lastContentIdxQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastContentIdxQuery, int.class);

    }

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
}
