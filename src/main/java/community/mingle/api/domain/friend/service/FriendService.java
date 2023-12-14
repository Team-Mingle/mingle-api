package community.mingle.api.domain.friend.service;

import community.mingle.api.domain.friend.entity.FriendCode;
import community.mingle.api.domain.friend.repository.FriendCodeRepository;
import community.mingle.api.domain.member.entity.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendCodeRepository friendCodeRepository;

    @Transactional
    public FriendCode createFriendCode(Member member) {

        String code = generateUniqueCode();
        FriendCode friendCode = FriendCode.builder()
                .member(member)
                .code(code)
                .expiresAt(LocalDateTime.now().plusDays(3L))
                .build();

        return friendCodeRepository.save(friendCode);
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = generateRandomCode();
        } while (friendCodeRepository.findByCode(code).isPresent());

        return code;
    }

    private static String generateRandomCode() {
        return UUID.randomUUID()
                .toString()
                .replaceAll("-", "");
    }
}
