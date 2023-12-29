package community.mingle.api.domain.notification.controller.request;

import community.mingle.api.enums.ContentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendPushNotificationRequest {
    private Long contentId; //알림 보낼 content_id
    @NotBlank
    private String title; //알림 제목
    @NotBlank
    private String body; //알림 내용
    @NotNull
    private ContentType contentType; //POST, COMMENT, ITEM
}