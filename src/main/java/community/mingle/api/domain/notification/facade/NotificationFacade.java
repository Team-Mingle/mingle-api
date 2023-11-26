package community.mingle.api.domain.notification.facade;

import community.mingle.api.domain.notification.controller.request.SendPushNotificationRequest;
import community.mingle.api.domain.notification.service.NotificationService;
import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.domain.post.service.PostService;
import community.mingle.api.enums.BoardType;
import community.mingle.api.global.firebase.FcmService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationFacade {

    private final PostService postService;
    private final NotificationService notificationService;
    private final FcmService fcmService;


    public String sendPushNotification(BoardType boardType, SendPushNotificationRequest request) {
        Post post = postService.getPost(request.getContentId());
        List<String> targetUserTokens = notificationService.findTargetUserTokensByBoardType(boardType, post);
        fcmService.sendAllMessage(
                request.getTitle(),
                request.getBody(),
                request.getContentId(),
                request.getContentType(),
                targetUserTokens
        );
        return "성공했습니다.";
    }
}
