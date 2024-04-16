package community.mingle.api.domain.notification.controller;

import community.mingle.api.domain.notification.controller.request.SendPushNotificationRequest;
import community.mingle.api.domain.notification.facade.NotificationFacade;
import community.mingle.api.dto.notification.NotificationResponse;
import community.mingle.api.enums.BoardType;
import community.mingle.api.enums.CountryType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Notification Controller", description = "푸시알림 관련 API")
@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationFacade notificationFacade;


    @Operation(summary = "알림 보내기 API")
    @PostMapping("/send-notification")
    public ResponseEntity<Void> sendPushNotification(@RequestBody @Valid SendPushNotificationRequest request) {
        notificationFacade.sendPushNotification(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "알림 리스트 API")
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications() {
        List<NotificationResponse> notifications = notificationFacade.getNotifications();
        return ResponseEntity.ok().body(notifications);
    }

    @Operation(summary = "알림 읽기 API")
    @PatchMapping("/{notificationId}")
    public ResponseEntity<String> readNotification(@PathVariable Long notificationId) {
        notificationFacade.readNotification(notificationId);
        return ResponseEntity.ok().build();
    }

}
