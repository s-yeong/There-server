package com.there.src.user;


import com.there.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Resource
    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public GetUserRes getUsersByIdx(int userIdx){
        String getUsersByIdxQuery = "select userIdx, name, nickName, email, info, imgUrl, followingCount, followeeCount\n" +
                "from User\n" +
                "    left join(select followeeIdx, count(followeeIdx) as followingCount\n" +
                "        from Follow\n" +
                "        where status ='ACTIVE'\n" +
                "        group by followeeIdx) f on f.followeeIdx = User.userIdx\n" +
                "    left join(select followerIdx, count(followerIdx) as followeeCount\n" +
                "        from Follow\n" +
                "        where status ='ACTIVE'\n" +
                "        group by followerIdx) f1 on f1.followerIdx = User.userIdx\n" +
                "where User.userIdx =?;";
        int getUsersByIdxParams = userIdx;
        return this.jdbcTemplate.queryForObject(getUsersByIdxQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("nickName"),
                        rs.getString("email"),
                        rs.getString("info"),
                        rs.getString("imgUrl"),
                        rs.getInt("followingCount"),
                        rs.getInt("followeeCount")),
                getUsersByIdxParams);
    }

    public List<GetUserPostsRes> selectUserPosts(int userIdx){
        String selectUserPostsQuery ="SELECT p.postIdx as postIdx,\n" +
                "                            p.imgUrl as imgUrl\n" +
                "                       FROM Post as p\n" +
                "\n" +
                "                            join User as u on u.userIdx = p.userIdx\n" +
                "                        WHERE p.status = 'ACTIVE' and u.userIdx = ?\n" +
                "                        group by p.postIdx\n" +
                "                        order by p.postIdx;";
        int selectUserPostsParam = userIdx;

        return this.jdbcTemplate.query(selectUserPostsQuery,
                (rs, rowNum) -> new GetUserPostsRes(
                        rs.getInt("postIdx"),
                        rs.getString("imgUrl")
                ), selectUserPostsParam);

    }

    // 로그인 시 리프레시 토큰 저장
    public int refreshTokensave(String refreshToken, int userIdx) {
        String refreshTokensaveQuery ="update User set refreshToken =? where userIdx=?";
        Object[] refreshTokensaveparams = new Object[]{refreshToken, userIdx};
        return this.jdbcTemplate.update(refreshTokensaveQuery, refreshTokensaveparams);


    }
    public User getPassword(PostLoginReq postLoginReq) {
        String getPwdQuery = "select userIdx, nickName, email, password from User where email = ? ";
        String getPwdParams = postLoginReq.getEmail();

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rw, rowNum) -> new User(
                        rw.getInt("userIdx"),
                        rw.getString("nickName"),
                        rw.getString("email"),
                        rw.getString("password")
                ),
                getPwdParams
        );
    }

    // 회원가입
    public int createUser(PostJoinReq postJoinReq) {
        String createUserQuery = "insert into User(email, password, name ) VALUES (?, ?, ?)";
        Object[] createUserParams = new Object[]{postJoinReq.getEmail(), postJoinReq.getPassword(), postJoinReq.getName()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInsertQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertQuery, int.class);
    }

    // 로그아웃
    public int logout (int userIdx) {
        String logoutQuery = "update User set User.refreshToken=null where userIdx= ?";
        Object[] logoutParams = new Object[]{userIdx};

        return this.jdbcTemplate.update(logoutQuery, logoutParams);
    }

    // 이메일 확인
    public int checkEmail(String email){
        String checkEmailQuery = "select exists(select email from User where email = ?)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);

    }

    // 회원 확인
    public int checkUserExist(int userIdx){
        String checkUserExistQuery = "select exists(select userIdx from User where userIdx = ?)";
        int checkUserExistParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserExistQuery,
                int.class,
                checkUserExistParams);
    }

    // 리프레시 토큰 중복 여부
    public int checkRefreshExist(int userIdx){
        String checkRefreshTokenExistQuery = "select exists(select refreshToken from User where userIdx =?)";
        int checkRefreshTokenExistParmas = userIdx;
        return this.jdbcTemplate.queryForObject(checkRefreshTokenExistQuery, int.class,
                checkRefreshTokenExistParmas);
    }

    // 리프레시 토큰 조회
    public String getRefreshToken(int userIdx) {
        String selectRefreshToken = "select refreshToken from User where userIdx= ?";
        int selectRefreshTokenParams = userIdx;
        return this.jdbcTemplate.queryForObject(selectRefreshToken, String.class, selectRefreshTokenParams);
    }


    // 회원 정보 수정
    public int updateProfile(int userIdx, PatchUserReq patchUserReq){
        String updateUserNameQuery= "update User set nickName =?, name=?, info=? where userIdx =?";
        Object[] updateUserNameParams = new Object[]{patchUserReq.getNickName(), patchUserReq.getName(),
                patchUserReq.getInfo(), userIdx};
        return this.jdbcTemplate.update(updateUserNameQuery, updateUserNameParams);
    }

    // 회원 삭제
    public int updateUserStatus(int userIdx){
        String deleteUserQuery = "update User set status ='INACTIVE' where userIdx =?";
        Object[] deleteUserParams = new Object[]{userIdx};

        return this.jdbcTemplate.update(deleteUserQuery, deleteUserParams);
    }
}
