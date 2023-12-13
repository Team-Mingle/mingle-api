package community.mingle.api.domain.friend.service;

import community.mingle.api.domain.friend.entity.Friend;
import community.mingle.api.domain.friend.entity.FriendCode;
import community.mingle.api.domain.friend.repository.FriendRepository;
import community.mingle.api.domain.friend.repository.FriendCodeRepository;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.global.exception.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static community.mingle.api.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendCodeRepository friendCodeRepository;
    private final FriendRepository friendRepository;

    @Transactional
    public FriendCode createFriendCode(Member member) {

        String code = generateRandomCode();
        FriendCode friendCode = FriendCode.builder()
                .member(member)
                .code(code)
                .expiresAt(LocalDateTime.now().plusDays(3L))
                .build();

        return friendCodeRepository.save(friendCode);
    }

    @Transactional
    public void createFriend(Member member, String friendCode, String friendName) {
        FriendCode checkedFriendCode = checkFriendCode(friendCode, member);

        checkAlreadyExistingFriend(member, checkedFriendCode.getMember());
        Friend friend = Friend.builder()
                .member(member)
                .friend(checkedFriendCode.getMember())
                .name(friendName)
                .build();

        Friend reverseFriend = Friend.builder()
                .member(checkedFriendCode.getMember())
                .friend(member)
                .name(checkedFriendCode.getMember().getNickname())
                .build();

        friendCodeRepository.delete(checkedFriendCode);
        friendRepository.save(friend);
        friendRepository.save(reverseFriend);
    }

    public List<Friend> listFriends(Member member) {
        return friendRepository.findAllByMember(member);
    }

    private FriendCode checkFriendCode(String friendCode, Member member) {
        FriendCode checkedFriendCode = friendCodeRepository.findByCode(friendCode)
                .orElseThrow(() -> new CustomException(FRIEND_CODE_NOT_FOUND));

        boolean isFriendCodeExpires = checkedFriendCode.getExpiresAt().isBefore(LocalDateTime.now());
        boolean isSelfGeneratedCode = checkedFriendCode.getMember().getId().equals(member.getId());

        if (isFriendCodeExpires) throw new CustomException(FRIEND_CODE_EXPIRED);
        if (isSelfGeneratedCode) throw new CustomException(FRIEND_CODE_NOT_FOUND);

        return checkedFriendCode;
    }

    private void checkAlreadyExistingFriend(Member member, Member friend) {
        boolean isAlreadyExistingFriend = friendRepository.existsFriendByMemberAndFriend(member, friend);
        if (isAlreadyExistingFriend) {
            throw new CustomException(FRIEND_ALREADY_ADDED);
        }
    }

    private static String generateRandomCode() {
        return UUID.randomUUID()
                .toString()
                .replaceAll("-", "");
    }
}
