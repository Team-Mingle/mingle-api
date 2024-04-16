package community.mingle.api.domain.item.facade;

import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.item.controller.request.CreateItemRequest;
import community.mingle.api.domain.item.controller.request.UpdateItemPostRequest;
import community.mingle.api.domain.item.controller.response.CreateItemResponse;
import community.mingle.api.domain.item.controller.response.DeleteItemPostResponse;
import community.mingle.api.domain.item.controller.response.ItemDetailResponse;
import community.mingle.api.domain.item.controller.response.ItemListResponse;
import community.mingle.api.domain.item.entity.Item;
import community.mingle.api.domain.item.entity.ItemComment;
import community.mingle.api.domain.item.entity.ItemImage;
import community.mingle.api.domain.item.service.ItemCommentService;
import community.mingle.api.domain.item.service.ItemImageService;
import community.mingle.api.domain.item.service.ItemService;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.service.MemberService;
import community.mingle.api.domain.post.controller.response.PostDetailCommentResponse;
import community.mingle.api.dto.comment.CoCommentDto;
import community.mingle.api.dto.comment.CommentDto;
import community.mingle.api.dto.item.ItemPreviewDto;
import community.mingle.api.dto.item.ItemStatusDto;
import community.mingle.api.enums.ItemStatusType;
import community.mingle.api.enums.MemberRole;
import community.mingle.api.infra.AmplitudeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static community.mingle.api.enums.ContentStatusType.INACTIVE;
import static community.mingle.api.enums.ContentStatusType.REPORTED;
import static community.mingle.api.global.utils.DateTimeConverter.convertToDateAndTime;

@Service
@RequiredArgsConstructor
public class ItemFacade {

    private final ItemService itemService;
    private final ItemImageService itemImageService;
    private final TokenService tokenService;
    private final MemberService memberService;
    private final ItemCommentService itemCommentService;
    private final AmplitudeService amplitudeService;

