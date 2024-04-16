package community.mingle.api.domain.notification.controller.request;

import community.mingle.api.enums.BoardType;
import community.mingle.api.enums.ContentType;
import community.mingle.api.enums.CountryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import javax.annotation.Nullable;

@Data
public class SendPushNotificationRequest {
    @Nullable
    private Long contentId; //알림 보낼 content_id
    @NotBlank
    private String title; //알림 제목
    @NotBlank
    private String body; //알림 내용
    @Nullable
    private ContentType contentType; //POST, COMMENT, ITEM
    @Nullable
    private CountryType countryType;
    @Nullable
    private BoardType boardType;
}