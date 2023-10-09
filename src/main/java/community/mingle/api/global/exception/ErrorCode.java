package community.mingle.api.global.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;

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
    DATABASE_ERROR(500, "1100", "데이터베이스 연결에 실패하였습니다.");



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