package community.mingle.api.domain.notification.event;

import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.notification.entity.Notification;
import community.mingle.api.domain.notification.service.NotificationService;
import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.domain.post.service.PostService;
import community.mingle.api.enums.ContentType;
import community.mingle.api.enums.NotificationType;
import community.mingle.api.global.firebase.FcmService;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@AllArgsConstructor
public class NotificationEventHandler {

    private final NotificationService notificationService;
    private final PostService postService;
    private final FcmService fcmService;


    private static final String COMMENT_NOTIFICATION_BODY = "새로운 댓글이 달렸어요: ";
    private static final String POPULAR_NOTIFICATION_BODY = "인기 게시물로 지정되었어요.";

    @EventListener(ManualNotificationEvent.class)
    @Async
    public void handleManualNotificationEvent(ManualNotificationEvent event) {
        Post post = postService.getPost(event.getContentId());
        List<Member> targetMembers = notificationService.getTargetTokenMembersByBoardType(event.getBoardType(), post);
        fcmService.sendAllMessage(
                event.getTitle(),
                event.getBody(),
                event.getContentId(),
                event.getContentType(),
                targetMembers.stream().map(Member::getFcmToken).toList()
        );
        saveNotification(NotificationType.POST, ContentType.POST, event.getContentId(), targetMembers);
    }

    @EventListener(CommentNotificationEvent.class)
    public void handleCommentNotificationEvent(CommentNotificationEvent event) {
        String title = event.getPost().getBoardType().getBoardName();
        String body = COMMENT_NOTIFICATION_BODY + event.getContent();

        List<Member> targetMembers = notificationService.getTargetUserTokenMembersForComment(event.getParentCommentId(), event.getMentionId(), event.getMember(), event.getPost());
        fcmService.sendAllMessage(
                title,
                body,
                event.getPost().getId(),
                ContentType.POST,
                targetMembers.stream().map(Member::getFcmToken).toList()
        );
        saveNotification(NotificationType.COMMENT, ContentType.POST, event.getPost().getId(), targetMembers);
    }

    @EventListener(PopularPostNotificationEvent.class)
    public void handlePopularPostNotificationEvent(PopularPostNotificationEvent event) {
        Post post = postService.getPost(event.getPostId());

        String title = post.getBoardType().name();
        String body = POPULAR_NOTIFICATION_BODY;

        List<Member> targetMember = List.of(post.getMember());
        fcmService.sendAllMessage(
                title,
                body,
                event.getPostId(),
                ContentType.POST,
                targetMember.stream().map(Member::getFcmToken).toList()
        );
        saveNotification(NotificationType.POPULAR, ContentType.POST, event.getPostId(), targetMember);
    }

    private void saveNotification(NotificationType notificationType, ContentType contentType, Long contentId, List<Member> targetMembers) {
        targetMembers.forEach(targetMember -> {
            Notification notification = Notification.builder()
                    .member(targetMember)
                    .notificationType(notificationType)
                    .contentType(contentType)
                    .contentId(contentId)
                    .build();
            notificationService.save(notification);
            notificationService.cleanNotification(targetMember);
        });
    }


}
