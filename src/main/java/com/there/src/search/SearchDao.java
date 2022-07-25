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
        String selectAccountListQuery = "select userIdx, name, nickName, profileImgUrl from User where status = 'ACTIVE' and nickName like concat('%', ?,'%')\n" +
                "UNION\n" +
                "select userIdx, name, nickName, profileImgUrl from User where status = 'ACTIVE' and name like concat('%', ?,'%');";
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
        String selectHashtagListQuery = "select t.tagIdx as tagIdx, t.name as name, count(pt.postIdx) as postCount\n" +
                "from Tag as t\n" +
                "    join PostTag as pt on pt.tagIdx = t.tagIdx\n" +
                "where name like concat('%', ?,'%')\n" +
                "group by tagIdx;";
        String selectHashtagListParams = hashtag;

        return this.jdbcTemplate.query(selectHashtagListQuery,
                (rs, rowNum) -> new GetSearchByHashtagRes(
                        rs.getInt("tagIdx"),
                        rs.getString("name"),
                        rs.getString("postCount")
                ), selectHashtagListParams);
    }

}
