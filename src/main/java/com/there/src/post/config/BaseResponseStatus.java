package com.there.src.post.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),

    // Posts
    EMPTY_IMGURL(false, 2010, "게시글 사진을 올려주세요."),
    EMPTY_CONTENT(false, 2011, "게시글 내용을 입력해주세요."),
    EXCEEDED_IMGURL(false,2012, "게시글 사진을 하나만 올려주세요"),
    EXCEEDED_HASHTAG(false,2013, "해시태그는 5개까지 가능합니다."),
    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),


    /**
     * 4000 : Database, Server 오류
     */
    // Common
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    // Posts
    CREATE_FAIL_POST(false, 4010, "게시글 생성을 실패하였습니다. "),
    UPDATE_FAIL_POST(false, 4011, "게시글 수정을 실패하였습니다. "),
    DELETE_FAIL_POST(false, 4012, "게시글 삭제를 실패하였습니다. "),


    PASSWORD_ENCRYPTION_ERROR(false, 4020, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4021, "비밀번호 복호화에 실패하였습니다.");


    // 5000 : 필요시 만들어서 쓰세요
    // 6000 : 필요시 만들어서 쓰세요


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
