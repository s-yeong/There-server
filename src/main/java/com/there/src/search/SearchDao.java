package com.there.src.search;

import com.there.src.search.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SearchDao {

    private JdbcTemplate jdbcTemplate;

    @Resource
    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 인기 검색어 조회
    public List<GetPopularSearchListRes> selectPopularSearches(){
        String selectPopularSearchesQuery = "select s.searchIdx as searchIdx, content\n" +
                "from Search as s\n" +
                "    left join(select count(searchIdx) as searchCount, searchIdx\n" +
                "                from UserSearch\n" +
                "                group by searchIdx) as us on us.searchIdx = s.searchIdx\n" +
                "order by searchCount desc LIMIT 4;";
        return this.jdbcTemplate.query(selectPopularSearchesQuery,
                (rs, rowNum) -> new GetPopularSearchListRes(
                        rs.getInt("searchIdx"),
                        rs.getString("content")
                ));
    }

    // 최근 검색어 조회
    public List<GetRecentSearchListRes> selectRecentSearches(int userIdx){
        String selectRecentSearchesQuery = "select s.searchIdx as searchIdx, content\n" +
                "from Search as s\n" +
                "    join UserSearch as us on us.searchIdx = s.searchIdx\n" +
                "where us.userIdx = ?\n" +
                "order by us.updated_At;";
        int selectRecentSearchesParam = userIdx;
        return this.jdbcTemplate.query(selectRecentSearchesQuery,
                (rs, rowNum) -> new GetRecentSearchListRes(
                        rs.getInt("searchIdx"),
                        rs.getString("content")
                ), selectRecentSearchesParam);
    }


    // 최근 검색어 삭제
    public int deleteRecentSearch(int userIdx, int searchIdx){

        String deleteRecentSearchQuery ="delete from UserSearch where userIdx =? and searchIdx=?;";
        Object[] deleteRecentSearchParam = new Object[] {userIdx, searchIdx};

        return this.jdbcTemplate.update(deleteRecentSearchQuery, deleteRecentSearchParam);
    }

    // 최근 검색어 모두 삭제
    public int deleteAllRecentSearch(int userIdx){

        String deleteAllRecentSearchQuery ="delete from UserSearch where userIdx = ?;";
        int deleteAllRecentSearchParam = userIdx;
        return this.jdbcTemplate.update(deleteAllRecentSearchQuery, deleteAllRecentSearchParam);
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
    public int checkSearchExist(int userIdx, String keyword){
        String checkSearchExistQuery = "select exists(\n" +
                "select *\n" +
                "from Search as s\n" +
                "    join UserSearch as us on us.searchIdx = s.searchIdx\n" +
                "where content = ? and userIdx = ?);";
        Object[] checkSearchExistParams = new Object[] {keyword, userIdx};

        return this.jdbcTemplate.queryForObject(checkSearchExistQuery,
                int.class,
                checkSearchExistParams);
    }

    // 중복 검색어 체크
    public int checkDuplicateKeyword(String keyword){
        String checkDuplicateSearchQuery = "select exists(select * from Search where content = ?);";
        String checkDuplicateSearchParam = keyword;

        return this.jdbcTemplate.queryForObject(checkDuplicateSearchQuery, int.class, checkDuplicateSearchParam);
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

    // 유저-검색 관계만 기록 (중복 검색어인 경우)
    public void insertUserSearch(int userIdx, String keyword){

        String selectsearchIdxQuery = "select searchIdx from Search where content = ?;";
        String selectsearchIdxParam = keyword;
        int searchIdx = this.jdbcTemplate.queryForObject(selectsearchIdxQuery, int.class, selectsearchIdxParam);

        String insertUserSearchQuery = "insert into UserSearch(userIdx, searchIdx) value(?, ?);";
        Object[] insertUserSearchParams = new Object[] {userIdx, searchIdx};
        this.jdbcTemplate.update(insertUserSearchQuery, insertUserSearchParams);
    }

    // 검색 업데이트
    public void updateSearch(String keyword){
        String selectsearchIdxQuery = "select searchIdx from Search where content = ?;";
        String selectsearchIdxParam = keyword;
        int searchIdx = this.jdbcTemplate.queryForObject(selectsearchIdxQuery, int.class, selectsearchIdxParam);

        String updateSearchQuery = "update UserSearch SET updated_At = NOW() where searchIdx = ?;";
        int updateSearchParam = searchIdx;
        this.jdbcTemplate.update(updateSearchQuery, updateSearchParam);
    }

    // 해당 유저 검색 기록인지 체크
    public int checkUserSearchExist(int userIdx, int searchIdx){
        String checkUserSearchExistQuery = "select exists(select * from UserSearch where userIdx = ? and searchIdx = ?);";
        Object[] checkUserSearchExistParams = new Object[] {userIdx, searchIdx};

        return this.jdbcTemplate.queryForObject(checkUserSearchExistQuery,
                int.class,
                checkUserSearchExistParams);

    }

    // 유저 체크
    public int checkUserExist(int userIdx){
        String checkUserExistQuery = "select exists(select userIdx\n" +
                "from User\n" +
                "where userIdx = ?);" ;
        int checkUserExistParam = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserExistQuery,
                int.class,
                checkUserExistParam);
    }

}
