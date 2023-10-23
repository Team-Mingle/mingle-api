package community.mingle.api.domain.post.facade;

import community.mingle.api.domain.auth.service.TokenService;
import community.mingle.api.domain.post.controller.response.PostCategoryResponse;
import community.mingle.api.domain.post.service.PostService;
import community.mingle.api.dto.security.TokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostFacade {

    private final PostService postService;
    private final TokenService tokenService;


    /**
     * 게시물 카테고리 목록 조회
     */
    public List<PostCategoryResponse> getPostCategory(){
        TokenDto tokenInfo = tokenService.getTokenInfo();
        return postService.getPostCategory(tokenInfo.getMemberRole());
    }



}
