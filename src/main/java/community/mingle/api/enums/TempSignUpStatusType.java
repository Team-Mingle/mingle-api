package community.mingle.api.enums;

//임시회원가입 시 TempSignUpStatusType에 따라 보내는 알림 및 이메일 분기처리 위함
public enum TempSignUpStatusType {
    PROCESSING,
    APPROVED,
    REJECTED,
    ADMIN
}