    @Transactional
    public CreateItemResponse createItemPost(CreateItemRequest request) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);

        Item item = Item.createItem(request, member);
        itemService.saveItem(item);
        itemImageService.createItemImage(item, request.multipartFile());

        amplitudeService.logEventAsync("createItemPost", memberId);

        return new CreateItemResponse(item.getId());
    }

    public ItemListResponse getItemPostList(PageRequest pageRequest) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);
        List<Item> itemList = itemService.getPageItemPostList(member, pageRequest);

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
                .nickname(nickname)
                .createdAt(convertToDateAndTime(item.getCreatedAt()))
                .likeCount(item.getItemLikeList().size())
                .commentCount(item.getItemCommentList().size())
                .status(item.getStatus().getName())
                .imgThumbnailUrl(!item.getItemImageList().isEmpty() ? item.getItemImageList().get(0).getUrl() : null)
                .location(item.getLocation())
                .itemImgList((!item.getItemImageList().isEmpty() ? item.getItemImageList().stream().map(ItemImage::getUrl).toList() : null))
                .chatUrl(item.getChatUrl())
                .isLiked(itemstatusDto.isLiked())
                .currency(item.getCurrency())
                .build();
    }

    @Transactional
    public ItemDetailResponse getItemPostDetail(Long itemId) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Item item = itemService.getById(itemId);

        itemService.updateView(item);

        return mapToItemDetailResponse(item, memberId);
    }

    private ItemDetailResponse mapToItemDetailResponse(Item item, Long memberId) {
        ItemStatusDto itemStatusDto = itemService.getItemStatus(item, memberId);
        String nickname = itemService.calculateNickname(item);
        String title = itemService.getTitleByStatus(item);
        String content = itemService.getContentByStatus(item);

        return ItemDetailResponse.builder()
                .id(item.getId())
                .title(title)
                .content(content)
                .price(item.getPrice())
                .currency(item.getCurrency())
                .location(item.getLocation())
                .chatUrl(item.getChatUrl())
                .nickname(nickname)
                .likeCount(item.getItemLikeList().size())
                .commentCount(item.getItemCommentList().size())
                .viewCount(item.getViewCount())
                .status(item.getStatus().getName())
                .imgThumbnailUrl(!item.getItemImageList().isEmpty() ? item.getItemImageList().get(0).getUrl() : null)
                .itemImgList(item.getItemImageList() != null ? item.getItemImageList().stream().map(ItemImage::getUrl).toList() : null)
                .isFileAttached(item.getItemImageList() != null)
                .isAnonymous(item.getAnonymous())
                .isMyPost(itemStatusDto.isMyPost())
                .isLiked(itemStatusDto.isLiked())
                .isReported(itemStatusDto.isReported())
                .isBlinded(itemStatusDto.isBlinded())
                .createdAt(convertToDateAndTime(item.getCreatedAt()))
                .build();
    }


    public List<PostDetailCommentResponse> getItemComments(Long itemId) {
        Long memberId = tokenService.getTokenInfo().getMemberId();

        Item item = itemService.getById(itemId);
        if (!itemService.isValidItemPost(item)) return new ArrayList<>();

        List<PostDetailCommentResponse> responseList = new ArrayList<>();

        Map<ItemComment, List<ItemComment>> commentListMap = itemCommentService.getCommentsWithCoCommentsMap(itemId, memberId);
        Map<CommentDto, List<CoCommentDto>> commentDtoListMap = new LinkedHashMap<>();

        commentListMap.forEach((comment, coCommentList) -> {
            CommentDto commentDto = createCommentDto(comment, memberId, item.getMember().getId());
            List<CoCommentDto> coCommentDtos = coCommentList.stream()
                    .map(it -> createCoCommentDto(it, memberId, item.getMember().getId()))
                    .toList();
            commentDtoListMap.put(commentDto, coCommentDtos);
        });
        commentDtoListMap.forEach((commentDto, coCommentDtoList) ->
                responseList.add(createCommentResponse(commentDto, coCommentDtoList)));

        return responseList;
    }


    private CommentDto createCommentDto(ItemComment comment, Long memberId, Long postAuthorId) {
        //TODO isAdmin -> role, isDeleted/isReported -> status 변경가능?
        return CommentDto.builder()
                .commentId(comment.getId())
                .nickname(itemCommentService.getDisplayName(comment, postAuthorId))
                .content(itemCommentService.getContentByStatus(comment))
                .likeCount(comment.getItemCommentLikes().size())
                .isLiked(itemCommentService.isCommentLikedByMember(comment, memberId))
                .isMyComment(memberId.equals(comment.getMember().getId()))
                .isCommentFromAuthor(postAuthorId.equals(comment.getMember().getId()))
                .isCommentDeleted(comment.getStatus() == INACTIVE)
                .isCommentReported(comment.getStatus() == REPORTED)
                .createdAt(convertToDateAndTime(comment.getCreatedAt()))
                .isAdmin(comment.getMember().getRole().equals(MemberRole.ADMIN))
                .build();
    }

    private CoCommentDto createCoCommentDto(ItemComment coComment, Long memberId, Long postAuthorId) {
        return CoCommentDto.builder()
                .commentId(coComment.getId())
                .parentCommentId(coComment.getParentCommentId()) //
                .mention(itemCommentService.getMentionDisplayName(coComment.getMentionId(), postAuthorId)) //
                .nickname(itemCommentService.getDisplayName(coComment, postAuthorId))
                .content(itemCommentService.getContentByStatus(coComment))
                .likeCount(coComment.getItemCommentLikes().size())
                .isLiked(itemCommentService.isCommentLikedByMember(coComment, memberId))
                .isMyComment(memberId.equals(coComment.getMember().getId()))
                .isCommentFromAuthor(postAuthorId.equals(coComment.getMember().getId()))
                .isCommentDeleted(coComment.getStatus() == INACTIVE)
                .isCommentReported(coComment.getStatus() == REPORTED)
                .createdAt(convertToDateAndTime(coComment.getCreatedAt()))
                .isAdmin(coComment.getMember().getRole().equals(MemberRole.ADMIN))
                .build();
    }


    private PostDetailCommentResponse createCommentResponse(CommentDto commentDto, List<CoCommentDto> coCommentDtoList) {
        //TODO isAdmin -> role, isDeleted/isReported -> status 변경가능?
        return PostDetailCommentResponse.builder()
                .commentId(commentDto.getCommentId())
                .nickname(commentDto.getNickname())
                .content(commentDto.getContent())
                .likeCount(commentDto.getLikeCount())
                .isLiked(commentDto.isLiked())
                .isMyComment(commentDto.isMyComment())
                .isCommentFromAuthor(commentDto.isCommentFromAuthor())
                .isCommentDeleted(commentDto.isCommentDeleted())
                .isCommentReported(commentDto.isCommentReported())
                .createdAt(commentDto.getCreatedAt())
                .isAdmin(commentDto.isAdmin())
                .coCommentsList(coCommentDtoList)
                .build();
    }

    @Transactional
    public ItemDetailResponse updateItemPost(UpdateItemPostRequest request, Long itemId) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Item item = itemService.getValidItem(itemId);

        Item updatedItem = item.updateItemPost(
                memberId,
                request.title(),
                request.content(),
                request.price(),
                request.currency(),
                request.location(),
                request.chatUrl(),
                request.isAnonymous()
        );

        itemImageService.updateItemImage(item, request.imageUrlsToDelete(), request.imagesToAdd());

        return mapToItemDetailResponse(updatedItem, memberId);
    }


    @Transactional
    public DeleteItemPostResponse deleteItemPost(Long itemId) {
        Long memberId = tokenService.getTokenInfo().getMemberId();

        itemService.deleteItemPost(memberId, itemId);
        itemCommentService.deleteAllByItemId(itemId);
        itemImageService.deleteItemImage(itemId);

        return DeleteItemPostResponse.builder()
                .deleted(true)
                .build();
    }

    @Transactional
    public void updateItemLike(Long itemId) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);
        Item item = itemService.getValidItem(itemId);

        if (itemService.isItemLiked(item, member)) {
            itemService.deleteItemLike(item, member);
        } else {
            itemService.createItemLike(item, member);
        }
    }

    @Transactional
    public void updateItemStatus(Long itemId, ItemStatusType itemStatusType) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);
        Item item = itemService.getValidItem(itemId);
        itemService.updateItemStatus(item, itemStatusType, member);
    }

    @Transactional
    public void updateItemBlind(Long itemId) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);
        Item item = itemService.getValidItem(itemId);

        if (itemService.isItemBlinded(item, member)) {
            itemService.deleteItemBlind(item, member);
        } else {
            itemService.createItemBlind(item, member);
        }
    }

    public ItemListResponse getSearchItemList(String keyword, PageRequest pageRequest) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);
        List<Item> itemList = itemService.searchItemWithKeyword(keyword, member, pageRequest);

        List<ItemPreviewDto> itemPreviewDtoList = itemList.stream()
                .map(item -> mapToItemPreviewDto(item, memberId))
                .toList();
        return new ItemListResponse(itemPreviewDtoList);
    }

    public ItemListResponse getMyPageItemList(PageRequest pageRequest, ItemStatusType itemStatusType) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);

        List<Item> itemList = itemService.pageItemPostsByMember(member, pageRequest);
        if (itemList.isEmpty()) return new ItemListResponse(List.of());

        List<ItemPreviewDto> itemPreviewDtoList = itemList.stream()
                .filter(item -> item.getStatus() == itemStatusType)
                .map(item -> mapToItemPreviewDto(item, memberId))
                .collect(Collectors.toList());
        return new ItemListResponse(itemPreviewDtoList);
    }

    public ItemListResponse getMyPageItemLikeList(PageRequest pageRequest, ItemStatusType itemStatusType) {
        Long memberId = tokenService.getTokenInfo().getMemberId();
        Member member = memberService.getById(memberId);

        List<Item> itemList = itemService.pageItemLikePostsByMember(member, pageRequest);
        if (itemList.isEmpty()) return new ItemListResponse(List.of());

        List<ItemPreviewDto> itemPreviewDtoList = itemList.stream()
                .filter(item -> item.getStatus() == itemStatusType)
                .map(item -> mapToItemPreviewDto(item, memberId))
                .collect(Collectors.toList());
        return new ItemListResponse(itemPreviewDtoList);
    }
}
