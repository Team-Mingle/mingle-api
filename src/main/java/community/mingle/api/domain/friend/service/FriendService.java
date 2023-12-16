package community.mingle.api.domain.friend.service;

import community.mingle.api.domain.friend.entity.Friend;
import community.mingle.api.domain.friend.entity.FriendCode;
import community.mingle.api.domain.friend.entity.FriendDisplayName;
import community.mingle.api.domain.friend.repository.FriendCodeRepository;
import community.mingle.api.domain.friend.repository.FriendDisplayNameRepository;
import community.mingle.api.domain.friend.repository.FriendRepository;
import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.global.exception.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
    private final FriendDisplayNameRepository friendDisplayNameRepository;

    @Transactional
    public FriendCode createFriendCode(Member member, String myDisplayName) {

        String code = generateUniqueCode();
        FriendCode friendCode = FriendCode.builder()
                .member(member)
                .code(code)
                .displayName(myDisplayName)
                .expiresAt(LocalDateTime.now().plusDays(3L))
                .build();

        updateLastDisplayName(member, myDisplayName);

        return friendCodeRepository.save(friendCode);
    }

    @Transactional
    public void createFriend(Member member, String friendCode, String myDisplayName) {
        FriendCode checkedFriendCode = checkFriendCode(friendCode, member);

        checkAlreadyExistingFriend(member, checkedFriendCode.getMember());
        Friend friend = Friend.builder()
                .member(member)
                .friend(checkedFriendCode.getMember())
                .name(checkedFriendCode.getDisplayName())
                .build();

        Friend reverseFriend = Friend.builder()
                .member(checkedFriendCode.getMember())
                .friend(member)
                .name(myDisplayName)
                .build();

        updateLastDisplayName(member, myDisplayName);

        friendCodeRepository.delete(checkedFriendCode);
        friendRepository.save(friend);
        friendRepository.save(reverseFriend);
    }

    public List<Friend> listFriends(Member member) {
        return friendRepository.findAllByMember(member);
    }

    public String getMemberLastDisplayName(Member member) {
        Optional<FriendDisplayName> lastDisplayName = friendDisplayNameRepository.findById(member.getId());
        if(lastDisplayName.isEmpty()) {
            return member.getNickname();
        } else {
            return lastDisplayName.get().getName();
        }
    }

    private void updateLastDisplayName(Member member, String displayName) {
        Optional<FriendDisplayName> lastDisplayName = friendDisplayNameRepository.findById(member.getId());
        if(lastDisplayName.isEmpty()) {
            FriendDisplayName friendDisplayName = FriendDisplayName.builder()
                    .member(member)
                    .name(displayName)
                    .build();
            friendDisplayNameRepository.save(friendDisplayName);
        } else {
            lastDisplayName.get().updateName(displayName);
        }
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
