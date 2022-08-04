package com.there.src.post;

import com.there.src.post.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class PostDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 게시물 생성
    public int createPosts(int userIdx, PostPostsReq postPostsReq)  {

        String createPostsQuery = "insert into Post (userIdx, content) values (?, ?);";
        Object[] createPostsParams = new Object[]{userIdx, postPostsReq.getContent()};

        this.jdbcTemplate.update(createPostsQuery, createPostsParams);

        String lastPostsIdxQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastPostsIdxQuery, int.class);

    }

    // db Tag 테이블 name 체크
    public int checkTagName(String hashtag){
        String checkTagNameExistQuery="select exists(select tagIdx from Tag where name=?)";
        String checkTagNameQuery="select tagIdx from Tag where name=?";
        if(this.jdbcTemplate.queryForObject(checkTagNameExistQuery, int.class,hashtag)==1){
            return this.jdbcTemplate.queryForObject(checkTagNameQuery,int.class,hashtag);
        }
        else{
            return 0;
        }
    }

    // tagIdx = 0 => name이 db에 없는 경우
    public void insertTag(int postIdx, String hashtag){

        String start = "START TRANSACTION";
        String insertHashtagQuery = "insert into Tag(name) values(?)";
        String insertPostTagQuery = "insert into PostTag(postIdx, tagIdx) values(?, last_insert_id())";
        String end = "COMMIT";

        this.jdbcTemplate.update(start);
        this.jdbcTemplate.update(insertHashtagQuery,hashtag);
        this.jdbcTemplate.update(insertPostTagQuery, postIdx);
        this.jdbcTemplate.update(end);
    }

    // tagIdx = 1 => name이 db에 있는 경우
    public void  insertIsTag(int postIdx, int tagIdx){

        String insertPostTagQuery = "insert into PostTag(postIdx, tagIdx) values(?, ?)";
        Object[] insertPostTagParams = new Object[]{postIdx, tagIdx};

        this.jdbcTemplate.update(insertPostTagQuery,insertPostTagParams);
    }



    /**
     * 게시물 수정
     * + 콘텐츠 수정
     * + 해시태그 수정
     */

    public int updatePosts(int postIdx, PatchPostsReq patchPostsReq) {

        String updatePostQuery = "update Post set content = ? where postIdx = ?";
        Object[] updatePostParams = new Object[]{patchPostsReq.getContent(), postIdx};

        return this.jdbcTemplate.update(updatePostQuery, updatePostParams);
    }

    // 해시태그 삭제
    public void deleteTag(int postIdx) {

        String start = "START TRANSACTION";
        String deletePostTagQuery1 = "select tagIdx from PostTag where postIdx = ? LIMIT 1;";
        String deletePostTagQuery2 = "delete from PostTag where postIdx = ? LIMIT 1;";
        String existPostTagQuery = "select exists(select postIdx from PostTag where tagIdx = ?);";
        String deleteTagQuery = "delete from Tag where tagIdx = ?;";
        String end = "COMMIT";

        this.jdbcTemplate.update(start);
        int tagIdx = this.jdbcTemplate.queryForObject(deletePostTagQuery1,int.class,postIdx);
        this.jdbcTemplate.update(deletePostTagQuery2, postIdx);
        int tagExist = this.jdbcTemplate.queryForObject(existPostTagQuery,int.class,tagIdx);
        if(tagExist == 0){
            this.jdbcTemplate.update(deleteTagQuery, tagIdx);
        }
        this.jdbcTemplate.update(end);

    }

    // 게시물 해시태그 체크
    public int checkPostTag(int postIdx){
        String checkPostTagQuery = "select  exists(select * from PostTag where postIdx = ?);";
        int checkPostTagParam = postIdx;
        return this.jdbcTemplate.queryForObject(checkPostTagQuery,int.class,checkPostTagParam);
    }

    // 게시물 삭제
    public int deletePosts(int postIdx) {

        String deletePostQuery = "UPDATE Post SET status = 'INACTIVE' WHERE postIdx = ?";
        int deletePostParams = postIdx;

        return this.jdbcTemplate.update(deletePostQuery, postIdx);
    }

    // 랜덤 게시물 리스트 조회
    public List<GetPostListRes> selectRandomPostList() {
        String selectRandomPostListQuery = "select p.imgUrl,  GROUP_CONCAT(t.name,' ') as name\n" +
                "from Post p\n" +
                "join PostTag as pt on pt.postIdx = p.postIdx\n" +
                "join Tag as t on t.tagIdx = pt.tagIdx\n" +
                "where status ='ACTIVE'\n" +
                "group by p.postIdx\n" +
                "order by rand() limit 100;";
        return this.jdbcTemplate.query(selectRandomPostListQuery,
                (rs, rowNum) -> new GetPostListRes(
                        rs.getString("imgUrl"),
                        rs.getString("name")
                ));
    }

    // 인기글 리스트 조회
    public List<GetPostListRes> selectRankingPostList() {
        String selectRankingPostListQuery = "select p.imgUrl, GROUP_CONCAT(t.name,' ') as name\n" +
                "from Post p\n" +
                "left join (select postIdx, count(postIdx) as likeCount\n" +
                "        from postLike\n" +
                "        group by postIdx) u on u.postIdx = p.postIdx\n" +
                "        join PostTag as pt on pt.postIdx = p.postIdx\n" +
                "        join Tag as t\n" +
                "        where status = 'ACTIVE' and t.tagIdx = pt.tagIdx\n" +
                "        group by p.postIdx\n" +
                "        order by likeCount desc;";
        return this.jdbcTemplate.query(selectRankingPostListQuery,
                (rs, rowNum) -> new GetPostListRes(
                        rs.getString("imgUrl"),
                        rs.getString("name")
                ));
    }



    // 내가 팔로우한 구독자의 게시글 해시태그 리스트 조회
    public List<GetPostListRes> selectFollowerPostList(int userIdx) {
        String selectFollowerPostListQuery = "select follower.imgUrl, GROUP_CONCAT(t.name,' ') as name\n" +
                "    from Post follower\n" +
                "        left join Follow as followee on followee.followerIdx = follower.userIdx\n" +
                "        join Follow as f on f.followIdx = followee.followIdx\n" +
                "        join PostTag as pt on pt.postIdx = follower.postIdx\n" +
                "        join Tag as t on t.tagIdx = pt.tagIdx\n" +
                "    where followee.followeeIdx = ?\n" +
                "GROUP BY follower.postIdx;";
        int selectFollowerPostListQueryParam = userIdx;
        return this.jdbcTemplate.query(selectFollowerPostListQuery,
                (rs, rowNum) -> new GetPostListRes(
                        rs.getString("imgUrl"),
                        rs.getString("name")
                ), selectFollowerPostListQueryParam);

    }

}
