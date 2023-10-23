package community.mingle.api.domain.post.service;

import community.mingle.api.enums.*;
import community.mingle.api.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import community.mingle.api.domain.post.controller.response.PostCategoryResponse;


import java.util.*;
import java.util.stream.Collectors;

import static community.mingle.api.enums.ContentStatusType.*;
import static community.mingle.api.global.exception.ErrorCode.INTERNAL_SERVER_ERROR;
import static community.mingle.api.global.exception.ErrorCode.POST_NOT_EXIST;

@Service
@RequiredArgsConstructor
public class PostService {


    public List<PostCategoryResponse> getPostCategory(MemberRole memberRole) {
        return getCategoriesByMemberRole(memberRole).stream()
                .map(PostCategoryResponse::new)
                .collect(Collectors.toList());
    }

    public List<CategoryType> getCategoriesByMemberRole(MemberRole memberRole) {
        return switch (memberRole) {
            case ADMIN -> Arrays.asList(CategoryType.FREE, CategoryType.QNA, CategoryType.KSA, CategoryType.MINGLE);
            case KSA -> Arrays.asList(CategoryType.FREE, CategoryType.QNA, CategoryType.KSA);
            default -> Arrays.asList(CategoryType.FREE, CategoryType.QNA);
        };
    }



}

