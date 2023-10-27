package community.mingle.api.global.exception;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {
    MEMBER_DUPLICATED(409, "1002", "중복된 유저입니다."),
    MEMBER_DELETED(409, "1003", "삭제된 멤버입니다"),
    EMAIL_DUPLICATED(409, "1005", "중복된 이메일입니다."),

    CODE_GENERATION_FAILED(409, "1006", "코드 생성에 실패하였습니다."),
    CODE_MATCH_FAILED(409, "1007", "코드 생성에 실패하였습니다."),
    CODE_FOUND_FAILED(409, "1008", "존재하는 코드가 없습니다."),
    CODE_VALIDITY_EXPIRED(409, "1009", "코드가 만료되었습니다."),
    EMAIL_SEND_FAILED(409, "1010", "이메일 전송에 실패하였습니다."),
    DATABASE_ERROR(500, "1100", "데이터베이스 연결에 실패하였습니다."),

    //TODO status 를 HttpStatus로 받아오기
    MEMBER_DUPLICATED(409, "1002", "중복된 유저입니다."),
    TOKEN_EXPIRED(401, "2000", "토큰이 만료되었습니다."),
    AUTHENTICATION_FAILED(401, "2001", "잘못된 인증 정보입니다."),

    FAIL_TO_REISSUE_TOKEN(401, "2002", "토큰 재발급에 실패하였습니다"),

    TOKEN_NOT_FOUND(401, "2003", "일치하는 토큰을 찾지 못하였습니다."),

    FAILED_TO_LOGIN(404, "2002", "일치하는 이메일이나 비밀번호를 찾지 못했습니다.\n" +
                             "띄어쓰기나 잘못된 글자가 있는지 확인해 주세요."),

    MEMBER_DELETED_ERROR(404, "2003","탈퇴한 사용자입니다."),
    MEMBER_REPORTED_ERROR(404, "2004", "신고된 사용자입니다."),

    MEMBER_NOT_FOUND(404, "2005", "존재하지 않는 회원 정보입니다."),

    FAILED_TO_CREATEJWT(500, "2005", "토큰 발급에 실패하였습니다." );
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