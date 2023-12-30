package community.mingle.api.dto.notification;

import community.mingle.api.enums.ContentType;
import lombok.Builder;

@Builder
public record NotificationResponse(
        Long notificationId,
        Long contentId, //redirection용
        ContentType contentType, // redirection용 //TODO: contentId와 같이 value object로 만들어보기
        String content, //알림 내용
        String notificationType,
        String boardType,
        String categoryType,
        boolean isRead,
        String createAt
) {
}
