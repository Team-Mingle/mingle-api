package community.mingle.api.domain.notification.event;

import community.mingle.api.domain.comment.entity.Comment;
import community.mingle.api.domain.comment.service.CommentService;
import community.mingle.api.domain.item.entity.Item;
import community.mingle.api.domain.item.entity.ItemComment;
import community.mingle.api.domain.item.service.ItemCommentService;
import community.mingle.api.domain.item.service.ItemService;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.service.MemberService;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Component
@AllArgsConstructor
@EnableAsync
@Slf4j
public class NotificationEventHandler {

    private final NotificationService notificationService;
    private final PostService postService;
    private final MemberService memberService;
    private final CommentService commentService;
    private final ItemService itemService;
    private final ItemCommentService itemCommentService;
    private final FcmService fcmService;


    private static final String COMMENT_NOTIFICATION_BODY = "새로운 댓글이 달렸어요: ";
    private static final String POPULAR_NOTIFICATION_BODY = "인기 게시물로 지정되었어요.";

    private static final String ITEM_COMMENT_NOTIFICATION_TITLE = "장터";

    //TODO 코드 중복 제거

    @EventListener(RedirectionByBoardTypeManualNotificationEvent.class)
    @Async
    @Transactional
    public void handleRedirectionByBoardTypeManualNotificationEvent(RedirectionByBoardTypeManualNotificationEvent event) {
        List<Member> targetMembers = notificationService.getTargetTokenMembersByCountry(event.getCountryType()); //TODO 태현 timezone 에러로 테스트 불가
        System.out.println("targetMembers = " + targetMembers.size());
        //TODO targetMember 나라별로 가져오는 로직 확인 후 주석 해제하고 알림 보내기
        fcmService.sendAllMessage(
                event.getTitle(), //title, body를 Manual 푸시는 밖에서 받아옴
                event.getBody(),
                null,
                null,
                event.getBoardType(),
                targetMembers.stream().map(Member::getFcmToken).toList()
        );
    }

    @EventListener(TempSignupNotificationEvent.class)
    @Async
    @Transactional
    public void handleTempSignupNotificationEvent(TempSignupNotificationEvent event) {
        fcmService.sendAllMessage(
                event.getTitle(),
                event.getBody(),
                null,
                null,
                null,
                List.of(event.getFcmToken())
        );
    }


    @EventListener(RedirectionByContentIdManualNotificationEvent.class)
    @Async
    @Transactional
    public void handleRedirectionByContentIdManualNotificationEvent(RedirectionByContentIdManualNotificationEvent event) {
        List<Member> targetMembers = notificationService.getTargetTokenMembersByBoardType(event.getBoardType(), event.getContentId()); //TODO 테스트 필요

        log.info("fcm message send target Member count: {}", targetMembers);

        fcmService.sendAllMessage(
                event.getTitle(), //title, body를 Manual 푸시는 밖에서 받아옴
                event.getBody(),
                event.getContentId(),
                event.getContentType(),
                event.getBoardType(),
                targetMembers.stream().map(Member::getFcmToken).toList()
        );
        //TODO: notificationService.cleanNotification(targetMember); manual push는 보내는 유저가 많은것을 고려해 clean은 생략 -> 한방 쿼리로 할 수 있는지 고려
    }

    @EventListener(CommentNotificationEvent.class)
    @Async
    @Transactional
    public void handleCommentNotificationEvent(CommentNotificationEvent event) { //DONE
        Post post = postService.getPost(event.getPostId());
        Member member = memberService.getById(event.getMemberId());
        Comment comment = commentService.getComment(event.getCommentId());
        String title = post.getBoardType().getBoardName(); //manual 푸시와 다르게 title, content를 event 정보로 생성
        String body = COMMENT_NOTIFICATION_BODY + event.getContent();

        List<Member> targetMembers = notificationService.getTargetUserTokenMembersForComment(event.getParentCommentId(), event.getMentionId(), member, post);
        fcmService.sendAllMessage(
                title,
                body,
                post.getId(),
                ContentType.POST,
                null,
                targetMembers.stream().map(Member::getFcmToken).toList()
        );
        targetMembers.forEach(targetMember -> {
            CommentNotification commentNotification = Notification.createCommentNotification(comment, post, targetMember);
            notificationService.saveCommentNotification(commentNotification);
            notificationService.cleanNotification(targetMember);
        });
    }

    @EventListener(ItemCommentNotificationEvent.class)
    @Async
    @Transactional
    public void handleItemCommentNotificationEvent(ItemCommentNotificationEvent event) { //DONE
        String title = ITEM_COMMENT_NOTIFICATION_TITLE; //manual 푸시와 다르게 title, content를 event 정보로 생성
        String body = COMMENT_NOTIFICATION_BODY + event.getContent();
        Member member = memberService.getById(event.getMemberId());
        Item item = itemService.getById(event.getItemId());
        ItemComment itemComment = itemCommentService.getById(event.getItemCommentId());


        List<Member> targetMembers = notificationService.getTargetUserTokenMembersForItemComment(event.getParentCommentId(), event.getMentionId(), member, item);
        fcmService.sendAllMessage(
                title,
                body,
                item.getId(),
                ContentType.ITEM,
                null,
                targetMembers.stream().map(Member::getFcmToken).toList()
        );
        targetMembers.forEach(targetMember -> {
            ItemCommentNotification itemCommentNotification = Notification.createItemCommentNotification(itemComment, item, targetMember);
            notificationService.saveItemCommentNotification(itemCommentNotification);
            notificationService.cleanNotification(targetMember);
        });
    }

    @EventListener(PopularPostNotificationEvent.class)
    @Async
    @Transactional
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
                null,
                targetMembers.stream().map(Member::getFcmToken).toList()
        );
        targetMembers.forEach(targetMember -> {
            PostNotification postNotification = Notification.createPostNotification(post, NotificationType.POPULAR, post.getMember());
            notificationService.savePostNotification(postNotification);
            notificationService.cleanNotification(targetMember);
        });
    }


}
