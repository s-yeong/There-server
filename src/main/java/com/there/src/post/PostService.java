package com.there.src.post;

import com.there.config.BaseException;
import com.there.src.post.model.PatchPostsReq;
import com.there.src.post.model.PostPostsReq;
import com.there.src.post.model.PostPostsRes;
import com.there.src.s3.S3Service;
import com.there.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.there.config.BaseResponseStatus.*;

@Service
public class PostService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PostDao postDao;
    private final JwtService jwtService;
    private final S3Service s3Service;

    @Autowired
    public PostService(PostDao postDao, JwtService jwtService, S3Service s3Service) {
        this.postDao = postDao;
        this.jwtService = jwtService;
        this.s3Service = s3Service;
    }

    /**
     * 게시글 생성
     */
    @Transactional(rollbackFor = Exception.class)
    public PostPostsRes createPosts(int userIdx, PostPostsReq postPostsReq, List<MultipartFile> MultipartFiles) throws BaseException {

        if(MultipartFiles.size() > 1){
            throw new BaseException(EXCEEDED_IMGURL);
        }

        try {
            int postIdx = postDao.createPosts(userIdx, postPostsReq);

            // 해시태그 생성
            if(postPostsReq.getHashtag()!=null) {
                for (String hashtag : postPostsReq.getHashtag()) {
                    int tagIdx = postDao.checkTagName(hashtag);

                    if (tagIdx == 0) {
                        postDao.insertTag(postIdx, hashtag);
                    } else {
                        postDao.insertIsTag(postIdx, tagIdx);
                    }
                }
            }

            // 게시물 사진 업로드

                // s3 업로드
                String s3path = "Post/postIdx : " + Integer.toString(postIdx);
                String imgPath = s3Service.uploadFiles(MultipartFiles.get(0), s3path);

                // db 업로드
                s3Service.uploadPostImg(imgPath, postIdx);

            return new PostPostsRes(postIdx);

        } catch (Exception exception) {
            throw new BaseException(CREATE_FAIL_POST);
        }
    }

    /**
     * 게시글 수정
     *  ImgUrl, Content 수정
     *  ImgUrl 수정
     *  Content 수정
     *  + 해시태그 수정
     */
    @Transactional(rollbackFor = Exception.class)
    public void updatePosts(int postIdx, PatchPostsReq patchPostsReq, List<MultipartFile> MultipartFiles) throws BaseException {

        int result = 0;
        if(MultipartFiles != null && MultipartFiles.size() > 1){
            throw new BaseException(EXCEEDED_IMGURL);
        }

        try {
            if (MultipartFiles != null && patchPostsReq.getContent() != null){

                s3Service.removeFolder("Post/postIdx : " + Integer.toString(postIdx));

                // s3 업로드
                String s3path = "Post/postIdx : " + Integer.toString(postIdx);
                String imgPath = s3Service.uploadFiles(MultipartFiles.get(0), s3path);

                // db 업로드
                s3Service.uploadPostImg(imgPath, postIdx);

                result = postDao.updatePosts(postIdx, patchPostsReq);

            }
            else if (MultipartFiles != null) {

                result = 1;
                s3Service.removeFolder("Post/postIdx : " + Integer.toString(postIdx));

                // s3 업로드
                String s3path = "Post/postIdx : " + Integer.toString(postIdx);
                String imgPath = s3Service.uploadFiles(MultipartFiles.get(0), s3path);

                // db 업로드
                s3Service.uploadPostImg(imgPath, postIdx);
            }
            else if (patchPostsReq.getContent() != null) {
                result = postDao.updatePosts(postIdx, patchPostsReq);
            }
            else result = 1;

            // 해시태그 수정
            if(patchPostsReq.getHashtag()!=null) {

                // 해시태그 삭제
                while(postDao.checkPostTag(postIdx) == 1){
                    // 게시물 해시태그 체크
                    postDao.deleteTag(postIdx);
                }

                // 해시태그 생성
                for (String hashtag : patchPostsReq.getHashtag()) {

                    int tagIdx = postDao.checkTagName(hashtag);

                    if (tagIdx == 0) {
                        postDao.insertTag(postIdx, hashtag);
                    } else {
                        postDao.insertIsTag(postIdx, tagIdx);
                    }
                }
            }

            if (result == 0) throw new BaseException(UPDATE_FAIL_POST); // 삭제 확인 (0 : 실패 / 1 : 성공)
        }
        catch (Exception exception) {
            System.out.println(exception);
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 게시글 삭제
     *  ACTIVE -> INACTIVE로 변경 (즉, 상태만 변경. 게시글 기록은 남김)
     */
    public void deletePosts(int postIdx) throws BaseException {

        try {
            int result = postDao.deletePosts(postIdx);
            if (result == 0) throw new BaseException(DELETE_FAIL_POST); // 삭제 확인 (0 : 실패 / 1 : 성공)
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }
}
