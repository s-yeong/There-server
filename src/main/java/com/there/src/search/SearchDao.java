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

    /**
     * 최근 검색 기록
     * 1. 검색 기록
     * 2. 검색 기록 searchIdx 가져오기
     * 3. 해시태그인 경우
     * 4. 계정인 경우
     */
    // 최근 검색 기록
    public List<GetRecentSearchRes> selectRecentSearches(int userIdx){
        String selectRecentSearchesQuery = "select s.searchIdx, s.tagOrUserIdx, s.tagOrAccount, s.created_At as createdAt\n" +
                "from Search as s\n" +
                "    join UserSearch as us on us.searchIdx = s.searchIdx\n" +
                "where us.userIdx=? and s.tagOrAccount = 'Tag'\n" +
                "UNION\n" +
                "select s.searchIdx, s.tagOrUserIdx, s.tagOrAccount, s.created_At as createdAt\n" +
                "from Search as s\n" +
                "    join UserSearch as us on us.searchIdx = s.searchIdx\n" +
                "where us.userIdx=? and s.tagOrAccount = 'Account'\n" +
                "order by createdAt desc;";
        Object[] selectRecentSearchesParam = new Object[]{userIdx, userIdx};
        return this.jdbcTemplate.query(selectRecentSearchesQuery,
                (rs, rowNum) -> new GetRecentSearchRes(
                        rs.getInt("searchIdx"),
                        rs.getInt("tagOrUserIdx"),
                        rs.getString("tagOrAccount"),
                        rs.getString("createdAt")
                ), selectRecentSearchesParam);
    }

    // 최근 검색 기록 - searchIdx값만 가져오기
    public List<GetRecentSearchListRes> selectRecentSearchIdx(int userIdx){
        String selectRecentSearchIdxQuery = "select s.searchIdx, s.created_At\n" +
                "from Search as s\n" +
                "    join UserSearch as us on us.searchIdx = s.searchIdx\n" +
                "where us.userIdx=? and s.tagOrAccount = 'Tag'\n" +
                "UNION\n" +
                "select s.searchIdx, s.created_At\n" +
                "from Search as s\n" +
                "    join UserSearch as us on us.searchIdx = s.searchIdx\n" +
                "where us.userIdx=? and s.tagOrAccount = 'Account'\n" +
                "order by created_At desc;";
        Object[] selectRecentSearchIdxParam = new Object[]{userIdx, userIdx};
        return this.jdbcTemplate.query(selectRecentSearchIdxQuery,
                (rs,rowNum) -> new GetRecentSearchListRes(
                        rs.getInt("searchIdx"),
                        null,
                        null
                ), selectRecentSearchIdxParam);

    }

    
    // 최근 검색 기록 - 해시태그인 경우
    public GetSearchByHashtagRes selectHashtag(int tagIdx){
        String selectHashtagQuery = "select t.tagIdx as tagIdx, t.name as name, count(pt.postIdx) as postCount\n" +
                "from Tag as t\n" +
                "    join PostTag as pt on pt.tagIdx = t.tagIdx\n" +
                "where t.tagIdx = ?\n" +
                "group by tagIdx;";
        int selectHashtagParam = tagIdx;
        return this.jdbcTemplate.queryForObject(selectHashtagQuery,
                (rs, rowNum) -> new GetSearchByHashtagRes(
                        rs.getInt("tagIdx"),
                        rs.getString("name"),
                        rs.getString("postCount")
                ), selectHashtagParam);
    }

    // 최근 검색 기록 - 계정인 경우
    public GetSearchByAccountRes selectAccount(int userIdx){
        String selectAccountQuery = "select userIdx, name, nickName, profileImgUrl from User where status = 'ACTIVE' and userIdx = ?;";
        int selectAccountParam = userIdx;
        return this.jdbcTemplate.queryForObject(selectAccountQuery,
                (rs, rowNum) -> new GetSearchByAccountRes(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("nickName"),
                        rs.getString("profileImgUrl")
                ), selectAccountParam);
    }

    // 최근 검색 삭제
    public int deleteRecentSearch(int searchIdx){

        String deleteRecentSearchQuery ="delete us, s\n" +
                "from UserSearch as us\n" +
                "    join Search as s on s.searchIdx = us.searchIdx\n" +
                "where us.searchIdx=?;";
        int deleteRecentSearchParam = searchIdx;
        return this.jdbcTemplate.update(deleteRecentSearchQuery, deleteRecentSearchParam);
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
    public List<GetSearchPostsByHashtagRes> selectPopularPosts(int tagIdx){
        String selectPopularPostsQuery = "select p.postIdx, p.imgUrl\n" +
                "from Post as p\n" +
                "    left join(select postIdx, count(postIdx) as likeCount\n" +
                "from postLike\n" +
                "group by postIdx) as pl on pl.postIdx = p.postIdx\n" +
                "    join PostTag as pt on pt.postIdx = p.postIdx\n" +
                "where p.status = 'ACTIVE' and pt.tagIdx = ?\n" +
                "order by likeCount desc;";
        int selectPopularPostsParam = tagIdx;

        return this.jdbcTemplate.query(selectPopularPostsQuery,
                (rs, rowNum) -> new GetSearchPostsByHashtagRes(
                        rs.getInt("postIdx"),
                        rs.getString("imgUrl")
                ), selectPopularPostsParam);
    }

    // 해시태그 최근 게시물 검색 (최근 게시물 생성 기준)
    public List<GetSearchPostsByHashtagRes> selectRecentPosts(int tagIdx){
        String selectRecentPostsQuery = "select p.postIdx, p.imgUrl\n" +
                "from Post as p\n" +
                "    join PostTag as pt on pt.postIdx = p.postIdx\n" +
                "where p.status = 'ACTIVE' and pt.tagIdx = ?\n" +
                "order by created_At desc;";
        int selectRecentPostsParam = tagIdx;

        return this.jdbcTemplate.query(selectRecentPostsQuery,
                (rs, rowNum) -> new GetSearchPostsByHashtagRes(
                        rs.getInt("postIdx"),
                        rs.getString("imgUrl")
                ), selectRecentPostsParam);
    }

    // 검색 기록 체크
    public int checkSearchExist(String keyword){
        String checkSearchExistQuery = "select exists(select content from Search where content = ?);";
        String checkSearchExistParam = keyword;

        return this.jdbcTemplate.queryForObject(checkSearchExistQuery,
                int.class,
                checkSearchExistParam);
    }

    // 검색 기록
    public void insertSearch(int userIdx, String keyword){
        String start = "START TRANSACTION;";
        String insertSearchQuery = "insert into Search(content) values(?);";
        String insertUserSearchQuery = "insert into UserSearch(userIdx, searchIdx) values(?,last_insert_id());";
        String end = "COMMIT;";
        String insertSearchParam = keyword;
        int insertUserSearchParam = userIdx;

        this.jdbcTemplate.update(start);
        this.jdbcTemplate.update(insertSearchQuery, insertSearchParam);
        this.jdbcTemplate.update(insertUserSearchQuery, insertUserSearchParam);
        this.jdbcTemplate.update(end);

    }

}
