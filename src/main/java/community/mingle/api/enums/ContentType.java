package community.mingle.api.enums;

public enum ContentType {
    // 12/16 ContentType의 용도가 Redirection 용이므로 댓글로 리다이렉션은 없기에 제거
    // 12/17 Redirection 용 뿐만아니라 Report 에서 contentId와 함께 사용되므로 다시 추가
    COMMENT,
    POST,
    ITEM
}
