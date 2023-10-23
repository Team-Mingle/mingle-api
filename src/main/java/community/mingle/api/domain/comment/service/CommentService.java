package community.mingle.api.domain.comment.service;


import community.mingle.api.domain.comment.entity.Comment;
import community.mingle.api.domain.comment.repository.CommentRepository;
import community.mingle.api.domain.post.entity.PostImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    @Transactional
    public void deleteComment(Long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        commentRepository.deleteAll(comments);
    }
}
