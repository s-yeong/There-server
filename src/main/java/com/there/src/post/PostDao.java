package com.there.src.post;

import com.there.src.post.model.PatchPostsReq;
import com.there.src.post.model.PostPostsReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

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

        // 해시태그 생성
        int postIdx = this.jdbcTemplate.queryForObject(lastPostsIdxQuery, int.class);
        System.out.println(postIdx);
        for(int i = 0; i < postPostsReq.getHashtag().length; i++){

            String start = "START TRANSACTION";
            String insertHashtagQuery = "insert into Tag(name) values(?)";
            String insertPostTagQuery = "insert into PostTag(postIdx, tagIdx) values(?, last_insert_id())";
            String end = "COMMIT";

            String insertHashtagParam = postPostsReq.getHashtag()[i];
            Object[] insertPostTagParams = new Object[]{postIdx};

            this.jdbcTemplate.update(start);
            this.jdbcTemplate.update(insertHashtagQuery,insertHashtagParam);
            this.jdbcTemplate.update(insertPostTagQuery, insertPostTagParams);
            this.jdbcTemplate.update(end);

        }

        return postIdx;


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


}
