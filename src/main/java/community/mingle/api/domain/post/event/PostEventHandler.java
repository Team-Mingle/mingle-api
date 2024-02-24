package community.mingle.api.domain.post.event;

import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.domain.post.entity.PostViewCountSession;
import community.mingle.api.domain.post.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@AllArgsConstructor
@EnableAsync
public class PostEventHandler {

    private final PostService postService;
    private static final Long SESSION_LIVE_LIMIT = 60L;

    @EventListener(ReadPostEvent.class)
    @Async
    @Transactional(readOnly = true)
    public void handleReadPostEvent(ReadPostEvent event) {
        Optional<PostViewCountSession> postViewCountSession = postService.getPostViewCountSessionByMemberIdAndPostId(
                event.getMemberId(), event.getPostId()
        );
        Post post = postService.getPost(event.getPostId());
        postViewCountSession.ifPresentOrElse(
                session -> {
                    if (session.getMember().getId().equals(event.getMemberId()) && session.getPost().getId().equals(event.getPostId())) {
                        if (session.getLastViewAt().plusSeconds(SESSION_LIVE_LIMIT).isBefore(LocalDateTime.now())) {
                            session.updateLastViewAt();
                            postService.updateView(post);
                        }
                    }
                },
                () -> {
                    postService.createPostViewCountSession(event.getMemberId(), event.getPostId());
                    postService.updateView(post);
                });
    }
}
