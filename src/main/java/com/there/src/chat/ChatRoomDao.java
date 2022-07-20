package com.there.src.chat;

import com.there.src.chat.model.ChatContent;
import com.there.src.chat.model.PostChatRoomRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

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




}
