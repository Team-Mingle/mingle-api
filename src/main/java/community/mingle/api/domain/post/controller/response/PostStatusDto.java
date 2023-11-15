package community.mingle.api.domain.post.controller.response;

public record PostStatusDto(boolean isMyPost, boolean isLiked, boolean isScraped, boolean isBlinded) {
}
