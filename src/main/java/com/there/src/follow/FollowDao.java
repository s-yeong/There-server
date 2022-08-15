package com.there.src.follow;

import com.there.src.follow.model.GetFollowerListRes;
import com.there.src.follow.model.GetFollowingListRes;
import com.there.src.follow.model.PostFollowReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;
import java.util.Locale;

@Repository
public class FollowDao {

    private JdbcTemplate jdbcTemplate;
    @Resource
    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int follow(int userIdx, int followeeIdx){
        String FollowQuery = "insert into Follow (followerIdx, followeeIdx) values (?,?);";
        Object[] FollowParams = new Object[]{userIdx, followeeIdx};
        String lastinsertIdxQuery = "select last_insert_id()";

        this.jdbcTemplate.update(FollowQuery, FollowParams);

        return this.jdbcTemplate.queryForObject(lastinsertIdxQuery, int.class);
    }

    public int unfollow(int followIdx) {
        String unFollowQuery = "update Follow SET status = 'INACTIVE' where followIdx = ?;";
        int unFollowParam = followIdx;
        return this.jdbcTemplate.update(unFollowQuery, unFollowParam);
    }

    public List<GetFollowerListRes> selectFollowerList(int userIdx) {
        String selectFollowerListQuery ="select  u.nickName, u.profileImgUrl\n" +
                "from User u, Follow f\n" +
                "where u.userIdx = f.followeeIdx and f.followerIdx = ? and f.status = 'ACTIVE'";
        Object[] selectFollowerParam = new Object[]{userIdx};
        return this.jdbcTemplate.query(selectFollowerListQuery,
                (rs, rowNum) -> new GetFollowerListRes(
                        rs.getString("nickName"),
                        rs.getString("profileImgUrl")
                ),selectFollowerParam);
    }

    public List<GetFollowingListRes> selectFollowingList(int userIdx){
        String selectFollowingListQuery = "select u.nickName, u.profileImgUrl\n" +
                "from User u, Follow f\n" +
                "where u.userIdx = f.followerIdx and f.followeeIdx = ? and f.status = 'ACTIVE'";
        Object[] selectFollowingParam = new Object[]{userIdx};
        return this.jdbcTemplate.query(selectFollowingListQuery,
                (rs, rowNum) -> new GetFollowingListRes(
                        rs.getString("nickName"),
                        rs.getString("profileImgUrl")
                ),selectFollowingParam);
    }
}
