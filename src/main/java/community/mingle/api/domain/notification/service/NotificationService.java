package community.mingle.api.domain.notification.service;

import community.mingle.api.domain.comment.repository.CommentRepository;
import community.mingle.api.domain.item.entity.Item;
import community.mingle.api.domain.item.repository.ItemCommentRepository;
import community.mingle.api.domain.item.repository.ItemRepository;
import community.mingle.api.domain.member.entity.Country;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.repository.CountryRepository;
import community.mingle.api.domain.member.repository.MemberRepository;
import community.mingle.api.domain.notification.entity.CommentNotification;
import community.mingle.api.domain.notification.entity.ItemCommentNotification;
import community.mingle.api.domain.notification.entity.Notification;
import community.mingle.api.domain.notification.entity.PostNotification;
import community.mingle.api.domain.notification.repository.CommentNotificationRepository;
import community.mingle.api.domain.notification.repository.ItemCommentNotificationRepository;
import community.mingle.api.domain.notification.repository.NotificationRepository;
import community.mingle.api.domain.notification.repository.PostNotificationRepository;
import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.domain.post.repository.PostRepository;
import community.mingle.api.enums.BoardType;
import community.mingle.api.enums.CountryType;
import community.mingle.api.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static community.mingle.api.enums.BoardType.*;
import static community.mingle.api.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final CommentNotificationRepository commentNotificationRepository;
    private final ItemCommentNotificationRepository itemCommentNotificationRepository;
    private final PostNotificationRepository postNotificationRepository;
    private final NotificationRepository notificationRepository;
    private final ItemCommentRepository itemCommentRepository;
    private final CountryRepository countryRepository;
    private final PostRepository postRepository;
    private final ItemRepository itemRepository;


    @Transactional
    public Notification saveItemCommentNotification(ItemCommentNotification notification) {
        return itemCommentNotificationRepository.save(notification);
    }


    @Transactional
    public Notification saveCommentNotification(CommentNotification notification) {
        return commentNotificationRepository.save(notification);
    }

    @Transactional
    public Notification savePostNotification(PostNotification notification) {
        return postNotificationRepository.save(notification);
    }

    @Transactional
    public void saveAllManualPostNotification(List<PostNotification> notifications) {
        postNotificationRepository.saveAll(notifications);
    }

    public List<Member> getTargetTokenMembersByBoardType(BoardType boardType, Long contentId) {
        List<Member> tokenMembers = new ArrayList<>();
        if (boardType.equals(UNIV)) {
            Post post = postRepository.findById(contentId).orElseThrow(() -> new CustomException(POST_NOT_EXIST));
            tokenMembers = memberRepository.findAllByUniversity(post.getMember().getUniversity());
        }
        if (boardType.equals(TOTAL)) {
            Post post = postRepository.findById(contentId).orElseThrow(() -> new CustomException(POST_NOT_EXIST));
            tokenMembers = memberRepository.findAllByUniversityCountry(post.getMember().getUniversity().getCountry());
        }
        if (boardType.equals(ITEM)) {
            Item item = itemRepository.findById(contentId).orElseThrow(() -> new CustomException(POST_NOT_EXIST));
            tokenMembers = memberRepository.findAllByUniversityCountry(item.getMember().getUniversity().getCountry());
        }
        return tokenMembers.stream()
                .filter(member -> member.getFcmToken() != null)
                .collect(Collectors.toList());
    }

    public List<Member> getTargetTokenMembersByCountry(CountryType countryType) {
        if (countryType == null) {
            return memberRepository.findAll()
                    .stream()
                    .filter(member -> member.getFcmToken() != null && !member.getFcmToken().isEmpty())
                    .collect(Collectors.toList());
        }
        Country country = countryRepository.findById(countryType.name())
                .orElseThrow(() -> new CustomException(COUNTRY_NOT_FOUND));
        return memberRepository.findAllByUniversityCountry(country)
                .stream()
                .filter(member -> member.getFcmToken() != null && !member.getFcmToken().isEmpty())
                .collect(Collectors.toList());
    }


    public List<Member> getTargetUserTokenMembersForComment(Long parentCommentId, Long mentionId, Member creatorMember, Post post) {
        if (parentCommentId == null && post.getMember().equals(creatorMember)) {
            return Collections.emptyList();
        }
        Set<Member> targetMembers = new HashSet<>();
        targetMembers.add(post.getMember());
        if (parentCommentId != null)
            commentRepository.findById(parentCommentId).ifPresent(comment -> targetMembers.add(comment.getMember()));
        if (mentionId != null)
            commentRepository.findById(mentionId).ifPresent(comment -> targetMembers.add(comment.getMember()));
        targetMembers.remove(creatorMember);
        return new ArrayList<>(targetMembers);
    }

    public List<Member> getTargetUserTokenMembersForItemComment(Long parentCommentId, Long mentionId, Member creatorMember, Item item) {
        if (parentCommentId == null && item.getMember().equals(creatorMember)) {
            return Collections.emptyList();
        }
        Set<Member> targetMembers = new HashSet<>();
        targetMembers.add(item.getMember());
        if (parentCommentId != null)
            itemCommentRepository.findById(parentCommentId).ifPresent(comment -> targetMembers.add(comment.getMember()));
        if (mentionId != null)
            itemCommentRepository.findById(mentionId).ifPresent(comment -> targetMembers.add(comment.getMember()));
        targetMembers.remove(creatorMember);
        return new ArrayList<>(targetMembers);
    }


    @Transactional
    public void cleanNotification(Member member) {
        if (member.getNotifications().size() > 20)
            notificationRepository.delete(member.getNotifications().get(0));
    }

    public List<Notification> getNotifications(Long memberId) {
        return notificationRepository.findFirst20ByMemberIdOrderByCreatedAtDesc(memberId);
    }

    @Transactional
    public void readNotification(Long notificationId) {
        notificationRepository.findById(notificationId)
            .ifPresentOrElse(
                    notification -> notification.markAsRead(),
                    () -> {
                        throw new CustomException(NOTIFICATION_NOT_FOUND);
                    });
    }

}
