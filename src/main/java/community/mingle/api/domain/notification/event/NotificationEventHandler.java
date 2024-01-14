package community.mingle.api.domain.notification.event;

import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.notification.entity.CommentNotification;
import community.mingle.api.domain.notification.entity.ItemCommentNotification;
import community.mingle.api.domain.notification.entity.Notification;
import community.mingle.api.domain.notification.entity.PostNotification;
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

    private static final String ITEM_COMMENT_NOTIFICATION_TITLE = "장터";

    //TODO 코드 중복 제거

    @EventListener(ManualNotificationEvent.class)
    @Async
    public void handleManualNotificationEvent(ManualNotificationEvent event) {
        Post post = postService.getPost(event.getContentId());
        List<Member> targetMembers = notificationService.getTargetTokenMembersByBoardType(event.getBoardType(), post);
        fcmService.sendAllMessage(
                event.getTitle(), //title, body를 Manual 푸시는 밖에서 받아옴
                event.getBody(),
                event.getContentId(),
                event.getContentType(),
                targetMembers.stream().map(Member::getFcmToken).toList()
        );
        List<PostNotification> postNotifications = targetMembers.stream()
                .map(targetMember -> Notification.createPostNotification(post, NotificationType.POST, targetMember)).toList();
        notificationService.saveAllManualPostNotification(postNotifications);
        //TODO: notificationService.cleanNotification(targetMember); manual push는 보내는 유저가 많은것을 고려해 clean은 생략 -> 한방 쿼리로 할 수 있는지 고려
    }

    @EventListener(CommentNotificationEvent.class)
    public void handleCommentNotificationEvent(CommentNotificationEvent event) { //DONE
        String title = event.getPost().getBoardType().getBoardName(); //manual 푸시와 다르게 title, content를 event 정보로 생성
        String body = COMMENT_NOTIFICATION_BODY + event.getContent();

        List<Member> targetMembers = notificationService.getTargetUserTokenMembersForComment(event.getParentCommentId(), event.getMentionId(), event.getMember(), event.getPost());
        fcmService.sendAllMessage(
                title,
                body,
                event.getPost().getId(),
                ContentType.POST,
                targetMembers.stream().map(Member::getFcmToken).toList()
        );
        targetMembers.forEach(targetMember -> {
            CommentNotification commentNotification = Notification.createCommentNotification(event.getComment(), event.getPost(), targetMember);
            notificationService.saveCommentNotification(commentNotification);
            notificationService.cleanNotification(targetMember);
        });
    }

    @EventListener(ItemCommentNotificationEvent.class)
    public void handleItemCommentNotificationEvent(ItemCommentNotificationEvent event) { //DONE
        String title = ITEM_COMMENT_NOTIFICATION_TITLE; //manual 푸시와 다르게 title, content를 event 정보로 생성
        String body = COMMENT_NOTIFICATION_BODY + event.getContent();

        List<Member> targetMembers = notificationService.getTargetUserTokenMembersForItemComment(event.getParentCommentId(), event.getMentionId(), event.getMember(), event.getItem());
        fcmService.sendAllMessage(
                title,
                body,
                event.getItem().getId(),
                ContentType.ITEM,
                targetMembers.stream().map(Member::getFcmToken).toList()
        );
        targetMembers.forEach(targetMember -> {
            ItemCommentNotification itemCommentNotification = Notification.createItemCommentNotification(event.getItemComment(), event.getItem(), targetMember);
            notificationService.saveItemCommentNotification(itemCommentNotification);
            notificationService.cleanNotification(targetMember);
        });
    }

    @EventListener(PopularPostNotificationEvent.class)
    public void handlePopularPostNotificationEvent(PopularPostNotificationEvent event) {
        Post post = postService.getPost(event.getPostId());

        String title = post.getBoardType().name();
        String body = POPULAR_NOTIFICATION_BODY;

        List<Member> targetMembers = List.of(post.getMember());
        fcmService.sendAllMessage(
                title,
                body,
                event.getPostId(),
                ContentType.POST,
                targetMembers.stream().map(Member::getFcmToken).toList()
        );
        targetMembers.forEach(targetMember -> {
            PostNotification postNotification = Notification.createPostNotification(post, NotificationType.POPULAR, post.getMember());
            notificationService.savePostNotification(postNotification);
            notificationService.cleanNotification(targetMember);
        });
    }


}
