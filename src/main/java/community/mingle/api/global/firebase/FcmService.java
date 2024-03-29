package community.mingle.api.global.firebase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.*;
import community.mingle.api.enums.ContentType;
import community.mingle.api.global.exception.CustomException;
import community.mingle.api.global.exception.ErrorCode;
import community.mingle.api.infra.SecretsManagerService;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static community.mingle.api.global.exception.ErrorCode.FIREBASE_MESSAGING_ERROR;

@Component
@RequiredArgsConstructor
public class FcmService {

    private static final int BATCH_SIZE = 500;
    private final FirebaseApp firebaseApp;
    private final ObjectMapper objectMapper;
    private final SecretsManagerService secretsManagerService;
    private final String profile;
    private final String API_URL = "https://fcm.googleapis.com/v1/projects/mingle-new/messages:send"; //dev

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
            count += BATCH_SIZE;
        }
        return successCount;
    }

    public void sendMessage(String title, String body, Long contentId, ContentType contentType, List<String> allTokens) throws IOException {
        String message = makeMessage(allTokens.get(0), contentType,  title, body, contentId);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(API_URL).post(requestBody).addHeader("AUTHORIZATION", "Bearer " + getAccessToken())
                .addHeader("CONTENT_TYPE", "application/json; UTF-8")
                .build();
        Response response = client.newCall(request).execute();
        System.out.println(response.body().string());
    }

    private String makeMessage(String targetToken, ContentType contentType, String title, String body, Long postId) throws JsonProcessingException {
        FcmMessage fcmMessage = FcmMessage.builder()
                .message(FcmMessage.Message.builder().token(targetToken)
                        .notification(FcmMessage.Notification.builder().title(title).body(body).image(null).build())
                        .data(FcmMessage.Data.builder().
                                contentType(contentType.name()).
                                postId(String.valueOf(postId)).build())
                        .build())
                .validate_only(false).build();
        return objectMapper.writeValueAsString(fcmMessage);
    }

    private String getAccessToken() throws IOException {
        InputStream firebaseToken = convertToStream(secretsManagerService.getFcmToken(profile));
        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(firebaseToken)
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
        googleCredentials.refreshIfExpired();
        return googleCredentials.getAccessToken().getTokenValue();
    }

    private InputStream convertToStream(String token) {
        return new ByteArrayInputStream(token.getBytes());
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
