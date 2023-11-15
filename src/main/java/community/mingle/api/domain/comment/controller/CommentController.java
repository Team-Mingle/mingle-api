package community.mingle.api.domain.comment.controller;

import community.mingle.api.domain.comment.controller.request.CreateCommentRequest;
import community.mingle.api.domain.comment.controller.response.CreateCommentResponse;
import community.mingle.api.domain.comment.controller.response.DeleteCommentResponse;
import community.mingle.api.domain.comment.facade.CommentFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Comment Controller", description = "댓글 관련 API")
@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentFacade commentFacade;

    @Operation(summary = "댓글 생성 API")
    @PostMapping
    public ResponseEntity<CreateCommentResponse> create(@RequestBody @Valid CreateCommentRequest createCommentRequest) {
        CreateCommentResponse createCommentResponse = commentFacade.create(createCommentRequest);
        return new ResponseEntity<>(createCommentResponse, HttpStatus.OK);
    }

    @Operation(summary = "댓글 삭제 API")
    @PatchMapping("/delete/{commentId}")
    public ResponseEntity<DeleteCommentResponse> delete(@PathVariable Long commentId) {
        DeleteCommentResponse deleteCommentResponse = commentFacade.delete(commentId);
        return new ResponseEntity<>(deleteCommentResponse, HttpStatus.OK);
    }

}
