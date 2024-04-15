package community.mingle.api.global.firebase;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.*;
import community.mingle.api.enums.ContentType;
import community.mingle.api.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static community.mingle.api.global.exception.ErrorCode.FIREBASE_MESSAGING_ERROR;

@Component
@RequiredArgsConstructor
public class FcmService {

    private static final int BATCH_SIZE = 500;
    private final FirebaseApp firebaseApp;

    public Integer sendAllMessage(String title, String body, Long contentId, ContentType contentType, List<String> allTokens)  {
        int successCount = 0;
        int total = allTokens.size();
        int count = 0;
        while (count < total) {
            List<String> tokenSubList = allTokens.subList(count, Math.min(count + BATCH_SIZE, total));
            MulticastMessage messages = buildMessage(title, body, contentId, contentType, tokenSubList);
            BatchResponse batchResponse = null;
            try {
                batchResponse = FirebaseMessaging.getInstance(firebaseApp).sendMulticast(messages);
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
                throw new CustomException(FIREBASE_MESSAGING_ERROR);
            }
            successCount += batchResponse.getSuccessCount();
            System.out.println("successCount = " + successCount);
            count += BATCH_SIZE;
        }
        return successCount;
    }

    public MulticastMessage buildMessage(String title, String body, Long contentId, ContentType contentType, List<String> fcmTokens) {
        Notification notification = new Notification(title, body);
        return MulticastMessage.builder()
                .addAllTokens(fcmTokens)
                .setNotification(notification)
                .putData("contentId", String.valueOf(contentId))
                .putData("contentType", contentType.name())
                .build();
    }

}
