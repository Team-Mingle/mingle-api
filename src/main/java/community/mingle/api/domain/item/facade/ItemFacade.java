package community.mingle.api.domain.item.facade;

import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.item.controller.request.CreateItemRequest;
import community.mingle.api.domain.item.controller.response.CreateItemResponse;
import community.mingle.api.domain.item.entity.Item;
import community.mingle.api.domain.item.service.ItemImageService;
import community.mingle.api.domain.item.service.ItemService;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemFacade {

    private final ItemService itemService;
    private final ItemImageService itemImageService;
    private final TokenService tokenService;
    private final MemberService memberService;


    @Transactional
    public CreateItemResponse createItemPost(CreateItemRequest request) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);

        Item item = Item.createItem(request, member);
        itemService.saveItem(item);
        itemImageService.createItemImage(item, request.multipartFile());

        return new CreateItemResponse(item.getId());
    }
}
