package community.mingle.api.domain.item.service;

import community.mingle.api.domain.item.entity.ItemComment;
import community.mingle.api.domain.item.entity.ItemCommentLike;
import community.mingle.api.domain.item.repository.ItemCommentLikeRepository;
import community.mingle.api.domain.item.repository.ItemCommentRepository;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.repository.MemberRepository;
import community.mingle.api.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static community.mingle.api.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ItemCommentLikeService {

    private final ItemCommentRepository itemCommentRepository;
    private final ItemCommentLikeRepository itemCommentLikeRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ItemCommentLike create(Long commentId, Long memberId) {
        ItemComment comment = itemCommentRepository.findById(commentId).orElseThrow(() -> new CustomException(COMMENT_NOT_FOUND));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new CustomException(MEMBER_NOT_FOUND));

        ItemCommentLike itemCommentLike = ItemCommentLike.builder()
                .itemComment(comment)
                .member(member)
                .build();
        return itemCommentLikeRepository.save(itemCommentLike);
    }

    @Transactional
    public void delete(Long commentId, Long memberId) {
        ItemCommentLike commentLike = itemCommentLikeRepository.findByItemCommentIdAndMemberId(commentId, memberId).orElseThrow(() -> new CustomException(LIKE_NOT_FOUND));
        if (!commentLike.getMember().getId().equals(memberId)) {
            throw new CustomException(MODIFY_NOT_AUTHORIZED);
        }
        itemCommentLikeRepository.delete(commentLike);
    }

    public boolean isCommentLiked(Long commentId, Long memberId) {
        return itemCommentLikeRepository.existsByItemCommentIdAndMemberId(commentId, memberId);
    }
}
