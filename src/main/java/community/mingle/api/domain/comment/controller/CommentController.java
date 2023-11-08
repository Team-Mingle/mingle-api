package community.mingle.api.domain.comment.controller;

import community.mingle.api.domain.comment.controller.request.CreateCommentRequest;
import community.mingle.api.domain.comment.controller.response.CreateCommentResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comment")
public class CommentController {

    public ResponseEntity<CreateCommentResponse> createComment(
            @RequestBody
            @Valid
            CreateCommentRequest createCommentRequest
    ) {

    }
}
