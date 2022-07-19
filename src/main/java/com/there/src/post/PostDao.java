package com.there.src.post;

import com.there.src.post.model.GetPostListRes;
import com.there.src.post.model.PatchPostsReq;
import com.there.src.post.model.PostPostsReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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

        String createPostsQuery = "insert into Post (userIdx, imgUrl, content) values (?, ? ,?);";
        Object[] createPostsParams = new Object[]{userIdx, postPostsReq.getImgUrl(), postPostsReq.getContent()};

        this.jdbcTemplate.update(createPostsQuery, createPostsParams);

        String lastPostsIdxQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastPostsIdxQuery, int.class);
    }

    /**
     * 게시물 수정
     * 1. 이미지, 콘텐츠 수정
     * 2. 이미지 수정
     * 3. 콘텐츠 수정
     *
     */

    public int updatePosts(int postIdx, PatchPostsReq patchPostsReq) {

        String updatePostQuery = "update Post set imgUrl = ?, content = ? where postIdx = ?";
        Object[] updatePostParams = new Object[]{patchPostsReq.getImgUrl(), patchPostsReq.getContent(), postIdx};

        return this.jdbcTemplate.update(updatePostQuery, updatePostParams);

    }

    public int updatepostsImgUrl(int postIdx, PatchPostsReq patchPostsReq) {

        String updatePostQuery = "update Post set imgUrl = ?, content = ? where postIdx = ?";
        Object[] updatePostParams = new Object[]{patchPostsReq.getImgUrl(), patchPostsReq.getContent(), postIdx};

        return this.jdbcTemplate.update(updatePostQuery, updatePostParams);

    }

    public int updatepostsContent(int postIdx, PatchPostsReq patchPostsReq) {

        String updatePostQuery = "update Post set imgUrl = ?, content = ? where postIdx = ?";
        Object[] updatePostParams = new Object[]{patchPostsReq.getImgUrl(), patchPostsReq.getContent(), postIdx};

        return this.jdbcTemplate.update(updatePostQuery, updatePostParams);

    }

    // 게시물 삭제
    public int deletePosts(int postIdx) {

        String deletePostQuery = "UPDATE Post SET status = 'INACTIVE' WHERE postIdx = ?";
        int deletePostParams = postIdx;

        return this.jdbcTemplate.update(deletePostQuery, postIdx);
    }

    // 랜덤 게시물 리스트 조회
    public List<GetPostListRes> selectRandomPostList() {
        String selectRandomPostListQuery = "select imgUrl, content, created_At, likeCount\n" +
                "from Post\n" +
                "left join(select postIdx, count(postIdx) as likeCount\n" +
                "    from postLike\n" +
                "    group by postIdx) u on u.postIdx = Post.postIdx\n" +
                "where status ='ACTIVE'\n" +
                "order by rand() limit 100;";
        return this.jdbcTemplate.query(selectRandomPostListQuery,
                (rs, rowNum) -> new GetPostListRes(
                        rs.getString("imgUrl"),
                        rs.getString("content"),
                        rs.getString("created_At"),
                        rs.getInt("likeCount")
                ));
    }

    // 인기글 리스트 조회
    public List<GetPostListRes> selectRankingPostList() {
        String selectRankingPostListQuery = "select imgUrl, content, created_At, likeCount\n" +
                "from Post\n" +
                "left join(select postIdx, count(postIdx) as likeCount\n" +
                "    from postLike\n" +
                "    group by postIdx) u on u.postIdx = Post.postIdx\n" +
                "where status ='ACTIVE'\n" +
                "order by likeCount desc;";
        return this.jdbcTemplate.query(selectRankingPostListQuery,
                (rk, rowNum) -> new GetPostListRes(
                        rk.getString("imgUrl"),
                        rk.getString("content"),
                        rk.getString("created_At"),
                        rk.getInt("likeCount")
                ));
    }
}
