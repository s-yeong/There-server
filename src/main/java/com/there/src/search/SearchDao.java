package com.there.src.search;

import com.there.src.post.model.GetPostListRes;
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
        String selectHashtagListParam = hashtag;

        return this.jdbcTemplate.query(selectHashtagListQuery,
                (rs, rowNum) -> new GetSearchByHashtagRes(
                        rs.getInt("tagIdx"),
                        rs.getString("name"),
                        rs.getString("postCount")
                ), selectHashtagListParam);
    }

    // 해시태그 인기 게시물 검색 (좋아요 순)
    public List<GetPopularSearchRes> selectPopularPost(int tagIdx){
        String selectPopularPostQuery = "select p.postIdx, p.imgUrl\n" +
                "from Post as p\n" +
                "    left join(select postIdx, count(postIdx) as likeCount\n" +
                "from postLike\n" +
                "group by postIdx) as pl on pl.postIdx = p.postIdx\n" +
                "    join PostTag as pt on pt.postIdx = p.postIdx\n" +
                "where p.status = 'ACTIVE' and pt.tagIdx = ?\n" +
                "order by likeCount desc;";
        int selectPopularPostParam = tagIdx;

        return this.jdbcTemplate.query(selectPopularPostQuery,
                (rs, rowNum) -> new GetPopularSearchRes(
                        rs.getInt("postIdx"),
                        rs.getString("imgUrl")
                ), selectPopularPostParam);
    }

}
