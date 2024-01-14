package community.mingle.api.domain.notification.service;

import community.mingle.api.domain.comment.repository.CommentRepository;
import community.mingle.api.domain.item.entity.Item;
import community.mingle.api.domain.item.repository.ItemCommentRepository;
import community.mingle.api.domain.member.entity.Member;
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
import community.mingle.api.enums.BoardType;
import community.mingle.api.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static community.mingle.api.enums.BoardType.TOTAL;
import static community.mingle.api.enums.BoardType.UNIV;
import static community.mingle.api.global.exception.ErrorCode.NOTIFICATION_NOT_FOUND;

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

    public List<Member> getTargetTokenMembersByBoardType(BoardType boardType, Post post) {
        List<Member> tokenMembers = new ArrayList<>();
        if (boardType.equals(UNIV))
            tokenMembers = memberRepository.findAllByUniversity(post.getMember().getUniversity());
        if (boardType.equals(TOTAL))
            tokenMembers = memberRepository.findAllByUniversityCountry(post.getMember().getUniversity().getCountry());
        return tokenMembers.stream()
                .filter(member -> member.getFcmToken() != null)
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
