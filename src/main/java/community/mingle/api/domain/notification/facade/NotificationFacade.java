package community.mingle.api.domain.notification.facade;

import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.service.MemberService;
import community.mingle.api.domain.notification.controller.request.SendPushNotificationRequest;
import community.mingle.api.domain.notification.entity.Notification;
import community.mingle.api.domain.notification.entity.NotificationContentProvider;
import community.mingle.api.domain.notification.event.ManualNotificationEvent;
import community.mingle.api.domain.notification.service.NotificationService;
import community.mingle.api.dto.notification.NotificationResponse;
import community.mingle.api.enums.BoardType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static community.mingle.api.global.utils.DateTimeConverter.convertToDateAndTime;

@Service
@RequiredArgsConstructor
public class NotificationFacade {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final MemberService memberService;
    private final TokenService tokenService;
    private final NotificationService notificationService;

    public void sendPushNotification(BoardType boardType, SendPushNotificationRequest request) {
        applicationEventPublisher.publishEvent(
                new ManualNotificationEvent(this, boardType, request.getTitle(), request.getBody(), request.getContentId(), request.getContentType())
        );
    }


    public List<NotificationResponse> getNotifications() {
        Long memberId = tokenService.getTokenInfo().getMemberId(); //TODO 이 2줄 사용처 모두 메서드 하나로 합치기
        Member member = memberService.getById(memberId);
        List<Notification> notifications = notificationService.getNotifications(member.getId());

        return notifications.stream()
                .filter(NotificationContentProvider.class::isInstance)
                .map(notification -> buildResponse(notification, (NotificationContentProvider) notification))
                .collect(Collectors.toList());
    }

    private NotificationResponse buildResponse(Notification notification, NotificationContentProvider contentProvider) {
        return NotificationResponse.builder()
                .notificationId(notification.getId())
                .contentId(notification.getContentId())
                .contentType(notification.getContentType())
                .notificationType(notification.getNotificationType().getName())
                .isRead(notification.getRead())
                .content(contentProvider.getContent())
                .boardType(contentProvider.getBoardType())
                .categoryType(contentProvider.getCategoryType())
                .createAt(convertToDateAndTime(notification.getCreatedAt()))
                .build();
    }

    @Transactional
    public void readNotification(Long notificationId) {
        notificationService.readNotification(notificationId);
    }

}
