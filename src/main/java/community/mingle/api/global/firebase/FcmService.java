package community.mingle.api.global.firebase;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import community.mingle.api.enums.ContentType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FcmService {

    private static final int BATCH_SIZE = 500;
    private final FirebaseApp firebaseApp;


    public void sendAllMessage(String title, String body, Long contentId, ContentType contentType, List<String> allTokens) {
        int total = allTokens.size();
        int count = 0;
        while (count < total) {
            List<String> tokenSubList = allTokens.subList(count, Math.min(count + BATCH_SIZE, total));
            MulticastMessage messages = buildMessage(title, body, contentId, contentType, tokenSubList);
            FirebaseMessaging.getInstance(firebaseApp).sendMulticastAsync(messages);
            count += BATCH_SIZE;
        }
    }

    public MulticastMessage buildMessage(String title, String body, Long contentId, ContentType contentType, List<String> fcmTokens) {
        Notification notification = new Notification(title, body);
        return MulticastMessage.builder()
                .addAllTokens(fcmTokens)
                .setNotification(notification)
                .putData("postId", String.valueOf(contentId))
                .putData("contentType", contentType.name())
                .build();
    }

}
