package community.mingle.api.domain.notification.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;


/**
 * event에 필요한 데이터
 */
@Getter
public class CommentNotificationEvent extends ApplicationEvent {

    private final Long postId;
    private final Long commentId;
    private final Long memberId;
    private final Long parentCommentId;
    private final Long mentionId;
    private final String content;

    public CommentNotificationEvent(Object source, Long postId, Long commentId, Long memberId, Long parentCommentId, Long mentionId, String content) {
        super(source);
        this.postId = postId;
        this.memberId = memberId;
        this.commentId = commentId;
        this.parentCommentId = parentCommentId;
        this.mentionId = mentionId;
        this.content = content;
    }
}
