package community.mingle.api.domain.member.facade;

import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.member.service.MemberService;
import community.mingle.api.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberFacade {

    private final PostService postService;
    private final TokenService tokenService;
    private final MemberService memberService;


}
