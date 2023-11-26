package community.mingle.api.domain.notification.service;

import community.mingle.api.domain.member.entity.Member;
import community.mingle.api.domain.member.repository.MemberRepository;
import community.mingle.api.domain.post.entity.Post;
import community.mingle.api.enums.BoardType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static community.mingle.api.enums.BoardType.*;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final MemberRepository memberRepository;

    public List<String> findTargetUserTokensByBoardType(BoardType boardType, Post post) {
        List<Member> tokenMembers = new ArrayList<>();
        if (boardType.equals(UNIV))
            tokenMembers = memberRepository.findAllByUniversity(post.getMember().getUniversity());
        if (boardType.equals(TOTAL))
            tokenMembers = memberRepository.findAllByUniversityCountry(post.getMember().getUniversity().getCountry());
        return tokenMembers.stream()
                .filter(member -> member.getFcmToken() != null)
                .map(Member::getFcmToken)
                .collect(Collectors.toList());
    }

}
