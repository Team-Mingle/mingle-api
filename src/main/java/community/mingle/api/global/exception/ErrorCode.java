package community.mingle.api.global.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {

    //1 /auth
    EMAIL_DUPLICATED(409, "EMAIL_DUPLICATED", "이미 존재하는 이메일 주소입니다."), //1.3
    EMAIL_SEND_FAILED(500, "EMAIL_SEND_FAILED", "이메일 전송에 실패하였습니다."), //1.4
    CODE_MATCH_FAILED(409, "CODE_MATCH_FAILED", "인증번호가 일치하지 않습니다."), //1.5
    CODE_FOUND_FAILED(409, "1008", "존재하지 않는 인증번호입니다."), //1.5
    CODE_VALIDITY_EXPIRED(409, "CODE_VALIDITY_EXPIRED", "인증번호가 만료되었습니다."), //1.5
    POLICY_NOT_FOUND(404, "2010", "해당 정책을 찾을 수 없습니다."), //1.6

    MEMBER_ALREADY_EXIST(409, "MEMBER_ALREADY_EXIST", "이미 가입된 회원입니다."), //1.8
    NICKNAME_DUPLICATED(409, "NICKNAME_DUPLICATED", "이미 존재하는 닉네임입니다."), //1.8
    EMAIL_DOMAIN_MISMATCH(400, "EMAIL_DOMAIN_MISMATCH", "학교의 공식 이메일 도메인이 아닙니다."),

    UNIVERSITY_NOT_FOUND(404, "UNIVERSITY_NOT_FOUND", "존재하지 않는 대학입니다."), //1,8
    FAILED_TO_LOGIN(404, "FAILED_TO_LOGIN", "일치하는 이메일이나 비밀번호를 찾지 못했습니다.\n" +
                             "띄어쓰기나 잘못된 글자가 있는지 확인해 주세요."), //1.9
    FAILED_TO_CREATEJWT(500, "2006", "토큰 발급에 실패하였습니다." ), //1.9

    MEMBER_DELETED_ERROR(404, "MEMBER_DELETED_ERROR","탈퇴한 사용자입니다."), //1.9
    MEMBER_UNAUTHENTICATED_ERROR(404, "MEMBER_UNAUTHENTICATED_ERROR","사용자 인증이 완료되지 않았습니다.\n 인증이 완료되면 이메일과 앱 푸시를 통해 안내드리겠습니다."), //1.9
    MEMBER_REPORTED_ERROR(404, "MEMBER_REPORTED_ERROR", "신고로 인해 계정 사용이 중지되었습니다."), //1.9
    TOKEN_EXPIRED( 401, "TOKEN_EXPIRED", "토큰이 만료되었습니다."), //1.12
    TOKEN_NOT_FOUND(401, "TOKEN_NOT_FOUND", "일치하는 토큰을 찾지 못하였습니다."), //1.12
    AUTHENTICATION_FAILED(401, "2001", "잘못된 인증 정보입니다."), //1.12
    FAIL_TO_REISSUE_TOKEN(401, "2002", "토큰 재발급에 실패하였습니다"), //1.12
    MEMBER_NOT_FOUND(404, "2005", "존재하지 않는 회원 정보입니다."),
    COURSE_TIME_CONFLICT(409, "COURSE_TIME_CONFLICT", "강의 시간이 겹치지 않게 설정해 주세요."),
    TIMETABLE_CONFLICT(409, "TIMETABLE_CONFLICT", "시간표가 겹칩니다."),
    COURSE_NOT_FOUND(404, "COURSE_NOT_FOUND", "존재하지 않는 강의입니다."),
    COURSE_FORBIDDEN(409, "COURSE_FORBIDDEN", "접근할 수 없는 강의입니다."),
    SEMESTER_NOT_FOUND(404, "SEMESTER_NOT_FOUND", "존재하지 않는 학기입니다."),
    TIMETABLE_NOT_FOUND(404, "TIMETABLE_NOT_FOUND", "존재하지 않는 시간표입니다."),
    COUPON_TYPE_NOT_FOUND(404, "COUPON_TYPE_NOT_FOUND", "존재하지 않는 쿠폰 타입입니다."),
    COUPON_NOT_FOUND(404, "COUPON_NOT_FOUND", "보유 중인 쿠폰이 없습니다."),
    POINT_NOT_ENOUGH(406, "POINT_NOT_ENOUGH", "포인트가 부족합니다."),
    COURSE_ALREADY_EVALUATED(409, "COURSE_ALREADY_EVALUATED", "이미 해당 강의에 평가를 작성했습니다."),
    FRIEND_CODE_EXPIRED(409, "FRIEND_CODE_EXPIRED", "친구 코드가 만료되었습니다."),
    FRIEND_CODE_NOT_FOUND(404, "FRIEND_CODE_NOT_FOUND", "존재하지 않는 친구 코드입니다."),
    FRIEND_ALREADY_ADDED(409, "FRIEND_ALREADY_ADDED", "이미 친구로 등록된 사용자입니다."),
    MEMBER_NOT_FRIEND(403, "MEMBER_NOT_FRIEND", "해당 유저의 친구로 등록된 유저가 아닙니다."),
    COURSE_ALREADY_ADDED(409, "COURSE_ALREADY_ADDED", "이미 추가된 강의입니다."),
    TIMETABLE_ALREADY_PINNED(409, "TIMETABLE_ALREADY_PINNED", "이미 고정된 시간표입니다."),
    FRIEND_NOT_FOUND(404, "FRIEND_NOT_FOUND","친구를 찾을 수 없습니다"),

    // 2. TODO post, comment 정리
    MODIFY_NOT_AUTHORIZED(403, "MODIFY_NOT_AUTHORIZED", "수정 권한이 없습니다."),
    COMMENT_NOT_FOUND(404, "3000", "존재하지 않는 멘션 댓글입니다."),
    LIKE_ALREADY_EXIST(409, "2010", "이미 좋아요를 눌렀습니다."),
    FAIL_TO_CREATE_COMMENT(400, "2011", "댓글 생성에 실패했습니다."),
    LIKE_NOT_FOUND(404, "LIKE_NOT_FOUND", "좋아요를 찾을 수 없습니다."),
    SCRAP_NOT_FOUND(404, "SCRAP_NOT_FOUND", "스크랩을 찾을 수 없습니다."),
    POST_LIKE_ALREADY_EXIST(409, "2010", "이미 좋아요를 눌렀습니다."),
    POST_LIKE_NOT_FOUND(404, "2011", "좋아요를 찾을 수 없습니다."),
    POST_NOT_EXIST(400,"2200", "게시물이 존재하지 않습니다."),
    POST_DELETED_REPORTED(400,"2201", "삭제되거나 신고된 게시물입니다." ),


    //image
    UPLOAD_FAIL_IMAGE(500, "UPLOAD_FAIL_IMAGE", "이미지 업로드에 실패하였습니다."),
    INVALID_IMAGE_FORMAT(400, "INVALID_IMAGE_FORMAT", "지원하지 않는 파일 형식입니다."),
    DELETE_FAIL_IMAGE(500,"DELETE_FAIL_IMAGE" , "이미지 삭제에 실패하였습니다,"),

    //notification
    NOTIFICATION_NOT_FOUND(404, "NOTIFICATION_NOT_FOUND", "존재하지 않는 알림입니다."),
    NOTIFICATION_BAD_REQUEST(400, "NOTIFICATION_BAD_REQUEST", "알림 요청이 잘못되었습니다"),


    //Report
    ALREADY_REPORTED(409, "ALREADY_REPORTED", "이미 신고한 컨텐츠입니다."),
    //mypage
    EMPTY_MYPOST_LIST(404, "EMPTY_MYPOST_LIST", "게시글이 없어요."),

    //Internal server error
    INTERNAL_SERVER_ERROR(500, "500", "INTERNAL SERVER ERROR"),

    //fcm error
    FIREBASE_MESSAGING_ERROR(500, "FIREBASE_MESSAGING_ERROR", "FCM 메시지 전송에 실패하였습니다."),

    //Item
    INVALID_STATUS_REQUEST(400, "INVALID_ITEM_STATUS_REQUEST", "유효하지 않은 상태 요청 값 입니다."),
    ITEM_COMMENT_NOT_FOUND(404, "ITEM_COMMENT_NOT_FOUND", "장터 게시물 댓글을 찾을 수 없습니다."),
    COUNTRY_NOT_FOUND(404, "COUNTRY_NOT_FOUND", "유효하지 않은 국가입니다."),

    FRESHMAN_COUPON_ALREADY_GIVEN(409, "FRESHMAN_COUPON_ALREADY_GIVEN", "이미 새내기 이용권 발급이 신청되었거나 발급되었습니다.");

    @Getter
    private final int status;
    private final String code;
    private final String message;

    ErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

}