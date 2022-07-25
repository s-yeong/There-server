package com.there.src.chat;

import com.there.src.chat.model.GetChatRoomRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ChatRoomDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    public int createRoom(int senderIdx, int receiverIdx) {

        String createRoomQuery = "insert into chatRoom (senderIdx, receiverIdx) values (?, ?);";
        Object[] createRoomParams = new Object[]{senderIdx, receiverIdx};

        this.jdbcTemplate.update(createRoomQuery, createRoomParams);

        String lastRoomIdxQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastRoomIdxQuery, int.class);

    }

    // 해당 User의 채팅방 목록 조회
    public List<GetChatRoomRes> selectChatRoomList(int userIdx) {
        String getChatRoomListQuery = "sselect  u.nickName, u.profileImgUrl\n" +
                "from    User u\n" +
                "where   u.userIdx in (select  receiverIdx\n" +
                "                      from    chatRoom c, User u\n" +
                "                      where   c.senderIdx = ? and u.userIdx = ?);";
        int getChatRoomListParams = userIdx;

        return this.jdbcTemplate.query(getChatRoomListQuery, (rs, rowNum) -> new GetChatRoomRes(
                rs.getString("nickName"),
                rs.getString("profileImgUrl")), getChatRoomListParams);
    }

    // 채팅방 삭제
    public int deleteChatRoom(int roomIdx) {

        String deleteChatRoomQuery = "UPDATE ChatRoom SET status = 'INACTIVE' WHERE roomIdx = ?";
        int deletePostParams = roomIdx;

        return this.jdbcTemplate.update(deleteChatRoomQuery, roomIdx);

    }

    // chatRoom 가져오기
    public int selectRoomIdx(int senderIdx, int receiverIdx) {

        String getRoomIdxQuery = "select roomIdx from chatRoom where sendIdx = ? and receiverIdx = ?";
        Object[] getRoomIdxParams = new Object[]{senderIdx, receiverIdx};

        return this.jdbcTemplate.queryForObject(getRoomIdxQuery, int.class, getRoomIdxParams);
    }


}
