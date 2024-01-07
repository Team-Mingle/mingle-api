package community.mingle.api.domain.item.facade;

import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.item.controller.request.CreateItemRequest;
import community.mingle.api.domain.item.controller.response.CreateItemResponse;
import community.mingle.api.domain.item.controller.response.ItemListResponse;
import community.mingle.api.domain.item.entity.Item;
import community.mingle.api.domain.item.entity.ItemImage;
import community.mingle.api.domain.item.service.ItemImageService;
import community.mingle.api.domain.item.service.ItemService;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.service.MemberService;
import community.mingle.api.dto.item.ItemPreviewDto;
import community.mingle.api.dto.item.ItemStatusDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static community.mingle.api.global.utils.DateTimeConverter.convertToDateAndTime;

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

    public ItemListResponse getItemPostList(PageRequest pageRequest) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        List<Item> itemList = itemService.getPageItemPostList(pageRequest);

        List<ItemPreviewDto> itemPreviewDtoList = itemList.stream()
                .map(item -> mapToItemPreviewDto(item, memberId))
                .toList();
        return new ItemListResponse(itemPreviewDtoList);
    }

    private ItemPreviewDto mapToItemPreviewDto(Item item, Long memberId) {
        ItemStatusDto itemstatusDto = itemService.getItemStatus(item, memberId);
        String nickname = itemService.calculateNickname(item);
        String title = itemService.getTitleByStatus(item);
        String content = itemService.getContentByStatus(item);

        return ItemPreviewDto.builder()
                .id(item.getId())
                .title(title)
                .content(content)
                .price(item.getPrice())
                .nickName(nickname)
                .createdAt(convertToDateAndTime(item.getCreatedAt()))
                .likeCount(item.getItemLikeList().size())
                .commentCount(item.getItemCommentList().size())
                .status(item.getStatus().getName())
                .imgThumbnailUrl(item.getItemImageList() != null ? item.getItemImageList().get(0).getUrl() : null)
                .location(item.getLocation())
                .itemImgList((item.getItemImageList() != null ? item.getItemImageList().stream().map(ItemImage::getUrl).toList() : null))
                .chatUrl(item.getChatUrl())
                .isLiked(itemstatusDto.isLiked())
                .currency(item.getCurrency())
                .build();
    }
}
