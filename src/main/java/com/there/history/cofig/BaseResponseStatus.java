package com.there.src.history.cofig;

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

    // ******** ethan ********
    // users
    USERS_EMPTY_USER_ID(false,2010, "유저 아이디 값을 확인해주세요."),
    USERS_POSTS_INVALID_ID(false,2011, "해당 유저가 아닙니다."),
    USERS_HISTORYS_INVALID_ID(false,2012, "해당 유저가 아닙니다."),
    HISTORYS_EMPTY_HISTORY_ID(false, 2030, "히스토리 아이디 값을 확인해주세요."),

    // histors
    HISTORYS_EMPTY_TITLES(false,2050,"히스토리 제목을 입력해주세요."),
    HISTORYS_INVALID_TITLES(false, 2051, "히스토리 제목의 글자 수를 확인해주세요"),
    HISTORYS_EMPTY_CONTENTS(false,2052,"히스토리 내용을 입력해주세요."),
    HISTORYS_INVALID_CONTENTS(false, 2053, "히스토리 내용의 글자 수를 확인해주세요"),
    HISTORYS_EMPTY_IMGURL(false,2054, "히스토리 사진은 하나 이상이어야 합니다."),


    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // ******** ethan ********
    DELETE_FAIL_HISTORY(false, 3030,"히스토리 삭제를 실패하였습니다."),
    MODIFY_FAIL_HISTORY(false, 3030,"히스토리 수정을 실패하였습니다."),

    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),
    DELETE_FAIL_POST(false, 4002, "게시글 삭제를 실패하였습니다. "),


    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다.");


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
