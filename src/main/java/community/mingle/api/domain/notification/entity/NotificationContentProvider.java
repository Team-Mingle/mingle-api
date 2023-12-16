package community.mingle.api.domain.notification.entity;

/**
 * NotificationResponse를 만들 떄
 * Notification 종류마다 각기 다른 content, boardType, categoryType을 제공하기 위한 인터페이스
 */
public interface NotificationContentProvider {

    String getContent();
    String getBoardType();
    String getCategoryType();
}

