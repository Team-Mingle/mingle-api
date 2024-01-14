package community.mingle.api.domain.item.facade;

import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.item.controller.request.CreateItemCommentRequest;
import community.mingle.api.domain.item.controller.response.CreateItemCommentResponse;
import community.mingle.api.domain.item.entity.Item;
import community.mingle.api.domain.item.entity.ItemComment;
import community.mingle.api.domain.item.service.ItemCommentService;
import community.mingle.api.domain.item.service.ItemService;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.service.MemberService;
import community.mingle.api.domain.notification.event.ItemCommentNotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Transactional
@Service
public class ItemCommentFacade {

    private final TokenService tokenService;
    private final MemberService memberService;
    private final ItemService itemService;
    private final ItemCommentService itemCommentService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public CreateItemCommentResponse createComment(CreateItemCommentRequest request) {
        Long memberId = tokenService.getTokenInfo().getMemberId();

        Item item = itemService.getById(request.itemId());
        Member member = memberService.getById(memberId);

        ItemComment itemComment = itemCommentService.createComment(
                item,
                member,
                request.parentCommentId(),
                request.mentionId(),
                request.content(),
                request.isAnonymous()
        );

        applicationEventPublisher.publishEvent(
                new ItemCommentNotificationEvent(this,
                        item,
                        itemComment,
                        member,
                        request.parentCommentId(),
                        request.mentionId(),
                        request.content())
        );

        return new CreateItemCommentResponse(itemComment.getId());
    }
}
