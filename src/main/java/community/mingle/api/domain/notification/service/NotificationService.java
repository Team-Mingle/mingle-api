package community.mingle.api.domain.notification.service;

import community.mingle.api.domain.comment.repository.CommentRepository;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.repository.MemberRepository;
import community.mingle.api.domain.notification.entity.Notification;
import community.mingle.api.domain.notification.repository.NotificationRepository;
import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.enums.BoardType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static community.mingle.api.enums.BoardType.TOTAL;
import static community.mingle.api.enums.BoardType.UNIV;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final NotificationRepository notificationRepository;


    @Transactional
    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
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


    @Transactional
    public void cleanNotification(Member member) {
        if (member.getNotifications().size() > 20)
            notificationRepository.delete(member.getNotifications().get(0));
    }

}
