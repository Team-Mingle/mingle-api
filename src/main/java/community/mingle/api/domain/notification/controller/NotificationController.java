package community.mingle.api.domain.notification.controller;

import community.mingle.api.domain.notification.controller.request.SendPushNotificationRequest;
import community.mingle.api.domain.notification.facade.NotificationFacade;
import community.mingle.api.enums.BoardType;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Notification Controller", description = "푸시알림 관련 API")
@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationFacade notificationFacade;


    @PostMapping("/send-notification/{boardType}")
    public ResponseEntity<Void> sendPushNotification(@PathVariable BoardType boardType, @RequestBody @Valid SendPushNotificationRequest request) {
        notificationFacade.sendPushNotification(boardType, request);
        return ResponseEntity.ok().build();
    }


}
