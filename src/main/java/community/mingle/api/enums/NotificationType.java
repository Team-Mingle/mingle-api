package community.mingle.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    COMMENT("새로운 댓글이 달렸어요"), //comment
    POPULAR("인기 게시물로 지정되었어요."), //post
    REPORTED("다른 사용자들에 의해 신고되었어요"), //report
    POST("밍글에서 알림이 도착했어요"); //TODO manual 푸시알림시 알림 리스트에서 표시해줄 문구 정하기;

    private final String name;
}