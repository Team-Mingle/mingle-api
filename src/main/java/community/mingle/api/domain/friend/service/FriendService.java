package community.mingle.api.domain.friend.service;

import community.mingle.api.domain.friend.entity.Friend;
import community.mingle.api.domain.friend.entity.FriendCode;
import community.mingle.api.domain.friend.repository.FriendCodeRepository;
import community.mingle.api.domain.friend.repository.FriendRepository;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.point.event.EarningPointEvent;
import community.mingle.api.enums.PointEarningType;
import community.mingle.api.global.exception.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static community.mingle.api.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendCodeRepository friendCodeRepository;
    private final FriendRepository friendRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public Friend getById(Long friendId) {
        return friendRepository.findById(friendId).orElseThrow(() -> new CustomException(FRIEND_NOT_FOUND));
    }

    @Transactional
    public FriendCode createFriendCode(Member member, String myDisplayName) {

        String code = generateUniqueCode();
        FriendCode friendCode = FriendCode.builder()
                .member(member)
                .code(code)
                .displayName(myDisplayName)
                .expiresAt(LocalDateTime.now().plusDays(3L))
                .build();

        return friendCodeRepository.save(friendCode);
    }

    @Transactional
    public void createFriend(Member member, String friendCode, String myDisplayName) {
        FriendCode checkedFriendCode = checkFriendCode(friendCode, member);

        checkAlreadyExistingFriend(member, checkedFriendCode.getMember());
        Friend friend = Friend.builder()
                .member(member)
                .friend(checkedFriendCode.getMember())
                .friendName(checkedFriendCode.getDisplayName())
                .build();

        Friend reverseFriend = Friend.builder()
                .member(checkedFriendCode.getMember())
                .friend(member)
                .friendName(myDisplayName)
                .build();

        //LastDisplayName은 friendCode의 제일 최근 이름을 가져오고 있음
        //친구를 생성할 때의 myDisplayName도 LastDisplayName으로 사용하기 위해 임의의 friendCode를 만들어줌
        createFriendCode(member, myDisplayName);
        friendCodeRepository.delete(checkedFriendCode);
        friendRepository.save(friend);
        friendRepository.save(reverseFriend);

        applicationEventPublisher.publishEvent(new EarningPointEvent(this, PointEarningType.ADD_FRIEND, member.getId()));
        applicationEventPublisher.publishEvent(new EarningPointEvent(this, PointEarningType.ADD_FRIEND, friend.getFriend().getId()));
    }

    public List<Friend> listFriends(Member member) {
        return friendRepository.findAllByMember(member);
    }

    @Transactional
    public void deleteFriend(Member member, Long friendId) {
        Friend memberToFriendRelation = friendRepository.findById(friendId).orElseThrow(() -> new CustomException(FRIEND_NOT_FOUND));
        if (!member.equals(memberToFriendRelation.getMember())) {
            throw new CustomException(MEMBER_NOT_FRIEND);
        }
        Member friend = memberToFriendRelation.getFriend();
        Friend friendToMemberRelation = friendRepository.findByMemberAndFriend(friend, member).orElseThrow(() -> new CustomException(FRIEND_NOT_FOUND));
        friendRepository.delete(memberToFriendRelation);
        friendRepository.delete(friendToMemberRelation);
    }

    public void updateMemberName(Long friendId, String newFriendName) {
        Friend memberToFriendRelation = friendRepository.findById(friendId).orElseThrow(() -> new CustomException(FRIEND_NOT_FOUND));
        memberToFriendRelation.changeFriendName(newFriendName);
    }

    public String getMemberLastDisplayName(Member member) {
        Optional<String> memberLastDisplayName = friendCodeRepository.findMemberLastDisplayName(member.getId());
        return memberLastDisplayName.orElseGet(member::getNickname);
    }

    private FriendCode checkFriendCode(String friendCode, Member member) {
        FriendCode checkedFriendCode = friendCodeRepository.findByCode(friendCode)
                .orElseThrow(() -> new CustomException(FRIEND_CODE_NOT_FOUND));

        boolean isFriendCodeExpires = checkedFriendCode.getExpiresAt().isBefore(LocalDateTime.now());
        boolean isSelfGeneratedCode = checkedFriendCode.getMember().getId().equals(member.getId());

        if (isFriendCodeExpires) {
            friendCodeRepository.delete(checkedFriendCode);
            throw new CustomException(FRIEND_CODE_EXPIRED);
        }
        if (isSelfGeneratedCode) throw new CustomException(FRIEND_CODE_NOT_FOUND);

        return checkedFriendCode;
    }

    private void checkAlreadyExistingFriend(Member member, Member friend) {
        boolean isAlreadyExistingFriend = friendRepository.existsFriendByMemberAndFriend(member, friend);
        if (isAlreadyExistingFriend) {
            throw new CustomException(FRIEND_ALREADY_ADDED);
        }
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = generateRandomCode();
        } while (friendCodeRepository.findByCode(code).isPresent());

        return code;
    }

    private static String generateRandomCode() {
        Random random = new Random();
        return String.valueOf(random.nextInt(888888) + 111111);
    }
}
