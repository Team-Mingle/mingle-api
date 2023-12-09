package community.mingle.api.domain.notification.event;

import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.post.entity.Post;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;


/**
 * event에 필요한 데이터
 */
@Getter
public class CommentNotificationEvent extends ApplicationEvent {

    private final Post post;
    private final Member member;
    private final Long parentCommentId;
    private final Long mentionId;
    private final String content;

    public CommentNotificationEvent(Object source, Post post, Member member,Long parentCommentId, Long mentionId, String content) {
        super(source);
        this.post = post;
        this.member = member;
        this.parentCommentId = parentCommentId;
        this.mentionId = mentionId;
        this.content = content;
    }
}
