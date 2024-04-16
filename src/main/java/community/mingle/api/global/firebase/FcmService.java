package community.mingle.api.global.firebase;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.*;
import community.mingle.api.enums.BoardType;
import community.mingle.api.enums.ContentType;
import community.mingle.api.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static community.mingle.api.global.exception.ErrorCode.FIREBASE_MESSAGING_ERROR;
import static community.mingle.api.global.exception.ErrorCode.NOTIFICATION_BAD_REQUEST;

@Component
@RequiredArgsConstructor
public class FcmService {

    private static final int BATCH_SIZE = 99;
    private final FirebaseApp firebaseApp;

    public void sendAllMessage(String title, String body, Long contentId, ContentType contentType, BoardType boardType, List<String> allTokens) {
        int total = allTokens.size();
        int count = 0;
        while (count < total) {
            List<String> tokenSubList = allTokens.subList(count, Math.min(count + BATCH_SIZE, total));
            MulticastMessage messages = buildMessage(title, body, contentId, contentType, boardType, tokenSubList);

            try {
                FirebaseMessaging.getInstance(firebaseApp).sendMulticast(messages);
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
                throw new CustomException(FIREBASE_MESSAGING_ERROR);
            }
            count += BATCH_SIZE;
        }
    }

    public MulticastMessage buildRedirectByContentIdMessage(String title, String body, Long contentId, ContentType contentType, List<String> fcmTokens) {
        Notification notification = new Notification(title, body);
        return MulticastMessage.builder()
                .addAllTokens(fcmTokens)
                .setNotification(notification)
                .putData("contentId", String.valueOf(contentId))
                .putData("contentType", contentType.name())
                .build();
    }

    public MulticastMessage buildRedirectByBoardTypeMessage(String title, String body, BoardType boardType, List<String> fcmTokens) {
        Notification notification = new Notification(title, body);
        return MulticastMessage.builder()
                .addAllTokens(fcmTokens)
                .setNotification(notification)
                .putData("boardType", boardType.name())
                .build();
    }

    public MulticastMessage buildNoRedirectTypeMessage(String title, String body, List<String> fcmTokens) {
        Notification notification = new Notification(title, body);
        return MulticastMessage.builder()
                .addAllTokens(fcmTokens)
                .setNotification(notification)
                .build();
    }

    public MulticastMessage buildMessage(String title, String body, Long contentId, ContentType contentType, BoardType boardType, List<String> tokens) {
        Notification notification = new Notification(title, body);
        MulticastMessage.Builder messageBuilder = MulticastMessage.builder()
                .addAllTokens(tokens)
                .setNotification(notification);

        if (contentId != null && contentType != null) {
            messageBuilder.putData("contentId", String.valueOf(contentId))
                    .putData("contentType", contentType.name());
        } else if (boardType != null) {
            messageBuilder.putData("boardType", boardType.name());
        }

        return messageBuilder.build();
    }

}
