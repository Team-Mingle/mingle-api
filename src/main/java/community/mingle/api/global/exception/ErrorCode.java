package community.mingle.api.global.exception;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {

    //TODO status 를 HttpStatus로 받아오기
    MEMBER_DUPLICATED(409, "1002", "중복된 유저입니다."),
    TOKEN_EXPIRED(401, "2000", "토큰이 만료되었습니다."),
    AUTHENTICATION_FAILED(401, "2001", "잘못된 인증 정보입니다."),

    POST_NOT_EXIST(400,"2200", "게시물이 존재하지 않습니다." ),
    POST_DELETED_REPORTED(400,"2201", "삭제되거나 신고된 게시물입니다." )
    ;

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
    public int getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}