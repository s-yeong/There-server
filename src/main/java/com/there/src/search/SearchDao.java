package com.there.src.search;

import com.there.src.search.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;

@Repository
public class SearchDao {

    private JdbcTemplate jdbcTemplate;

    @Resource
    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    // 계정 검색
    public List<GetSearchByAccountRes> selectAccountList(String account){
        String selectAccountListQuery = "select userIdx, name, nickName, profileImgUrl from User where status = 'ACTIVE' and nickName = ?\n" +
                "UNION\n" +
                "select userIdx, name, nickName, profileImgUrl from User where status = 'ACTIVE' and name = ?;";
        Object[] selectAccountListParams = new Object[] {account, account};
        return this.jdbcTemplate.query(selectAccountListQuery,
                (rs, rowNum) -> new GetSearchByAccountRes(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("nickName"),
                        rs.getString("profileImgUrl")
                ), selectAccountListParams);
    }

    // 해시태그 검색
    public List<GetSearchByHashtagRes> selectHashtagList(String hashtag){
        String selectTagIdxQuery = "select tagIdx from Tag where name = ?;";
        String selectHashtagListQuery = "select t.tagIdx as tagIdx, t.name as hashtag, if(postCount is null, 0, postCount) as postCount\n" +
                "from Tag as t\n" +
                "    left join (select tagIdx, count(postIdx) as postCount\n" +
                "    from PostTag where tagIdx = ?) pt on pt.tagIdx = t.tagIdx\n" +
                "where t.tagIdx = ?;";

        int tagIdx = this.jdbcTemplate.queryForObject(selectTagIdxQuery, int.class, hashtag);
        Object[] selectHashtagListParams = new Object[] {tagIdx, tagIdx};
        return this.jdbcTemplate.query(selectHashtagListQuery,
                (rs, rowNum) -> new GetSearchByHashtagRes(
                        rs.getInt("tagIdx"),
                        rs.getString("hashtag"),
                        rs.getString("postCount")
                ), selectHashtagListParams);
    }

}
