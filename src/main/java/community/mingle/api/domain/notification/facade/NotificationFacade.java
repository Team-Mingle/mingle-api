package community.mingle.api.domain.notification.facade;

import community.mingle.api.domain.notification.event.ManualNotificationEvent;
import community.mingle.api.domain.notification.controller.request.SendPushNotificationRequest;
import community.mingle.api.enums.BoardType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationFacade {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void sendPushNotification(BoardType boardType, SendPushNotificationRequest request) {
        applicationEventPublisher.publishEvent(
                new ManualNotificationEvent(this, boardType, request.getTitle(), request.getBody(), request.getContentId(), request.getContentType())
        );
    }


}
