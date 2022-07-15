package com.there.src.follow;

import com.there.src.follow.model.PostFollowReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
@Repository
public class FollowDao {

    private JdbcTemplate jdbcTemplate;
    @Resource
    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int follow(int followeeIdx, PostFollowReq postFollowReq){
        String FollowQuery = "insert into Follow (followeeIdx, followerIdx) values (?,?);";
        Object[] FollowParams = new Object[]{followeeIdx, postFollowReq.getFollowerIdx()};
        String lastinsertIdxQuery = "select last_insert_id()";

        this.jdbcTemplate.update(FollowQuery, FollowParams);

        return this.jdbcTemplate.queryForObject(lastinsertIdxQuery, int.class);
    }

    public int unfollow(int followIdx) {
        String unFollowQuery = "update Follow SET status = 'INACTIVE' where followIdx = ?;";
        int unFollowParam = followIdx;
        return this.jdbcTemplate.update(unFollowQuery, unFollowParam);
    }
}
