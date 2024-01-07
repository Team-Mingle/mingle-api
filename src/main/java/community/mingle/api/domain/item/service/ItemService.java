package community.mingle.api.domain.item.service;

import community.mingle.api.domain.item.entity.Item;
import community.mingle.api.domain.item.repository.ItemLikeRepository;
import community.mingle.api.domain.item.repository.ItemReportRepository;
import community.mingle.api.domain.item.repository.ItemRepository;
import community.mingle.api.domain.report.entity.ItemReport;
import community.mingle.api.domain.report.entity.Report;
import community.mingle.api.dto.item.ItemStatusDto;
import community.mingle.api.dto.post.PostStatusDto;
import community.mingle.api.enums.MemberRole;
import community.mingle.api.enums.ReportType;
import community.mingle.api.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static community.mingle.api.global.exception.ErrorCode.POST_NOT_EXIST;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemLikeRepository itemLikeRepository;
    private final ItemReportRepository itemReportRepository;

    @Transactional
    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }


    public List<Item> getPageItemPostList(PageRequest pageRequest) {
        return itemRepository.findAll(pageRequest).toList();
    }

    public ItemStatusDto getItemStatus(Item item, Long memberId) {
        boolean isMyPost = Objects.equals(item.getMember().getId(), memberId);
        boolean isLiked  = itemLikeRepository.countByItemIdAndMemberId(item.getId(), memberId) > 0;
        return new ItemStatusDto(isMyPost, isLiked);
    }


    public String calculateNickname(Item item) {
        if (item.getAnonymous()) {
            return "익명";
        } else if (item.getMember().getRole() == MemberRole.FRESHMAN) {
            return "🐥" + item.getMember().getNickname();
        } else {
            return item.getMember().getNickname();
        }
    }

    public String getTitleByStatus(Item item) {
        return switch (item.getStatus()) {
            case INACTIVE -> throw new CustomException(POST_NOT_EXIST);
            case REPORTED -> "다른 사용자들의 신고에 의해 삭제된 글 입니다.";
            case DELETED -> "운영규칙 위반에 따라 삭제된 글입니다.";
            default -> item.getTitle();
        };
    }

    public String getContentByStatus(Item item) {
        return switch (item.getStatus()) {
            case INACTIVE -> throw new CustomException(POST_NOT_EXIST);
            case REPORTED -> {
                ReportType reportType = getReportedItemReason(item.getId());
                yield "사유: " + reportType.getDescription();
            }
            case DELETED -> "사유: 이용약관 제 12조 위반";
            default -> item.getContent();
        };
    }

    private ReportType getReportedItemReason(Long itemId) {
        List<ItemReport> itemReportList = itemReportRepository.findByItemId(itemId);

        if (itemReportList == null || itemReportList.isEmpty()) {
            return null;
        }
        return itemReportList.stream()
                .collect(Collectors.groupingBy(Report::getReportType, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }


}
