package community.mingle.api.domain.item.controller;

import community.mingle.api.domain.item.controller.request.CreateItemCommentRequest;
import community.mingle.api.domain.item.controller.response.CreateItemCommentResponse;
import community.mingle.api.domain.item.facade.ItemCommentFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "ItemComment Controller", description = "댓글 관련 API")
@RestController
@RequestMapping("/itemcomment")
@RequiredArgsConstructor
public class ItemCommentController {

    private final ItemCommentFacade itemCommentFacade;




}